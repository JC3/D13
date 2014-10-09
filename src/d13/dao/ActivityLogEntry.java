package d13.dao;

import java.util.List;

import org.joda.time.DateTime;

import d13.util.HibernateUtil;

public class ActivityLogEntry {
    
    private long entryId;
    private DateTime time;
    private User toWho;
    private User byWho;
    private String description;
   
    ActivityLogEntry () {      
    }
    
    public ActivityLogEntry (User who, String description) {
        this.time = DateTime.now();
        this.toWho = who;
        this.byWho = who;
        this.description = description;
    }
    
    public ActivityLogEntry (User who, String description, User by) {
        this.time = DateTime.now();
        this.toWho = who;
        this.byWho = by;
        this.description = description;
    }
    
    public long getEntryId () {
        return entryId;
    }
    
    public DateTime getTime () {
        return time;
    }
    
    public User getToWho () {
        return toWho;
    }
    
    public User getByWho () {
        return byWho;
    }
    
    public String getDescription () {
        return description;
    }

    public static List<ActivityLogEntry> findAll () {
        @SuppressWarnings("unchecked")
        List<ActivityLogEntry> entries = (List<ActivityLogEntry>)HibernateUtil.getCurrentSession()
                .createCriteria(ActivityLogEntry.class)
                .list();
        return entries;
    }
}
