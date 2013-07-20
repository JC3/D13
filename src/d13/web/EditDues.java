package d13.web;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.DueItem;
import d13.dao.User;
import d13.dao.UserState;
import d13.util.Util;

public class EditDues {

    private CustomDuesBean bean = new CustomDuesBean();
    private boolean failed;
    private String errorMessage;
    private String failTarget;
    private String successTarget;

    public EditDues (PageContext context, SessionData session) {
        
        Long user_id = Util.getParameterLong(context.getRequest(), "user_id");    
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");

        try {
           
            BeanUtils.populate(bean, context.getRequest().getParameterMap());

            if (!session.isLoggedIn())
                throw new SecurityException("Permission denied.");
            if (user_id == null)
                throw new IllegalArgumentException("No user ID specified.");
            if (!session.getUser().getRole().canEditDues())
                throw new SecurityException("Permission denied.");

            User user = User.findById(user_id);
            if (user.getState() != UserState.APPROVED)
                throw new SecurityException("To set special dues, user must be Approved.");
            
            boolean personalCustom = "1".equals(bean.getPersonalCustom());
            boolean rvCustom = "1".equals(bean.getRvCustom());
            int personalAmount = -1;
            int rvAmount = -1;
            
            if (personalCustom) {
                try {
                    personalAmount = (int)(Float.parseFloat(bean.getPersonalAmount()) * 100.0f + 0.5f);
                } catch (Throwable t) {
                    throw new IllegalArgumentException("Invalid special camp fee.");
                }
                if (personalAmount < 0) throw new IllegalArgumentException("Invalid special camp fee value.");
            }
            
            if (rvCustom) {
                try {
                    rvAmount = (int)(Float.parseFloat(bean.getRvAmount()) * 100.0f + 0.5f);
                } catch (Throwable t) {
                    throw new IllegalArgumentException("Invalid special RV fee.");
                }
                if (rvAmount < 0) throw new IllegalArgumentException("Invalid special RV fee value.");
            }

            user.getPersonalDueItem().setCustomAmount(personalCustom ? personalAmount : DueItem.NO_CUSTOM_AMOUNT);
            user.getRvDueItem().setCustomAmount(rvCustom ? rvAmount : DueItem.NO_CUSTOM_AMOUNT);
            user.setCustomDueComments(bean.getExplain());
            
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
    
    public CustomDuesBean getCustomDuesBean () {
        return bean;
    }
    
    public boolean isFailed () {
        return failed;
    }
    
}
