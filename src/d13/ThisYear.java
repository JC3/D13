package d13;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import d13.dao.DueCalculator.Tier;


public class ThisYear {

    public static final int CAMP_YEAR = 2015;
    public static final String SYSTEM_VERSION = "v1.16";
    
    /*
     * 2015
     * 
     * Tier 1: $425 if paid by 11:59 PM (Eastern Time) on July 26th
Tier 2: $525 if paid by 11:59 PM (Eastern Time) on July 31st
Tier 3: $625 if paid by 11:59 PM (Eastern Time) on August 16th

RV Fee Tier 1: $950 if paid by 11:59 PM (Eastern Time) on July 26th
RV Fee Tier 2: $1050 if paid by 11:59 PM (Eastern Time) on July 31st
RV Fee Tier 3: $1150 if paid by 11:59 PM (Eastern Time) on August 16th
     */

    public static final int GRACE_PERIOD_DAYS = 3;

    public static void setupPersonalTiers (List<Tier> personalTiers) {

        DateTimeZone tz = DateTimeZone.forID("America/New_York");

        personalTiers.clear();        
        personalTiers.add(new Tier(new DateTime(2015, 7, 28, 0, 0, 0, tz), 42500, "Tier 1"));
        personalTiers.add(new Tier(new DateTime(2015, 8, 1, 0, 0, 0, tz), 52500, "Tier 2"));
        personalTiers.add(new Tier(null, 62500, "Tier 3"));
        
    }
    
    public static void setupRVTiers (List<Tier> rvTiers) {
        
        DateTimeZone tz = DateTimeZone.forID("America/New_York");

        rvTiers.clear();
        rvTiers.add(new Tier(new DateTime(2015, 7, 28, 0, 0, 0, tz), 95000, "R.V. Tier 1"));
        rvTiers.add(new Tier(new DateTime(2015, 8, 1, 0, 0, 0, tz), 105000, "R.V. Tier 2"));
        rvTiers.add(new Tier(null, 115000, "R.V. Tier 3"));
        
    }

}
