package d13.dao;

import java.util.List;

import org.joda.time.DateTime;

import d13.util.HibernateUtil;

public class ActivityLogEntry {
    
    public static final int TYPE_REVIEW = 0;
    public static final int TYPE_EDIT = 1;
    public static final int TYPE_DELETE_CELL = 2;
    
    private long entryId;
    private DateTime time;
    private User toWho;
    private User byWho;
    private String description;
    private int type;
   
    ActivityLogEntry () {      
    }
    
    public ActivityLogEntry (User who, String description, int type) {
        this.time = DateTime.now();
        this.toWho = who;
        this.byWho = who;
        this.description = description;
        this.type = type;
    }
    
    public ActivityLogEntry (User who, String description, int type, User by) {
        this.time = DateTime.now();
        this.toWho = who;
        this.byWho = by;
        this.description = description;
        this.type = type;
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
    
    public int getType () {
        return type;
    }

    public static List<ActivityLogEntry> findAll () {
        @SuppressWarnings("unchecked")
        List<ActivityLogEntry> entries = (List<ActivityLogEntry>)HibernateUtil.getCurrentSession()
                .createCriteria(ActivityLogEntry.class)
                .list();
        return entries;
    }
}
