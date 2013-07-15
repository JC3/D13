package d13.dao;

public enum UserState {

    NEW_USER(0, "New User"),
    NEEDS_REVIEW(1, "Needs Review"),
    REGISTERED(2, "Registered"),
    APPROVED(3, "Approved"),
    REJECTED(4, "Rejected"),
    APPROVE_PENDING(5, "Approval Pending"),
    REJECT_PENDING(6, "Rejection Pending");
    
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
        case 1: return NEEDS_REVIEW;
        case 2: return REGISTERED;
        case 3: return APPROVED;
        case 4: return REJECTED;
        case 5: return APPROVE_PENDING;
        case 6: return REJECT_PENDING;
        default: return null;
        }
    }
    
    @Override public String toString () {
        return displayString;
    }
   
}
