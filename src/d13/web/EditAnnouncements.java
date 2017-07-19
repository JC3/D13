package d13.web;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import d13.dao.GeneralLogEntry;
import d13.dao.RuntimeOptions;

public class EditAnnouncements {

    private boolean failed;
    private String errorMessage;
    private String failTarget;
    private String successTarget;

    public EditAnnouncements (PageContext context, SessionData session) {
        
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");

        try {

            if (!session.isLoggedIn())
                throw new SecurityException("Permission denied.");
            if (!session.getUser().getRole().canEditAnnouncements())
                throw new SecurityException("Permission denied.");

            String prevmessage = StringUtils.trimToNull(RuntimeOptions.Global.getLoggedInAnnouncement());            
            String message = StringUtils.trimToNull(context.getRequest().getParameter("message"));
            RuntimeOptions.Global.setLoggedInAnnouncement(message);
            
            if (!StringUtils.equals(prevmessage, message)) {
                if (message != null)
                    session.getUser().addGeneralLogEntry("Set site announcement.", message, GeneralLogEntry.TYPE_ANNOUNCEMENT);
                else
                    session.getUser().addGeneralLogEntry("Removed site announcement.", GeneralLogEntry.TYPE_ANNOUNCEMENT);
            }
            
        } catch (Throwable t) {
            
            failed = true;
            errorMessage = t.getMessage();            
            t.printStackTrace();
            
        }
        
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public String getFailTarget () {
        return failTarget;
    }
    
    public String getSuccessTarget () {
        return successTarget;
    }
    
    public boolean isFailed () {
        return failed;
    }
    

}
