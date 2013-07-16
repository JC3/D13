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
        boolean action_final_approve = "final_approve".equalsIgnoreCase(context.getRequest().getParameter("action"));
        boolean action_final_reject = "final_reject".equalsIgnoreCase(context.getRequest().getParameter("action"));
   
        UserState oldState = user.getState();
        
        if (action_final_approve || action_final_reject) {
        
            // TODO: error message if already finalized to reduce confusion on concurrent edits

            if (!user.isFinalizableBy(session.getUser())) {
                failed = true;
                errorMessage = "Permission denied.";
                return;
            }
            
            if (action_final_approve && user.getState() != UserState.APPROVED) {
                user.setState(UserState.APPROVED);
                user.setApprovedOnNowIfNotSet();
                QueuedEmail.queueNotification(QueuedEmail.TYPE_APPROVED, user);
            } else if (action_final_reject && user.getState() != UserState.REJECTED) { 
                user.setState(UserState.REJECTED);
                QueuedEmail.queueNotification(QueuedEmail.TYPE_REJECTED, user);
            }
         
        } else if (action_approve || action_reject) {

            // note: it's ok to change status from approval (pending) to reject (pending)
            // or vice versa.
                      
            if (!user.isApprovableBy2(session.getUser())) {
                failed = true;
                errorMessage = "Permission denied.";
                return; // permission denied
            }
            
            boolean notify = (user.getState() != UserState.APPROVE_PENDING && user.getState() != UserState.REJECT_PENDING);
            
            if (action_approve)
                user.setState(UserState.APPROVE_PENDING);
            else if (action_reject)
                user.setState(UserState.REJECT_PENDING);
            
            if (notify)
                QueuedEmail.queueNotification(QueuedEmail.TYPE_FINALIZE, user);
            
            //if (action_approve && user.getState() != UserState.APPROVED) {
            //    user.setState(UserState.APPROVED);
            //    QueuedEmail.queueNotification(QueuedEmail.TYPE_APPROVED, user);
            //} else if (action_reject && user.getState() != UserState.REJECTED) { 
            //    user.setState(UserState.REJECTED);
            //    QueuedEmail.queueNotification(QueuedEmail.TYPE_REJECTED, user);
            //}
            
        } else if (action_review) {
        
            // TODO: error message if already reviewed to reduce confusion on concurrent edits
            
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
    
        if (user.getState() != oldState)
            user.addStateActivityLogEntry(session.getUser(), oldState);
        
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
