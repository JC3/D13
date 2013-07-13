package d13.web;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.RegistrationForm;
import d13.dao.User;
import d13.dao.UserState;
import d13.notify.ReviewNotificationEmail;
import d13.util.HibernateUtil;
import d13.util.Util;

public class EditRegistration {

    private RegistrationBean bean = new RegistrationBean();
    private boolean failed;
    private String errorMessage;
    private String failTarget;
    private String successTarget;

    public EditRegistration (PageContext context, SessionData session) {
        
        Long user_id = Util.getParameterLong(context.getRequest(), "user_id");    
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");

        try {
           
            BeanUtils.populate(bean, context.getRequest().getParameterMap());

            if (!session.isLoggedIn())
                throw new SecurityException("Permission denied.");
            if (user_id == null)
                throw new IllegalArgumentException("No user ID specified.");
            
            User editor = session.getUser();
            User editee = User.findById(user_id);
            if (!editee.isEditableBy(editor))
                throw new SecurityException("Permission denied.");
            
            RegistrationForm form = editee.getRegistration();
            boolean sendmail = false;
            
            // validate all before updating
            HibernateUtil.getCurrentSession().evict(form);
            HibernateUtil.getCurrentSession().evict(editee);
            BeanUtils.copyProperties(form, bean);
            form.validateMisc();
            if (!form.isCompleted())
                form.setCompletionTimeNow();
            if (editee.getState() == UserState.NEW_USER) {
                editee.setState(UserState.NEEDS_REVIEW);
                sendmail = true;
            }
            HibernateUtil.getCurrentSession().merge(editee);
            HibernateUtil.getCurrentSession().merge(form);
                        
            if (sendmail)
                ReviewNotificationEmail.sendNow(editee, User.findAdmins());
            
        } catch (InvocationTargetException x) {
            
            failed = true;
            errorMessage = x.getCause().getMessage();
            x.printStackTrace();
            
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
    
    public RegistrationBean getRegistrationBean () {
        return bean;
    }
    
    public boolean isFailed () {
        return failed;
    }
    
}
