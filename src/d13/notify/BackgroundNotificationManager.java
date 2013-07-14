package d13.notify;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.Session;
import org.hibernate.Transaction;

import d13.dao.QueuedEmail;
import d13.dao.User;
import d13.util.HibernateUtil;

public class BackgroundNotificationManager implements ServletContextListener {
    
    private static final int POLL_INTERVAL = 30000;

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


        private boolean processEmails () {
            
            List<QueuedEmail> queued;
            Session session = null;
            Transaction tx = null;
            
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
                t.printStackTrace();
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
                        sendMail(q);
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
        
        
        private void sendMail (QueuedEmail queued) throws Throwable {

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
                ReviewNotificationEmail.sendNow(user, recipients);
                break;
            case QueuedEmail.TYPE_ACCEPTED:
                AcceptedNotificationEmail.sendNow(user, recipients);
                break;
            case QueuedEmail.TYPE_APPROVED:
                ApprovalEmail.sendNow(user);
                break;
            case QueuedEmail.TYPE_REJECTED:
                RejectionEmail.sendNow(user);
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
