package d13.web;

import javax.servlet.jsp.PageContext;

import d13.dao.QueuedEmail;
import d13.dao.User;
import d13.dao.UserState;
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

            // TODO: error message if user already approved or rejected, to notify admins that an editing
            // conflict just occurred.
            
            if (!user.isApprovableBy2(session.getUser())) {
                failed = true;
                errorMessage = "Permission denied.";
                return; // permission denied
            }
          
            if (action_approve && user.getState() != UserState.APPROVED) {
                user.setState(UserState.APPROVED);
                QueuedEmail.queueNotification(QueuedEmail.TYPE_APPROVED, user);
            } else if (action_reject && user.getState() != UserState.REJECTED) { 
                user.setState(UserState.REJECTED);
                QueuedEmail.queueNotification(QueuedEmail.TYPE_REJECTED, user);
            }
            
        } else if (action_review) {
        
            // TODO: error message if already reviewed, same reason as above.
            
            if (!user.isReviewableBy2(session.getUser())) {
                failed = true;
                errorMessage = "Permission denied.";
                return; // permission denied
            }
           
            if (user.getState() == UserState.NEEDS_REVIEW) {
                user.setState(UserState.REGISTERED);
                QueuedEmail.queueNotification(QueuedEmail.TYPE_ACCEPTED, user);
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
