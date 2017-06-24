package d13;

import java.net.URLEncoder;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import d13.dao.DueCalculator.Tier;


public class ThisYear {

    public static final int CAMP_YEAR = 2017;
    public static final String SYSTEM_VERSION = "v1.23";
    public static final String CSS_VERSION;
    public static final DateTime PLAYA_MONDAY = new DateTime(2017, 8, 28, 12, 0);
    
    static {
        String css_version = "";
        try {
            css_version = URLEncoder.encode(SYSTEM_VERSION, "us-ascii");
        } catch (Exception x) {
            // won't happen.
        }
        CSS_VERSION = css_version;
    }
    
    /*
     * 2017
     * 
       2017
       
Tier 1 $395 Camp Dues: 11:59 PM (Eastern Time) on Wednesday, July 12
Tier 2 $545 Camp Dues : 11:59 PM (Eastern Time) on Wednesday, July 26
Tier 3 $665 Camp Dues: 11:59 PM (Eastern Time) on Wednesday, August 16

Tier 1 $1050 RV Fee: Wednesday, July 12 at 11:59 PM (Eastern Time) 
Tier 2 $1250 RV Fee: Wednesdsay, July 26 at 11:59 PM (Eastern Time)  
Tier 3 $1550 RV Fee: Wednesday, August 16 at 11:59 PM (Eastern Time)
     */

    public static final int GRACE_PERIOD_DAYS = 3;

    public static void setupPersonalTiers (List<Tier> personalTiers) {

        DateTimeZone tz = DateTimeZone.forID("America/New_York");

        personalTiers.clear();        
        personalTiers.add(new Tier(new DateTime(2017, 7, 13, 0, 0, 0, tz), 39500, "Tier 1"));
        personalTiers.add(new Tier(new DateTime(2017, 7, 27, 0, 0, 0, tz), 54500, "Tier 2"));
        personalTiers.add(new Tier(null, 66500, "Tier 3"));
        
    }
    
    public static void setupRVTiers (List<Tier> rvTiers) {
        
        DateTimeZone tz = DateTimeZone.forID("America/New_York");

        rvTiers.clear();
        rvTiers.add(new Tier(new DateTime(2017, 7, 13, 0, 0, 0, tz), 105000, "R.V. Tier 1"));
        rvTiers.add(new Tier(new DateTime(2017, 7, 27, 0, 0, 0, tz), 125000, "R.V. Tier 2"));
        rvTiers.add(new Tier(null, 155000, "R.V. Tier 3"));
        
    }
    
    public static enum PlayaWeek {
        
        ALPHA(-1),
        FESTIVAL(0),
        DISENGAGE(1);
        
        final int offset;
        
        PlayaWeek (int offset) { 
            this.offset = offset;
        }

        public DateTime getDate (int daysFromMonday) {
            return PLAYA_MONDAY.plusWeeks(offset).plusDays(daysFromMonday);
        }
        
    }
    
}
