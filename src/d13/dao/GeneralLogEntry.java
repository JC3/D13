package d13.dao;

import java.util.List;

import org.joda.time.DateTime;

import d13.util.HibernateUtil;

public class GeneralLogEntry {
    
    public static final int TYPE_TERMS_TITLE = 0;
    public static final int TYPE_TERMS_TEXT = 1;
    public static final int TYPE_EMAIL_TEMPLATE = 2;
    public static final int TYPE_ANNOUNCEMENT = 3;
    
    private long entryId;
    private DateTime time;
    private User byWho;
    private String summary;
    private String detail;
    private int type;
   
    GeneralLogEntry () {      
    }
   
    public GeneralLogEntry (String summary, String detail, int type, User by) {
        this.time = DateTime.now();
        this.byWho = by;
        this.summary = summary;
        this.detail = detail;
        this.type = type;
    }
    
    public long getEntryId () {
        return entryId;
    }
    
    public DateTime getTime () {
        return time;
    }
    
    public User getByWho () {
        return byWho;
    }
    
    public String getSummary () {
        return summary;
    }
    
    public String getDetail () {
        return detail;
    }
    
    public int getType () {
        return type;
    }

    public static List<GeneralLogEntry> findAll () {
        @SuppressWarnings("unchecked")
        List<GeneralLogEntry> entries = (List<GeneralLogEntry>)HibernateUtil.getCurrentSession()
                .createCriteria(GeneralLogEntry.class)
                .list();
        return entries;
    }
}
