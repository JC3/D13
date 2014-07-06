package d13;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import d13.dao.DueCalculator.Tier;


public class ThisYear {

    public static final int CAMP_YEAR = 2014;

    /*
     * So: 
Tier 1: $425 – If you pay your camp dues by July 14th at 11:59PM PT
Tier 2: $475 – If you pay your camp dues by July 24st at 11:59PM PT
Tier 3: $525 – If you pay your camp dues by August 4th at 11:59PM PT
Tier 4: $575 - If you pay your camp dues after  August 12th at 11:59PM PT
OMG TIER $625 - If you pay your camp dues after August 12th 
Any camper with unpaid camp dues after August 19th will NOT BE PLACED. 


RV Fees:
Tier 1: $850 - If you pay your RV fees by July 17th 
Tier 2: $900 - If you pay your RV fees by July  31st 
OMG TIER: $950 - If you pay your RV fees after July 31st 
     */
    
    public static void setupPersonalTiers (List<Tier> personalTiers) {

        DateTimeZone tz = DateTimeZone.forID("America/New_York");

        personalTiers.clear();        
        personalTiers.add(new Tier(new DateTime(2014, 7, 15, 0, 0, 0, tz), 42500, "Tier 1"));
        personalTiers.add(new Tier(new DateTime(2014, 7, 25, 0, 0, 0, tz), 47500, "Tier 2"));
        personalTiers.add(new Tier(new DateTime(2014, 8, 5, 0, 0, 0, tz), 52500, "Tier 3"));
        personalTiers.add(new Tier(new DateTime(2014, 8, 13, 0, 0, 0, tz), 57500, "Tier 4"));
        personalTiers.add(new Tier(null, 62500, "Tier 5"));
        
    }
    
    public static void setupRVTiers (List<Tier> rvTiers) {
        
        DateTimeZone tz = DateTimeZone.forID("America/New_York");

        rvTiers.clear();
        rvTiers.add(new Tier(new DateTime(2014, 7, 18, 0, 0, 0, tz), 85000, "R.V. Tier 1"));
        rvTiers.add(new Tier(new DateTime(2014, 8, 1, 0, 0, 0, tz), 90000, "R.V. Tier 2"));
        rvTiers.add(new Tier(null, 95000, "R.V. Tier 3"));
        
    }
    
}
