package d13.web;

import javax.servlet.jsp.PageContext;

import d13.dao.User;
import d13.dao.UserState;
import d13.notify.RegistrationEmail;
import d13.util.Util;

public class EditApproval {
    
    private boolean failed;
    private String errorMessage;
    private String successTarget;
    private String failTarget;
    
    public EditApproval (PageContext context, SessionData session) {

        if (!session.isLoggedIn()) {
            failed = true;
            errorMessage = "Permission denied.";
            return; // permission denied
        }
        
        Long user_id = Util.getParameterLong(context.getRequest(), "user_id");    
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");
       
        if (user_id == null) {
            failed = true;
            errorMessage = "User ID must be specified.";
            return; // missing user_id parameter
        }
        
        User user = null;
        try {
            user = User.findById(user_id);
        } catch (Throwable t) {
            failed = true;
            errorMessage = "User ID is incorrect.";
            return; // no such user
        }
   
        boolean action_approve = "approve".equalsIgnoreCase(context.getRequest().getParameter("action"));
        boolean action_reject = "reject".equalsIgnoreCase(context.getRequest().getParameter("action"));
        boolean action_review = "review".equalsIgnoreCase(context.getRequest().getParameter("action"));
        
        if (action_approve || action_reject) {

            if (!user.isApprovableBy(session.getUser())) {
                failed = true;
                errorMessage = "Permission denied.";
                return; // permission denied
            }
          
            if (action_approve)
                user.setState(UserState.APPROVED);
            else if (action_reject)
                user.setState(UserState.REJECTED);
            
        } else if (action_review) {
        
            if (!session.getUser().isAdmin()) { // !user.isEditableBy(session.getUser())) {
                failed = true;
                errorMessage = "Permission denied.";
                return; // permission denied
            }
           
            if (user.getState() == UserState.NEEDS_REVIEW) {
                user.setState(UserState.REGISTERED);
                RegistrationEmail.sendNotificationAdmissions(user);
            }
            
        } else {
    
            failed = true;
            errorMessage = "Incorrect action.";
            return; // invalid/missing action parameter
        
        }                    
    
    }
    
    public boolean isFailed () {
        return failed;
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public String getSuccessTarget () {
        return successTarget;
    }
    
    public String getFailTarget () {
        return failTarget;
    }

}
