package d13.notify;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;

import d13.dao.QueuedEmail;
import d13.dao.RuntimeOptions;
import d13.dao.User;
import d13.notify.Email.Configuration;
import d13.util.HibernateUtil;

public class BackgroundNotificationManager implements ServletContextListener {

    public static String RT_ENABLE_NOTIFY = "notify.enabled"; // "1" to enable, else disable
    
    private static final int POLL_INTERVAL = 60000;

    private ExecutorService executor;
    private Notifier notifier;
    private int pollInterval = POLL_INTERVAL;
    private Boolean enableOverride = null;
    
    public void overrideConfiguration (boolean enable, int pollInterval) {
        this.enableOverride = enable;
        this.pollInterval = pollInterval;
    }
    
    @Override public void contextInitialized (ServletContextEvent e) {
        executor = Executors.newSingleThreadExecutor();
        notifier = new Notifier();
        executor.submit(notifier);
    }

    @Override public void contextDestroyed (ServletContextEvent e) {
        notifier.requestQuit();
        executor.shutdown();
        executor = null;
    }
    
    private class Notifier implements Runnable {

        private final Object timer = new Object();
        private volatile boolean terminate = false;
        
        @Override public void run () {

            while (!terminate && processEmails()) {
                synchronized (timer) {
                    try {
                        timer.wait(pollInterval);
                    } catch (InterruptedException x) {
                    }
                }                
            }
            
        }

        private int enablestate = -1; // for console messages

        private boolean processEmails () {

            Session session = null;
            Transaction tx = null;
            Email.Configuration config = null;

            // load configuration
            
            boolean enabled;
            try {
                session = HibernateUtil.openSession();
                if (enableOverride != null)
                    enabled = enableOverride;
                else
                    enabled = "1".equals(RuntimeOptions.getOption(RT_ENABLE_NOTIFY, "1", session));
                if (enabled && enablestate != 1) {
                    enablestate = 1;
                    System.out.println("MAIL: Notifications enabled.");
                } else if (!enabled && enablestate != 0) {
                    enablestate = 0;
                    System.out.println("MAIL: Notifications disabled.");
                }
                if (enabled)
                    config = Configuration.fromDatabase(session);
                session.close();
                session = null;
            } catch (Throwable t) {
                System.err.println("MAIL: When loading configuration options: " + t.getMessage());
                enabled = false;
            }
                
            if (!enabled)
                return !terminate;
            
            // process queue
            
            List<QueuedEmail> queued;
            
            try {
                session = HibernateUtil.openSession();
                try {
                    tx = session.beginTransaction();
                    queued = QueuedEmail.getQueuedNotifications(session);
                    tx.commit();
                } catch (Throwable t) {
                    if (tx != null) tx.rollback();
                    throw t;
                } finally {
                    session.close();
                    session = null;
                    tx = null;
                }
            } catch (Throwable t) {
                System.err.println("MAIL: When retrieving notification queue: " + t.getMessage());
                //t.printStackTrace();
                return !terminate;
            }

            System.out.println("MAIL: Processing " + queued.size() + " notification(s)...");
            
            for (QueuedEmail q:queued) {
            
                try {
                    
                    if (terminate)
                        break;
    
                    session = HibernateUtil.openSession();
                    try {
                        tx = session.beginTransaction();
                        session.delete(q);
                        tx.commit();
                    } catch (Throwable t) {
                        if (tx != null) tx.rollback();
                        throw t;
                    } finally {
                        session.close();
                        session = null;
                        tx = null;
                    }
                   
                    String error = null;
                    try {
                        sendMail(q, config);
                    } catch (Throwable t) {
                        System.err.println("MAIL: When sending email [#" + q.getQnId() + "]: " + t.getMessage());
                        //t.printStackTrace();
                        error = t.getMessage();
                        if (error == null) error = "";
                    }
                                 
                    if (error != null) {
                        q.markFailed(error);
                        session = HibernateUtil.openSession();
                        try {
                            tx = session.beginTransaction();
                            session.save(q);
                            tx.commit();
                        } catch (Throwable t) {
                            if (tx != null) tx.rollback();
                            throw t;
                        } finally {
                            session.close();
                            session = null;
                            tx = null;
                        }                    
                    }
                    
                } catch (Throwable t) {
                    System.err.println("MAIL: When processing notification [#" + q.getQnId() + "]: " + t.getMessage());
                    //t.printStackTrace();
                }
                
            }
            
            if (!queued.isEmpty())
                System.out.println("MAIL: Finished processing queue.");
            
            return !terminate;
            
        }
        
        
        private void sendMail (QueuedEmail queued, Email.Configuration config) throws Throwable {

            User user;
            List<User> recipients;
            
            Session session = HibernateUtil.openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                user = queued.fetchUser(session);
                if (queued.getType() == QueuedEmail.TYPE_REVIEW)
                    recipients = User.findReviewersForEmail(session);
                else if (queued.getType() == QueuedEmail.TYPE_ACCEPTED)
                    recipients = User.findAdmissionsForEmail(session);
                else if (queued.getType() == QueuedEmail.TYPE_FINALIZE)
                    recipients = User.findFinalizersForEmail(session);
                else
                    recipients = null;
                tx.commit();
            } catch (Throwable t) {
                if (tx != null) tx.rollback();
                throw t;
            } finally {
                session.close();
                session = null;
                tx = null;
            }              
            
