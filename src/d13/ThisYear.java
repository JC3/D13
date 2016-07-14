package d13;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import d13.dao.DueCalculator.Tier;


public class ThisYear {

    public static final int CAMP_YEAR = 2016;
    public static final String SYSTEM_VERSION = "v1.18f";
    
    /*
     * 2016
     * 
Tier 1: $395 if paid by 11:59 PM (Eastern Time) on July 15th
Tier 2: $545 if paid by 11:59 PM (Eastern Time) on July 25st
Tier 3: $665 if paid by 11:59 PM (Eastern Time) on August 16th

RV Fee Tier 1: $1050 if paid by 11:59 PM (Eastern Time) on July 15th
RV Fee Tier 2: $1250 if paid by 11:59 PM (Eastern Time) on July 25th
RV Fee Tier 3: $1550 if paid by 11:59 PM (Eastern Time) on August 16th
     */

    public static final int GRACE_PERIOD_DAYS = 3;

    public static void setupPersonalTiers (List<Tier> personalTiers) {

        DateTimeZone tz = DateTimeZone.forID("America/New_York");

        personalTiers.clear();        
        personalTiers.add(new Tier(new DateTime(2016, 7, 16, 0, 0, 0, tz), 39500, "Tier 1"));
        personalTiers.add(new Tier(new DateTime(2016, 7, 26, 0, 0, 0, tz), 54500, "Tier 2"));
        personalTiers.add(new Tier(null, 66500, "Tier 3"));
        
    }
    
    public static void setupRVTiers (List<Tier> rvTiers) {
        
        DateTimeZone tz = DateTimeZone.forID("America/New_York");

        rvTiers.clear();
        rvTiers.add(new Tier(new DateTime(2016, 7, 16, 0, 0, 0, tz), 105000, "R.V. Tier 1"));
        rvTiers.add(new Tier(new DateTime(2016, 7, 26, 0, 0, 0, tz), 125000, "R.V. Tier 2"));
        rvTiers.add(new Tier(null, 155000, "R.V. Tier 3"));
        
    }

}
