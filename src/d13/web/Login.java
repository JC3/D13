package d13.web;


import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;

import d13.InvalidLoginException;
import d13.dao.QueuedEmail;
import d13.dao.RuntimeOptions;
import d13.dao.User;
import d13.util.Util;

public class Login {
    
    private LoginBean bean = new LoginBean();
    private boolean failed;
    private String errorMessage;
    private boolean loggedIn;
    private boolean passwordResetRequest;
    
    public Login (PageContext context, SessionData session) {
        
        try {
            BeanUtils.populate(bean, context.getRequest().getParameterMap());
        } catch (Throwable t) {
            t.printStackTrace();
            failed = true;
            errorMessage = t.getMessage();
            return;
        }

        if (bean.isForgot()) {
            // forgotten password
            try {
                sendPasswordResetRequest();
            } catch (Throwable t) {
                failed = true;
                errorMessage = t.getMessage();
                return;
            }
            passwordResetRequest = true;
        } else if (bean.isExisting()) {
            // if it's an existing user, log in
            try {
                session.login(bean.getEmail(), bean.getPassword(), context.getRequest()); // <- checks maintenance mode
            } catch (InvalidLoginException lx) { // special case to enhance error message
                failed = true;
                errorMessage = lx.getMessage() + " If you are a new user select 'new user' below and you can set a password on the next page.";
                return;
            } catch (Throwable t) {
                failed = true;
                errorMessage = t.getMessage();
                return;
            }
            loggedIn = true;
        } else {
            // otherwise web interface will be creating a new user
            //String email = (bean.getEmail() == null ? "" : bean.getEmail().trim());
            //if (email.isEmpty()) {
            //    failed = true;
            //    errorMessage = "Email address must be specified.";
            //    return;
            //}
            try {
                Util.requireEmail(bean.getEmail(), "A valid email address");
                if (RuntimeOptions.Global.isMaintenanceMode()) // <- new user page checks as well but this gives better error feedback
                    throw new Exception("Sorry, the system is temporarily down for maintenance.");
                if (User.findByEmail(bean.getEmail()) != null)
                    throw new Exception("That email address is already in use. Do you have a password from a previous year?");
            } catch (Throwable t) {
                failed = true;
                errorMessage = t.getMessage();
                return;
            }
        }
        
    }
    
    public boolean isFailed () {
        return failed;
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public boolean isLoggedIn () {
        return loggedIn;
    }
    
    public boolean isPasswordResetRequest () {
        return passwordResetRequest;
    }

    public String getEmail () {
        return bean.getEmail();
    }
    
    public boolean isExisting () {
        return bean.isExisting();
    }

    private void sendPasswordResetRequest () {

        Util.requireEmail(bean.getEmail(), "A valid email address");
        
        User user = User.findByEmail(bean.getEmail());
        if (user != null) {
            System.out.println("PWRESET: Resetting for " + user.getEmail() + " : " + user.getUserId());
        } else {
            System.out.println("PWRESET: No such email: " + bean.getEmail() + " : Silently ignoring.");
            return;
        }

        // flood protection
        DateTime lastreset = user.getPasswordResetTime();
        if (lastreset != null && lastreset.plusMinutes(5).isAfterNow()) {
            System.out.println("PWRESET: Silent flood protection for " + user.getEmail() + " : " + user.getUserId());
            return;
        }
        
        // we're go for reset so set up code and links then send email
        user.activatePasswordReset();
        QueuedEmail.queueNotification(QueuedEmail.TYPE_PWRESET, user);

    }
    
}