            if (user == null)
                throw new Exception("No such user.");
            
            //System.out.println("SEND: " + queued.getType() + " TO " + user.getEmail());
            
            switch (queued.getType()) {
            case QueuedEmail.TYPE_REVIEW:
                ReviewNotificationEmail.sendNow(user, recipients, config);
                break;
            case QueuedEmail.TYPE_ACCEPTED:
                AcceptedNotificationEmail.sendNow(user, recipients, config);
                break;
            case QueuedEmail.TYPE_FINALIZE:
                PendingNotificationEmail.sendNow(user, recipients, config);
                break;
            case QueuedEmail.TYPE_APPROVED:
                ApprovalEmail.sendNow(user, config);
                break;
            case QueuedEmail.TYPE_REJECTED:
                RejectionEmail.sendNow(user, config);
                break;
            }
            
            // 7/23/2013: update user's grace period start time with current time if
            // approval email was sent successfully. this is more fair to user's when
            // there are delays in the notification emails.
            if (queued.getType() == QueuedEmail.TYPE_APPROVED) {
                session = HibernateUtil.openSession();
                tx = null;
                try {
                    tx = session.beginTransaction();
                    user = queued.fetchUser(session);
                    user.setGracePeriodStart(DateTime.now());
                    tx.commit();
                } catch (Throwable t) {
                    if (tx != null) tx.rollback();
                    throw t;
                } finally {
                    session.close();
                    session = null;
                    tx = null;
                }
            }
            
        }
       
        public void requestQuit () {
            synchronized (timer) {
                terminate = true;
                timer.notify();
            }
        }
        
    }

    /*private static class RateLimiter {
        
        static final int RATE_LIMIT_OK = 0;
        static final int RATE_LIMIT_EXCEEDED = 1;
        static final int RATE_LIMIT_ERROR = 2;
        
        private static final int MAX_PER_HOUR = 90;
        
        @SuppressWarnings("unused")
        private static class Entry {
            long entryId;
            DateTime entryTime = DateTime.now();
        }
       
        static void addEntry () {
            
            Session s = null;
            Transaction tx = null;
       
            try {
                s = HibernateUtil.openSession();
                DateTime now = DateTime.now();
                tx = s.beginTransaction();
                // remove old entries
                int removed = s.createQuery("delete from Entry where entryTime < " + now.getMillis()).executeUpdate();
                System.err.println("MAIL: RateLimiter removed " + removed + " old entr" + (removed == 1 ? "y" : "ies") + ".");
                // add new entry
                s.persist(new Entry());
                tx.commit();
            } catch (Throwable t) {
                if (tx != null) tx.rollback();
                System.err.println("MAIL: While adding rate limit entry: " + t.getMessage());
            } finally {
                if (s != null) s.close();
            }
            
        }
        
        static int checkStatus () {
            
            Session 
            
        }
        
    }*/
    
}
