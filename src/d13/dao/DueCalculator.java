package d13.dao;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

public class DueCalculator {

    public static class Tier {
        private DateTime end;
        private int      amount;
        private String   name;
        private Tier (DateTime end, int amount, String name) {
            this.end = end;
            this.amount = amount;
            this.name = name;
        }
        public String getName () {
            return name;
        }
        public int getAmount () {
            return amount;
        }
    }
    
    public static class Amount {
        private String tierName;
        private int amount;
        private Amount (Tier tier) { this.tierName = tier.name; this.amount = tier.amount; }
        public Amount (String name, int amount) { this.tierName = name; this.amount = amount; }
        public String getTierName () { return tierName; }
        public int getAmount () { return amount; }
    }
    
    private static final List<Tier> personalTiers = new ArrayList<Tier>();
    private static final Tier rvTier;
    private static final int GRACE_PERIOD_DAYS = 7;

    static {
        
        /*
        Tier 1: $325 if paid by Midnight on July 20th
        Tier 2: $375 if paid by Midnight July 27th
        Tier 3: $425 if paid by Midnight August 3rd
        Tier 4: $475 if paid after Midnight August 3rd
        */

        DateTimeZone tz = DateTimeZone.forID("America/New_York");
        personalTiers.add(new Tier(new DateTime(2013, 7, 21, 0, 0, 0, tz), 32500, "Tier 1"));
        personalTiers.add(new Tier(new DateTime(2013, 7, 28, 0, 0, 0, tz), 37500, "Tier 2"));
        personalTiers.add(new Tier(new DateTime(2013, 8, 4, 0, 0, 0, tz), 42500, "Tier 3"));
        personalTiers.add(new Tier(null, 47500, "Tier 4"));
        
        rvTier = new Tier(null, 75000, "R.V. Fee");
        
    }
   
    public static Amount calculateAmount (DateTime registered, DateTime approved, int type) {
        
        return calculateAmount(registered, approved, type, DateTime.now());
        
    }
    
    public static final int TYPE_PERSONAL = 0;
    public static final int TYPE_RV = 1;
    
    public static Amount calculateAmount (DateTime registered, DateTime approved, int type, DateTime now) {
        
        if (type == TYPE_RV)
            return new Amount(rvTier);
        else if (type == TYPE_PERSONAL) {
            Duration period = new Duration(approved, now);
            if (period.getStandardDays() < GRACE_PERIOD_DAYS)
                return new Amount(findPersonalTier(registered));
            else
                return new Amount(findPersonalTier(now));
        } else
            throw new IllegalArgumentException("Unknown due item type specified.");
        
    }
    
    public static List<Tier> getPersonalTiers (DateTime registered, DateTime approved, DateTime now) {
        
        List<Tier> tiers = new ArrayList<Tier>();
        
        Duration period = new Duration(approved, now);
        if (period.getStandardDays() < GRACE_PERIOD_DAYS)
            tiers.add(findPersonalTier(registered));
        else
            tiers.add(findPersonalTier(now));
        
        for (Tier tier:personalTiers)
            if (tier.amount > tiers.get(0).amount)
                tiers.add(tier);
        
        return tiers;
        
    }
    
    public static Tier getRVTier () {
        
        return rvTier;
        
    }
    
    private static Tier findPersonalTier (DateTime date) {
        
        for (Tier tier:personalTiers)
            if (tier.end == null || date.isBefore(tier.end))
                return tier;
        
        throw new IllegalStateException("Internal error. Last tier must have no end date.");
        
    }
    
}
