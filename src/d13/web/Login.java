package d13.web;


import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;
import d13.util.Util;

public class Login {
    
    private LoginBean bean = new LoginBean();
    private boolean failed;
    private String errorMessage;
    private boolean loggedIn;
    
    public Login (PageContext context, SessionData session) {
        
        try {
            BeanUtils.populate(bean, context.getRequest().getParameterMap());
        } catch (Throwable t) {
            t.printStackTrace();
            failed = true;
            errorMessage = t.getMessage();
            return;
        }
        
        // if it's an existing user, log in
        if (bean.isExisting()) {
            try {
                session.login(bean.getEmail(), bean.getPassword());
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

    public String getEmail () {
        return bean.getEmail();
    }
    
    public boolean isExisting () {
        return bean.isExisting();
    }
    
}
