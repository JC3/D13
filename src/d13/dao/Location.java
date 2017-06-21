package d13.dao;

public enum Location {

    NYC(1, "New York Area"),
    PHILLY(2, "Philly Area"),
    LA(3, "Los Angeles Area"),
    SF(4, "San Francisco Area"),
    SACRAMENTO(6, "Sacramento"),
    SEATTLE(7, "Seattle"),
    AUSTIN(9, "Austin"),
    RENO(10, "Reno"),
    DIRECT(11, "Flying to BRC", true), // For driving questions only, not profile
    OTHER(0, "Other");
    
    private final int dbId;
    private final String displayString;
    private final boolean hiddenInProfile;
    
    private Location (int dbId, String displayString) {
        this(dbId, displayString, false);
    }
    
    private Location (int dbId, String displayString, boolean hiddenInProfile) {
        this.dbId = dbId;
        this.displayString = displayString;
        this.hiddenInProfile = hiddenInProfile;
    }
    
    public int toDBId () {
        return dbId;
    }
    
    public String toDisplayString () {
        return displayString;
    }
    
    public boolean isHiddenInProfile () {
        return hiddenInProfile;
    }
    
    public static Location fromDBId (int dbId) {
        switch (dbId) {
        case 0: return OTHER;
        case 1: return NYC;
        case 2: return PHILLY;
        case 3: return LA;
        case 4: return SF;
        case 6: return SACRAMENTO;
        case 7: return SEATTLE;
        case 9: return AUSTIN;
        case 10: return RENO;
        default: return null;
        }
    }
    
    @Override public String toString () {
        return Integer.toString(dbId);
    }
    
}
