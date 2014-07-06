package d13.dao;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import d13.ThisYear;

public class DueCalculator {

    public static class Tier {
        private DateTime end;
        private int      amount;
        private String   name;
        public Tier (DateTime end, int amount, String name) {
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
    private static final List<Tier> rvTiers = new ArrayList<Tier>();
    private static final int GRACE_PERIOD_DAYS = 7;

    static {
        
        /*
        Tier 1: $425 if paid by Midnight on July 7th
        Tier 2: $475 if paid by Midnight July 21st
        Tier 3: $525 if paid by after Midnight July 21st
        */

        ThisYear.setupPersonalTiers(personalTiers);
        ThisYear.setupRVTiers(rvTiers);
        
    }
   
    public static Amount calculateAmount (DateTime registered, DateTime approved, int type) {
        
        return calculateAmount(registered, approved, type, DateTime.now());
        
    }
    
    public static final int TYPE_PERSONAL = 0;
    public static final int TYPE_RV = 1;
    
    public static Amount calculateAmount (DateTime registered, DateTime approved, int type, DateTime now) {
        
        if (type == TYPE_RV) {
            Duration period = new Duration(approved, now);
            if (period.getStandardDays() < GRACE_PERIOD_DAYS)
                return new Amount(findRVTier(registered));
            else
                return new Amount(findRVTier(now));
        } else if (type == TYPE_PERSONAL) {
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
    
    public static List<Tier> getRVTiers (DateTime registered, DateTime approved, DateTime now) {
        
        List<Tier> tiers = new ArrayList<Tier>();
        
        Duration period = new Duration(approved, now);
        if (period.getStandardDays() < GRACE_PERIOD_DAYS)
            tiers.add(findRVTier(registered));
        else
            tiers.add(findRVTier(now));
        
        for (Tier tier:rvTiers)
            if (tier.amount > tiers.get(0).amount)
                tiers.add(tier);
        
        return tiers;
        
    }
    
    private static Tier findPersonalTier (DateTime date) {
        
        for (Tier tier:personalTiers)
            if (tier.end == null || date.isBefore(tier.end))
                return tier;
        
        throw new IllegalStateException("Internal error. Last tier must have no end date.");
        
    }
    
    private static Tier findRVTier (DateTime date) {
        
        for (Tier tier:rvTiers)
            if (tier.end == null || date.isBefore(tier.end))
                return tier;
        
        throw new IllegalStateException("Internal error. Last tier must have no end date.");
        
    }
    
}
