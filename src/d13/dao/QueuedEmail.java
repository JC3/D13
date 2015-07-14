package d13.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.joda.time.DateTime;

import d13.util.HibernateUtil;

public class QueuedEmail {

    public static final int TYPE_REVIEW = 1;
    public static final int TYPE_ACCEPTED = 2;
    public static final int TYPE_APPROVED = 3;
    public static final int TYPE_REJECTED = 4;
    public static final int TYPE_FINALIZE = 5;
    public static final int TYPE_PWRESET = 6;
    public static final int TYPE_INVITE = 7;
     
    private long qnId;
    private int type;
    private DateTime queued = DateTime.now();
    private Long userId;
    private Long inviteId;
    private DateTime failedAttempt;
    private String failedDetail;
  
    public QueuedEmail () {
    }
    
    public void setType (int type) {
        this.type = type;
    }
    
    public void setUserId (User user) {
        this.userId = user.getUserId();
    }
    
    public void setUserId (long userId) {
        this.userId = userId;
    }
    
    public void setInviteId (Invite invite) {
        this.inviteId = invite.getInviteId();
    }
    
    public void setInviteId (long inviteId) {
        this.inviteId = inviteId;
    }
    
    public void markFailed (String detail) {
        failedAttempt = DateTime.now();
        failedDetail = detail;
    }
    
    public long getQnId () {
        return qnId;
    }
    
    public int getType () {
        return type;
    }
    
    public Long getUserId () {
        return userId;
    }
    
    public Long getInviteId () {
        return inviteId;
    }
    
    public User fetchUser (Session session) {
        return userId == null ? null : User.findById(userId, session);
    }
    
    public Invite fetchInvite (Session session) {
        return inviteId == null ? null : Invite.findById(inviteId, session);
    }
    
    public DateTime getQueued () {
        return queued;
    }
    
    public boolean isFailed () {
        return failedAttempt != null;
    }
    
    public DateTime getFailedAttempt () {
        return failedAttempt;
    }
    
    public String getFailedDetail () {
        return failedDetail;
    }
    
    public static void queueNotification (int type, User who) {
        
        QueuedEmail q = new QueuedEmail();
        q.setType(type);
        q.setUserId(who);
      
        HibernateUtil.getCurrentSession().persist(q);
        System.out.println("Notification queued: type=" + type + " user=" + who.getEmail());
        
    }
    
    public static void queueNotification (int type, Invite invite) {
        
        QueuedEmail q = new QueuedEmail();
        q.setType(type);
        q.setInviteId(invite);
      
        HibernateUtil.getCurrentSession().persist(q);
        System.out.println("Notification queued: type=" + type + " invite=" + invite.getInviteeEmail() + " " + invite.getInviteCode());
        
    }    
    
    public static List<QueuedEmail> getQueuedNotifications (Session session) {
        
        @SuppressWarnings("unchecked")
        List<QueuedEmail> qs = (List<QueuedEmail>)session
                .createCriteria(QueuedEmail.class)
                .addOrder(Order.asc("queued"))
                .list();
        
        return qs;
                
    }
    
}
