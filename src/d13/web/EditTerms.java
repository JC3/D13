package d13.web;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import d13.dao.GeneralLogEntry;
import d13.dao.RuntimeOptions;
import d13.dao.User;

public class EditTerms {

    private boolean failed;
    private String errorMessage;
    private String message;
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
            User editor = session.getUser();
            if (!editor.getRole().canEditTerms())
                throw new SecurityException("Permission denied.");

            submittedTitle = StringUtils.trimToNull(context.getRequest().getParameter("terms_title"));
            submittedText = StringUtils.trimToNull(context.getRequest().getParameter("terms_text"));

            if (submittedTitle == null)
                throw new IllegalArgumentException("Title must not be blank.");
            if (submittedText == null)
                throw new IllegalArgumentException("Terms must not be blank.");
            
            String oldTitle = RuntimeOptions.Global.getTermsTitle();
            String oldText = RuntimeOptions.Global.getTermsText();
            
            if (!submittedTitle.equals(oldTitle))
                editor.addGeneralLogEntry("Updated terms title: " + submittedTitle, submittedTitle, GeneralLogEntry.TYPE_TERMS_TITLE);
            if (!submittedText.equals(oldText)) {
                int oldn = (oldText == null) ? 0 : oldText.length();
                int newn = submittedText.length();
                String lstr;
                if (newn < oldn)
                    lstr = String.format("Updated terms text: Removed %s character(s).", oldn - newn);
                else if (newn > oldn)
                    lstr = String.format("Updated terms text: Added %s character(s)", newn - oldn);
                else
                    lstr = "Updated terms text.";
                editor.addGeneralLogEntry(lstr, submittedText, GeneralLogEntry.TYPE_TERMS_TEXT);
            }
            
            RuntimeOptions.Global.setTermsTitle(submittedTitle);
            RuntimeOptions.Global.setTermsText(submittedText);
            
            switch ((int)(Math.random() * 2.0) % 2) {
                case 0:
                    message = "Behold, the virgin shall be with child, and bear HTML, and they shall call His name Updated Registration Terms.";
                    break;
                case 1:
                    message = "So the LORD said, \"I will wipe from the face of the earth the old registration text -- and with it the animals, the birds and the creatures that move along the ground -- for I have updated it.\"";
                    break;
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
    
    public String getMessage () {
        return message;
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
