package d13.web;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import d13.dao.Gender;
import d13.dao.RVSelection;
import d13.dao.TicketSource;
import d13.dao.User;
import d13.dao.UserState;

public class DefaultDataConverter implements DataConverter {

    private static final DateTimeFormatter dtfNoSeconds = DateTimeFormat.forPattern("M/dd/yyyy hh:mm a");
    private static final DateTimeFormatter dtfSeconds = DateTimeFormat.forPattern("M/dd/yyyy hh:mm:ss a");
    private static final DateTimeZone tz = DateTimeZone.forID("America/New_York");
    
    private final boolean seconds;
    
    public DefaultDataConverter () {
        this(false);
    }
    
    public DefaultDataConverter (boolean withSeconds) {
        this.seconds = withSeconds;
    }
    
    @Override public String asString (Object object) {
        return objectAsString(object, seconds);
    }
    
    public static String objectAsString (Object object) {
        return objectAsString(object, false);
    }
    
    public static String objectAsString (Object object, boolean withSeconds) {
        if (object == null)
            return "";
        else if (object instanceof Boolean)
            return ((Boolean)object) ? "Yes" : "No";
        else if (object instanceof User)
            return ((User)object).getRealName();
        else if (object instanceof DateTime)
            return ((DateTime)object).withZone(tz).toString(withSeconds ? dtfSeconds : dtfNoSeconds);
        else if (object instanceof Gender)
            return ((Gender)object).toDisplayString();
        else if (object instanceof UserState)
            return ((UserState)object).toString();
        else if (object instanceof RVSelection)
            return ((RVSelection)object).toString();
        else if (object instanceof TicketSource)
            return ((TicketSource)object).toDisplayString();
        else
            return object.toString();
    }
    
}
