package d13.changetrack;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class Tracker {

    public static class Change {
        private final String key;
        private final String prev;
        private final String curr;
        Change (String key, String prev, String curr) { this.key = key; this.prev = prev; this.curr = curr; }
        public String getKey () { return key; }
        public String getPrev () { return prev; }
        public String getCurr () { return curr; }
        @Override public String toString () {
            return String.format("%s: %s => %s", key, prev, curr);
        }
    }
    
    private final TrackedState aState;
    
    public Tracker (Trackable a) {
        aState = new TrackedState(a);
    }
    
    public Map<String,Change> compare (Trackable b) {
    
        Map<String,String> ad = aState.getData();
        Map<String,String> bd = new TrackedState(b).getData();
        Map<String,Change> changes = new HashMap<String,Change>();
        
        for (Map.Entry<String,String> e : ad.entrySet()) {
            String aValue = e.getValue();
            String bValue = bd.get(e.getKey());
            if (!StringUtils.equalsIgnoreCase(aValue, bValue))
                changes.put(e.getKey(), new Change(e.getKey(), aValue, bValue));
        }
        
        for (Map.Entry<String,String> e : bd.entrySet()) {
            if (!ad.containsKey(e.getKey()))
                changes.put(e.getKey(), new Change(e.getKey(), null, e.getValue()));
        }
        
        //for (Change c : changes.values()) {
        //    System.out.println(c);
        //}
        
        return changes;
        
    }
    
}
