package d13.dao;

public enum TeeShirtSize {

    MS(11, "Men's S"),
    MM(12, "Men's M"),
    ML(13, "Men's L"),
    MXL(14, "Men's XL"),
    MXXL(15, "Men's XXL"),
    WS(21, "Women's S"),
    WM(22, "Women's M"),
    WL(23, "Women's L"),
    WXL(24, "Women's XL"),
    WXXL(25, "Women's XXL");
    
    private final int dbId;
    private final String displayString;
    
    private TeeShirtSize (int dbId, String displayString) {
        this.dbId = dbId;
        this.displayString = displayString;
    }
    
    public int toDBId () {
        return dbId;
    }
    
    public String toDisplayString () {
        return displayString;
    }
    
    public static TeeShirtSize fromDBId (int dbId) {
        switch (dbId) {
        case 11: return MS;
        case 12: return MM;
        case 13: return ML;
        case 14: return MXL;
        case 15: return MXXL;
        case 21: return WS;
        case 22: return WM;
        case 23: return WL;
        case 24: return WXL;
        case 25: return WXXL;
        default: return null;
        }
    }
    
    @Override public String toString () {
        return Integer.toString(dbId);
    }
    
}
