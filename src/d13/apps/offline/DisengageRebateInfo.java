package d13.apps.offline;

public class DisengageRebateInfo {
    
    String name;
    String email;
    boolean sunday, monday, tuesday, wednesday;
    
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RebateInfo [name=").append(name).append(", email=")
                .append(email).append(", sunday=").append(sunday)
                .append(", monday=").append(monday).append(", tuesday=")
                .append(tuesday).append(", wednesday=").append(wednesday)
                .append("]");
        return builder.toString();
    }
    
}