package d13.tests;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import d13.dao.DueCalculator;

public class DuesTest {

    public static void main(String[] args) throws Exception {

        DateTime dt;
        dt = new DateTime(2013, 7, 18, 0, 0, 0, DateTimeZone.forID("America/New_York"));
        System.out.println(dt);
        System.out.println(dt.withZone(DateTimeZone.forID("UTC")));
        
        System.out.println(dt.minusMinutes(30));
      
        DateTime reg = new DateTime(2013, 7, 1, 20, 42, 0, DateTimeZone.forID("America/New_York"));
        DateTime app = new DateTime(2013, 8, 1, 20, 42, 0, DateTimeZone.forID("America/New_York"));
        dt = new DateTime(2013, 7, 1, 0, 30, 0, DateTimeZone.forID("America/New_York"));
        for (int n = 0; n < 60; ++ n) {
            DateTime dto = dt.plusDays(n);
            DueCalculator.Amount amount = DueCalculator.calculateAmount(reg, app, DueCalculator.TYPE_PERSONAL, dto);
            System.out.println("  " + dto + " " + amount.getTierName() + " " + ((float)amount.getAmount() / 100.f));
        }
        
        /*
        HibernateUtil.beginTransaction();
        
        User user = User.findByEmail("newuser2@newuser.com");
        System.out.println("user state: " + user.getState());
        System.out.println("approval date: " + user.getApprovedOn());
        System.out.println("personal due: " + user.getPersonalDueItem());
        System.out.println("rv due: " + user.getRvDueItem());
        
        System.out.println("personal unpaid:");
        for (User u:User.findUnpaidPersonalDues())
            System.out.println("  " + u.getEmail());
        System.out.println("rv unpaid:");
        for (User u:User.findUnpaidRVDues())
            System.out.println("  " + u.getEmail());
        
        HibernateUtil.commitTransaction();
        */
        
    }

}
