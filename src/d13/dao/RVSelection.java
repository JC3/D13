package d13.dao;

public enum RVSelection {

    NOT_STAYING_IN(0, "No"),
    STAYING_IN(1, "Guest"),
    RESPONSIBLE(2, "Owner"),
    NEED_CLARIFICATION(3, "Need Clarification");
    
    private final int dbId;
    private final String displayString;
    
    private RVSelection (int dbId, String displayString) {
        this.dbId = dbId;
        this.displayString = displayString;
    }
    
    public int toDBId () {
        return dbId;
    }
    
    public static RVSelection fromDBId (int dbId) {
        switch (dbId) {
        case 0: return NOT_STAYING_IN;
        case 1: return STAYING_IN;
        case 2: return RESPONSIBLE;
        case 3: return NEED_CLARIFICATION;
        default: return null;
        }
    }
    
    @Override public String toString () {
        return displayString;
    }
   
}
