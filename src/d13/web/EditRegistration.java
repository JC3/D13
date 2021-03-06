package d13.web;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Hibernate;

import d13.changetrack.Tracker;
import d13.dao.QueuedEmail;
import d13.dao.RegistrationForm;
import d13.dao.User;
import d13.dao.UserState;
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
            if (!editee.isEditableBy2(editor))
                throw new SecurityException("Permission denied.");
            
            if (editor.isInviteCodeNeeded())
                throw new IllegalArgumentException("Invite code needed.");
            
            RegistrationForm form = editee.getRegistration();
            boolean sendmail = false;

            HibernateUtil.getCurrentSession().saveOrUpdate(editee);
            form = editee.getRegistration();
            
            Tracker tracker = new Tracker(form);

            // validate all before updating
            editee.hibernateInitLogHacks();
            Hibernate.initialize(editee.getRvDueItemNoInit()); // hack forces init of rv due item which may be updated by registration form changes
            HibernateUtil.getCurrentSession().evict(form);
            HibernateUtil.getCurrentSession().evict(editee);
            BeanUtils.copyProperties(form, bean);
            // hack to produce error message for rv question change
            if (context.getRequest().getParameter("rvTypeId") == null ||
                context.getRequest().getParameter("rvTypeId").trim().isEmpty())
                throw new IllegalArgumentException("You must specify whether you are staying in an R.V. or not.");
            // end hack - can be removed once RVSelection.NEEDS_CLARIFICATION is no longer needed.
            form.validateMisc();
            if (!form.isCompleted())
                form.setCompletionTimeNow();
            if (editee.getState() == UserState.NEW_USER) {
                editee.setState(UserState.NEEDS_REVIEW);
                sendmail = true;
                // also this is a good place to implicitly accept invite if alwaysInvited (2016-jul-15)
                // todo: for some reason the comment add in acceptByEmail() yields a hibernate error: failed to lazily initialize a collection
                /*
                if (editee.getRole().isAlwaysInvited()) {
                    try {
                        Invite.acceptByEmail(editee);
                    } catch (Throwable t) {
                        System.err.println("INVITE WARN: When implicitly accepting invite for " + editee.getUserId() + " " + editee.getEmail() + ": " + t.getMessage());
                    }
                }
                */
            }
            editee = (User)HibernateUtil.getCurrentSession().merge(editee);
            //form = (RegistrationForm)HibernateUtil.getCurrentSession().merge(form);
            editee.addTrackerActivityLogEntry(editor, "Registration", tracker.compare(editee.getRegistration()), true);

            if (sendmail)
                QueuedEmail.queueNotification(QueuedEmail.TYPE_REVIEW, editee);
           
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
