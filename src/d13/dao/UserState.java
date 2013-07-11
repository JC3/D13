package d13.dao;

public enum UserState {

    NEW_USER(0, "New User"),
    REGISTERED(1, "Needs Review"),
    APPROVED(2, "Approved"),
    REJECTED(3, "Rejected");
    
    private final int dbId;
    private final String displayString;
    
    private UserState (int dbId, String displayString) {
        this.dbId = dbId;
        this.displayString = displayString;
    }
    
    public int toDBId () {
        return dbId;
    }
    
    public static UserState fromDBId (int dbId) {
        switch (dbId) {
        case 0: return NEW_USER;
        case 1: return REGISTERED;
        case 2: return APPROVED;
        case 3: return REJECTED;
        default: return null;
        }
    }
    
    @Override public String toString () {
        return displayString;
    }
   
}
