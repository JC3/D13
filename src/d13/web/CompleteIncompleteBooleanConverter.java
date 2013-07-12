package d13.web;

public class CompleteIncompleteBooleanConverter implements DataConverter {

    @Override public String asString (Object object) {
        if (object == null || !(object instanceof Boolean))
            return "";
        else
            return ((Boolean)object) ? "Complete" : "Incomplete";
    }

}
