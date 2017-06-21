package d13.changetrack;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import d13.web.DataView;
import d13.web.DefaultDataConverter;

public class TrackedState {

    private Map<String,String> data = new HashMap<String,String>();
    
    public TrackedState (Trackable t) {
        for (Field f : t.getClass().getDeclaredFields())
            if (f.isAnnotationPresent(Track.class))
                try {
                    f.setAccessible(true);
                    addData(f, f.get(t));
                } catch (Exception x) {
                    System.err.println("TrackedState: field " + f.getName() + " => " + x.getMessage());
                }
        for (Method m : t.getClass().getDeclaredMethods())
            if (m.isAnnotationPresent(Track.class))
                try {
                    m.setAccessible(true);
                    addData(m, m.invoke(t));
                } catch (Exception x) {
                    System.err.println("TrackedState: method " + m.getName() + " => " + x.getMessage());
                }
        data = Collections.unmodifiableMap(data);
    }
    
    private <T extends AnnotatedElement & Member> void addData (T m, Object value) {
        String name = StringUtils.trimToEmpty(m.getAnnotation(Track.class).value());
        if (name.isEmpty() && m.isAnnotationPresent(DataView.class))
            name = StringUtils.trimToEmpty(m.getAnnotation(DataView.class).n());
        if (name.isEmpty())
            name = m.getName();
        data.put(name, value == null ? null : DefaultDataConverter.objectAsString(value));
    }
    
    public Map<String,String> getData () {
        return data;
    }
    
}
