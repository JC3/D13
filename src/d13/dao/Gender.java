package d13.dao;

public enum Gender {

    MALE("m", "Male"),
    FEMALE("f", "Female");
    
    private final String dbString;
    private final String displayString;
    
    private Gender (String dbString, String displayString) {
        this.dbString = dbString;
        this.displayString = displayString;
    }
    
    public String toDBString () {
        return dbString;
    }
    
    public String toDisplayString () {
        return displayString;
    }
    
    public static Gender fromDBString (String dbString) {
        if ("m".equalsIgnoreCase(dbString))
            return MALE;
        else if ("f".equalsIgnoreCase(dbString))
            return FEMALE;
        else
            return null;
    }
    
    @Override public String toString () {
        return dbString;
    }
    
}
