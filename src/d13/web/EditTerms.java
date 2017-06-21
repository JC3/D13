package d13.web;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import d13.dao.RuntimeOptions;

public class EditTerms {

    private boolean failed;
    private String errorMessage;
    private String failTarget;
    private String successTarget;
    private String submittedTitle;
    private String submittedText;

    public EditTerms (PageContext context, SessionData session) {
        
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");

        try {

            if (!session.isLoggedIn())
                throw new SecurityException("Permission denied.");
            if (!session.getUser().getRole().canEditTerms())
                throw new SecurityException("Permission denied.");

            submittedTitle = StringUtils.trimToNull(context.getRequest().getParameter("terms_title"));
            submittedText = StringUtils.trimToNull(context.getRequest().getParameter("terms_text"));

            if (submittedTitle == null)
                throw new IllegalArgumentException("Title must not be blank.");
            if (submittedText == null)
                throw new IllegalArgumentException("Terms must not be blank.");
            
            RuntimeOptions.Global.setTermsTitle(submittedTitle);
            RuntimeOptions.Global.setTermsText(submittedText);
            
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
    
    public String getSubmittedTitle () {
        return submittedTitle == null ? "" : submittedTitle;
    }
    
    public String getSubmittedText () {
        return submittedText == null ? "" : submittedText;
    }

}
