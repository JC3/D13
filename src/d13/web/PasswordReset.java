package d13.web;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;

public class PasswordReset {

    public static final int RESULT_OK = 0; // should redirect to index with success message
    public static final int RESULT_DENIED = 1; // should redirect to index silently
    public static final int RESULT_FAILED = 2; // should return to form with error message
    public static final int RESULT_EXPIRED = 3; // should redirect to index with error message
    
    public static final String EXPIRATION_INDEX_MESSAGE = "Your password reset link has expired. Your password has not been changed. Please click 'I forgot my password' below to send a new reset link email.";
    
    private PasswordResetBean bean = new PasswordResetBean();
    private int result = RESULT_DENIED;
    private String failureMessage;
    private String email;
    private String key;
    
    public PasswordReset (PageContext context, SessionData session) {
        
        try {
            
            result = RESULT_DENIED;
            
            BeanUtils.populate(bean, context.getRequest().getParameterMap());
            key = bean.getKey();
    
            if (bean.getUser() == null || bean.getKey() == null || bean.getPassword() == null || bean.getPassword2() == null)
                throw new IllegalArgumentException("Invalid request.");
                
            User user = User.findById(bean.getUser());
            if (user == null)
                throw new IllegalArgumentException("Invalid request.");
            
            email = user.getEmail();
            
            if (/*bean.getKey() == null ||*/ user.getPasswordResetKey() == null || !bean.getKey().trim().equals(user.getPasswordResetKey()))
                throw new IllegalArgumentException("Invalid request.");
            
            if (user.isPasswordResetTimeExpired()) {
                result = RESULT_EXPIRED;
                throw new IllegalArgumentException("Password reset key expired.");
            }

            result = RESULT_FAILED;
            
            if (bean.getPassword().equals("")) {
                failureMessage = "Password must be specified.";
                return;
            }
            
            if (!bean.getPassword().equals(bean.getPassword2())) {
                failureMessage = "Confirmation password does not match.";
                return;
            }
            
            try {
                user.setPassword(bean.getPassword());
            } catch (IllegalArgumentException x) {
                failureMessage = x.getMessage();
                return;
            }
            
            user.clearPasswordReset();
            
            result = RESULT_OK;
            
        } catch (Throwable t) {
            
            t.printStackTrace();
            return;
            
        }
        
    }

    public int getResult () {
        return result;
    }
    
    public String getFailureMessage () {
        return failureMessage;
    }

    public String getEmail () {
        return email;
    }
    
    public String getKey () {
        return key;
    }
    
}
