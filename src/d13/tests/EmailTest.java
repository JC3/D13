package d13.tests;

import java.util.ArrayList;
import java.util.List;

import d13.dao.User;
import d13.notify.AcceptedNotificationEmail;
import d13.notify.Email.Configuration;
import d13.notify.ReviewNotificationEmail;
import d13.util.HibernateUtil;

public class EmailTest {

    public static final void main (String[] args) throws Throwable {
        
        HibernateUtil.beginTransaction();
        
        User user = User.findByEmail("test-all@yopmail.com");
        List<User> r = new ArrayList<User>();
        r.add(User.findByEmail("jason.cipriani@gmail.com"));
        
        Configuration c = Configuration.fromDatabase(HibernateUtil.getCurrentSession());
        
        AcceptedNotificationEmail.sendNow(user, r, c);
        ReviewNotificationEmail.sendNow(user, r, c);
        HibernateUtil.commitTransaction();
        
    }
    
}
