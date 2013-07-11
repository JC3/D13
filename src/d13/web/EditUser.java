package d13.web;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.Gender;
import d13.dao.Location;
import d13.dao.User;
import d13.util.HibernateUtil;
import d13.util.Util;


public class EditUser {

    private UserProfileBean bean = new UserProfileBean();
    private boolean failed;
    private String errorMessage;
    private String failTarget;
    private String successTarget;
    
    public EditUser (PageContext context, SessionData session) {
        
        // ?action=(create|update)[&user_id=(id)]&redirect=(url)&(form fields)
        
        boolean create = "create".equalsIgnoreCase(context.getRequest().getParameter("action"));
        boolean update = "update".equalsIgnoreCase(context.getRequest().getParameter("action"));
        boolean nologin = "1".equalsIgnoreCase(context.getRequest().getParameter("nologin"));
        Long user_id = Util.getParameterLong(context.getRequest(), "user_id");
        
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");
        
        try {

            BeanUtils.populate(bean, context.getRequest().getParameterMap());
            
            if (create) {
                
                // add the user to the database
                User user = new User(bean.getEmail());
                user.setPassword(bean.getPassword());
                //if (!bean.getPassword().equals(bean.getPassword2()))
                //    throw new InvalidParameterException("password2", "Confirmation password does not match.");
                user.setRealName(bean.getRealName());
                user.setPlayaName(bean.getPlayaName());
                user.setGender(Gender.fromDBString(bean.getGender()));
                user.setPhone(bean.getPhone());
                try {
                    int lid = Integer.parseInt(bean.getLocation());
                    user.setLocation(Location.fromDBId(lid));
                } catch (Throwable t) {
                    throw new IllegalArgumentException("Location must be specified.");
                }
                user.setLocationOther(bean.getLocationOther());
                user.setEmergencyContact(bean.getEmergencyContact());
                User.addUser(user);
                
                // then login as that user
                if (!nologin)
                    session.login(user.getEmail(), bean.getPassword());
                
            } else if (update) {
                
                if (!session.isLoggedIn())
                    throw new SecurityException("Permission denied.");
                if (user_id == null)
                    throw new IllegalArgumentException("User ID not specified.");
               
                User editor = session.getUser();
                User editee = User.findById(user_id);

                if (!editee.isEditableBy(editor))
                    throw new SecurityException("Permission denied.");
                
                // kludge to give reasonable error message if email address exists
                String oldemail = editee.getEmail().trim();
                String newemail = (bean.getEmail() == null ? "" : bean.getEmail().trim());
                if (!oldemail.equalsIgnoreCase(newemail) && User.findByEmail(newemail) != null) 
                    throw new IllegalArgumentException("This email address is already in use.");

                // validate all before updating
                HibernateUtil.getCurrentSession().evict(editee);
                editee.setEmail(bean.getEmail());
                if (bean.getPassword() != null && !bean.getPassword().isEmpty()) { // change pw if specified
                   // if (!bean.getPassword().equals(bean.getPassword2()))
                   //     throw new InvalidParameterException("password2", "Confirmation password does not match.");
                    editee.setPassword(/*editor, bean.getOldpassword(),*/ bean.getPassword());
                }
                editee.setRealName(bean.getRealName());
                editee.setPlayaName(bean.getPlayaName());
                editee.setGender(Gender.fromDBString(bean.getGender()));
                editee.setPhone(bean.getPhone());
                try {
                    int lid = Integer.parseInt(bean.getLocation());
                    editee.setLocation(Location.fromDBId(lid));
                } catch (Throwable t) {
                    throw new IllegalArgumentException("Location must be specified.");
                }
                editee.setLocationOther(bean.getLocationOther());
                editee.setEmergencyContact(bean.getEmergencyContact());
                HibernateUtil.getCurrentSession().merge(editee);
                                     
            } else {
                
                throw new IllegalArgumentException("Invalid action.");
                
            }
            
        } catch (Throwable t) {
            failed = true;
            errorMessage = t.getMessage();
            return;
        }
        
    }
    
    public boolean isFailed () {
        return failed;
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public UserProfileBean getUserProfileBean () {
        return bean;
    }
 
    public String getFailTarget () {
        return failTarget;
    }
    
    public String getSuccessTarget () {
        return successTarget;
    }
    
}