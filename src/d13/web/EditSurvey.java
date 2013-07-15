package d13.web;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.ApprovalSurvey;
import d13.dao.User;
import d13.dao.UserState;
import d13.util.HibernateUtil;
import d13.util.Util;

public class EditSurvey {

    private SurveyBean bean = new SurveyBean();
    private boolean failed;
    private String errorMessage;
    private String failTarget;
    private String successTarget;

    public EditSurvey (PageContext context, SessionData session) {
        
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
            if (!editee.isEditableBy2(editor))
                throw new SecurityException("Permission denied.");
            if (editee.getState() != UserState.APPROVED)
                throw new SecurityException("Permission denied.");
            
            ApprovalSurvey form = editee.getApproval();
            
            // validate all before updating
            HibernateUtil.getCurrentSession().evict(form);
            HibernateUtil.getCurrentSession().evict(editee);
            BeanUtils.copyProperties(form, bean);
            form.validateMisc();
            if (!form.isCompleted())
                form.setCompletionTimeNow();
            HibernateUtil.getCurrentSession().merge(editee);
            HibernateUtil.getCurrentSession().merge(form);
                        
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
    
    public SurveyBean getSurveyBean () {
        return bean;
    }
    
    public boolean isFailed () {
        return failed;
    }
    
}
