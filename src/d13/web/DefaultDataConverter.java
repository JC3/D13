package d13.web;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import d13.dao.Gender;
import d13.dao.RVSelection;
import d13.dao.User;
import d13.dao.UserState;

public class DefaultDataConverter implements DataConverter {

    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("M/dd/yyyy KK:mm a");
    private static final DateTimeZone tz = DateTimeZone.forID("America/New_York");
    
    @Override public String asString (Object object) {
        if (object == null)
            return "";
        else if (object instanceof Boolean)
            return ((Boolean)object) ? "Yes" : "No";
        else if (object instanceof User)
            return ((User)object).getRealName();
        else if (object instanceof DateTime)
            return ((DateTime)object).withZone(tz).toString(dtf);
        else if (object instanceof Gender)
            return ((Gender)object).toDisplayString();
        else if (object instanceof UserState)
            return ((UserState)object).toString();
        else if (object instanceof RVSelection)
            return ((RVSelection)object).toString();
        else
            return object.toString();
    }
    
}
