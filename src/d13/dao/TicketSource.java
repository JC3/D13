package d13.dao;

public enum TicketSource {
    
    REGULAR(0, "Regular"),
    DGS(1, "Group Sale"),
    NONE(2, "I don't have a ticket.");
    
    private final int dbId;
    private final String displayString;
    
    private TicketSource (int dbId, String displayString) {
        this.dbId = dbId;
        this.displayString = displayString;
    }
    
    public int toDBId () {
        return dbId;
    }
    
    public String toDisplayString () {
        return displayString;
    }
    
    public static TicketSource fromDBId (int dbId) {
        switch (dbId) {
        case 0: return REGULAR;
        case 1: return DGS;
        case 2: return NONE;
        default: return null;
        }
    }
    
    @Override public String toString () {
        return Integer.toString(dbId);
    }

}
