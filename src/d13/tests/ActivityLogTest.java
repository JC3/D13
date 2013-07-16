package d13.tests;

import d13.dao.ActivityLogEntry;
import d13.dao.User;
import d13.util.HibernateUtil;

public class ActivityLogTest {

    public static void main(String[] args) throws Exception {

        HibernateUtil.beginTransaction();
        User user = User.findByEmail("jason.cipriani@gmail.com");
        System.out.println("current log:");
        for (ActivityLogEntry e:user.getActivityLog())
            System.out.println("  id: " + e.getEntryId());        
        HibernateUtil.commitTransaction();
        
        HibernateUtil.beginTransaction();
        user = User.findByEmail("jason.cipriani@gmail.com");
        user.addActivityLogEntry(new ActivityLogEntry(user, "tested log"));
        user.addActivityLogEntry(new ActivityLogEntry(user, "tested log again"));
        user.addActivityLogEntry(new ActivityLogEntry(user, "tested log and again"));
        HibernateUtil.commitTransaction();

    }

}
