package d13.notify;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.Session;
import org.hibernate.Transaction;

import d13.dao.QueuedEmail;
import d13.dao.RuntimeOptions;
import d13.dao.User;
import d13.notify.Email.Configuration;
import d13.util.HibernateUtil;

public class BackgroundNotificationManager implements ServletContextListener {

    public static String RT_ENABLE_NOTIFY = "notify.enabled"; // "1" to enable, else disable
    
    private static final int POLL_INTERVAL = 3000;

    private ExecutorService executor;
    private Notifier notifier;
    
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
    
    private static class Notifier implements Runnable {

        private final Object timer = new Object();
        private volatile boolean terminate = false;
        
        @Override public void run () {

            do {
                synchronized (timer) {
                    try {
                        timer.wait(POLL_INTERVAL);
                    } catch (InterruptedException x) {
                    }
                }                
            } while (!terminate && processEmails());
            
        }

        private int enablestate = -1; // for console messages

        private boolean processEmails () {

            Session session = null;
            Transaction tx = null;
            Email.Configuration config = null;

            // load configuration
            
            session = HibernateUtil.openSession();
            boolean enabled = "1".equals(RuntimeOptions.getOption(RT_ENABLE_NOTIFY, "1", session));
            if (enabled && enablestate != 1) {
                enablestate = 1;
                System.out.println("Notifications enabled.");
            } else if (!enabled && enablestate != 0) {
                enablestate = 0;
                System.out.println("Notifications disabled.");
            }
            if (enabled)
                config = Configuration.fromDatabase(session);
            session.close();
            session = null;
            
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
                System.err.println("When retrieving notification queue: " + t.getMessage());
                //t.printStackTrace();
                return !terminate;
            }
            
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
                        System.err.println("When sending email [#" + q.getQnId() + "]: " + t.getMessage());
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
                    System.err.println("When processing notification [#" + q.getQnId() + "]: " + t.getMessage());
                    //t.printStackTrace();
                }
                
            }
            
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
                    recipients = User.findAdmins(session);
                else if (queued.getType() == QueuedEmail.TYPE_ACCEPTED)
                    recipients = User.findAdmissions(session);
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
            case QueuedEmail.TYPE_APPROVED:
                ApprovalEmail.sendNow(user, config);
                break;
            case QueuedEmail.TYPE_REJECTED:
                RejectionEmail.sendNow(user, config);
                break;
            }
            
        }
       
        public void requestQuit () {
            synchronized (timer) {
                terminate = true;
                timer.notify();
            }
        }
        
    }

}
