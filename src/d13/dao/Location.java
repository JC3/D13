package d13.dao;

public enum Location {

    NYC(1, "New York Area"),
    PHILLY(2, "Philly Area"),
    LA(3, "Los Angeles Area"),
    SF(4, "San Francisco Area"),
    NEW_ENGLAND(5, "New England"),
    OTHER(0, "Other");
    
    private final int dbId;
    private final String displayString;
    
    private Location (int dbId, String displayString) {
        this.dbId = dbId;
        this.displayString = displayString;
    }
    
    public int toDBId () {
        return dbId;
    }
    
    public String toDisplayString () {
        return displayString;
    }
    
    public static Location fromDBId (int dbId) {
        switch (dbId) {
        case 0: return OTHER;
        case 1: return NYC;
        case 2: return PHILLY;
        case 3: return LA;
        case 4: return SF;
        case 5: return NEW_ENGLAND;
        default: return null;
        }
    }
    
    @Override public String toString () {
        return Integer.toString(dbId);
    }
    
}
