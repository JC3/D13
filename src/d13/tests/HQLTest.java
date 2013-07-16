package d13.tests;

import org.hibernate.Session;

import d13.dao.User;
import d13.util.HibernateUtil;

public class HQLTest {

    //@SuppressWarnings("unchecked")
    public static final void main (String[] args) throws Throwable {
        
        HibernateUtil.beginTransaction();
        
        Session s = HibernateUtil.getCurrentSession();

        /*Query q = s.createQuery("from User as user where user.role.viewUsers = true");
        List<User> x = q.list();
        for (User o:x)
            System.out.println(o.getEmail());*/
        
        System.out.println("reviewUsers:");
        for (User u:User.findReviewers(s))
            System.out.println("  " + u.getEmail() + " - " + u.getRoleDisplay());
        System.out.println("admitUsers:");
        for (User u:User.findAdmissions(s))
            System.out.println("  " + u.getEmail() + " - " + u.getRoleDisplay());
        System.out.println("finalizeUsers:");
        for (User u:User.findFinalizers(s))
            System.out.println("  " + u.getEmail() + " - " + u.getRoleDisplay());
        
        HibernateUtil.commitTransaction();
        
    }
    
}
