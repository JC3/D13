package d13.dao;

import java.util.List;

import org.joda.time.DateTime;

import d13.util.HibernateUtil;

public class RawIPNLogEntry {

    private long entryId;
    private DateTime received;
    private String data;
    private String reply;
    
    RawIPNLogEntry () {
    }
    
    public RawIPNLogEntry (String data, String reply) {
        this.received = DateTime.now();
        this.data = data;
        this.reply = reply;
    }
    
    public long getEntryId () {
        return entryId;
    }
    
    public DateTime getReceived () {
        return received;
    }
    
    public String getData () {
        return data;
    }
    
    public String getReply () {
        return reply;
    }
    
    @Override public String toString () {
        return data == null ? "" : data;
    }
    
    public static void addEntry (String data, String reply) {
        if (data != null) {
            RawIPNLogEntry entry = new RawIPNLogEntry(data, reply);
            try {
                HibernateUtil.getCurrentSession().persist(entry);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
   
    @SuppressWarnings("unchecked")
    public static List<RawIPNLogEntry> findAll () {
        return (List<RawIPNLogEntry>)HibernateUtil.getCurrentSession()
                .createCriteria(RawIPNLogEntry.class)
                .list();
    }
    
}
