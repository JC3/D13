package d13.tests;

import d13.dao.User;
import d13.notify.RegistrationEmail;
import d13.util.HibernateUtil;

public class EmailTest {

    public static final void main (String[] args) throws Throwable {
        
        HibernateUtil.beginTransaction();
        User user = User.findByEmail("test-all@yopmail.com");
        RegistrationEmail.sendNotification(user);
        HibernateUtil.commitTransaction();
        
    }
    
}
