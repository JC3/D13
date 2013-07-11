package d13.util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


@SuppressWarnings("deprecation") // hibernate 5.0 will require use of ServiceRegistry
public class HibernateUtil {

    
    static SessionFactory m_factory;

    
    static {
        try {
            m_factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable t) {
            Logger.exception(t, "creating static hibernate session factory");
            throw new ExceptionInInitializerError(t);
        }
    }
    
    
    /**
     * Get single instance of hibernate session factory.
     * @return  SessionFactory, does not return null.
     */
    private static SessionFactory getSessionFactory () {
        return m_factory;
    }

    
    /**
     * Get/create hibernate session.
     * @return A hibernate session.
     */
    public static Session getCurrentSession () {    
        return getSessionFactory().getCurrentSession();
    }
    
    
    /**
     * Start a new transaction.
     */
    public static void beginTransaction () 
        throws Exception
    {
        try {
            getCurrentSession().beginTransaction();     
        } catch (Exception x) {
            Logger.exception(x, "beginning hibernate transaction");
            throw x;
        }
    }
    

    /**
     * If there is a current transaction, end it, committing changes to
     * the database.
     */
    public static void commitTransaction () 
        throws Exception
    {
        try {
            getCurrentSession().getTransaction().commit();
        } catch (Exception t) {
            Logger.exception(t, "commiting hibernate transaction");
            throw t;
        }
    }

    
    /**
     * If there is a current transaction, end it, rolling back changes to
     * the database.
     */
    public static void rollbackTransaction () {
        try {
            if (getCurrentSession().getTransaction().isActive()) {
                Logger.warning("Rolling back transaction after error.");
                getCurrentSession().getTransaction().rollback();
            }
        } catch (Exception t) {
            Logger.exception(t, "rolling back hibernate transaction");
        }       
    }

    
}
