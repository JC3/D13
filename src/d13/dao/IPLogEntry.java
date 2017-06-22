package d13.dao;

import org.joda.time.DateTime;

public class IPLogEntry {
    
    private long id;
    private User user;
    private DateTime firstSeen;
    private DateTime lastSeen;
    private String ip;
    
    IPLogEntry () {
    }
    
    public IPLogEntry (User user, String ip) {
        this.user = user;
        this.ip = ip;
        this.see();
    }
    
    public void see () {
        DateTime now = DateTime.now();
        if (firstSeen == null || now.isBefore(firstSeen))
            firstSeen = now;
        if (lastSeen == null || now.isAfter(lastSeen))
            lastSeen = now;
    }

    public long getId () {
        return id;
    }
    
    public User getUser () {
        return user;
    }
    
    public DateTime getFirstSeen () {
        return firstSeen;
    }
    
    public DateTime getLastSeen () {
        return lastSeen;
    }
    
    public String getIp () {
        return ip;
    }
    
}
