package d13.dao;

public enum UserState {

    NEW_USER(0, "New User", 0),
    NEEDS_REVIEW(1, "Needs Review", 1),
    REGISTERED(2, "Registered", 2),
    APPROVED(3, "Approved", 5),
    REJECTED(4, "Rejected", 6),
    APPROVE_PENDING(5, "Approval Pending", 3),
    REJECT_PENDING(6, "Rejection Pending", 4);
    
    private final int dbId;
    private final String displayString;
    private final int sortOrder;
    
    private UserState (int dbId, String displayString, int sortOrder) {
        this.dbId = dbId;
        this.displayString = displayString;
        this.sortOrder = sortOrder;
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
    
    public int compareToOrdered (UserState o) {
        int a = sortOrder;
        int b = (o == null ? -1 : o.sortOrder);
        return Integer.compare(a, b);
    }
    
    @Override public String toString () {
        return displayString;
    }
   
}
