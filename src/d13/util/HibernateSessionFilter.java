package d13.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * The DBTransactionFilter runs around all servlet requests, and is
 * responsible for starting and ending a Hibernate transaction. This
 * is a basic implementation of the "open-session-in-view" model,
 * described in various places on the internet and also discussed
 * here:
 * 
 *   http://www.jroller.com/cardsharp/entry/open_session_in_view_pattern
 * 
 * Note that while this model makes most things a lot easier, it can
 * cause some problems, especially when isolated transactions are
 * required, e.g. if you want to load configuration data from a JSP
 * page. Right now, there aren't any problems like that. This model
 * also makes clean up after errors slightly messy, the rollback flag
 * is somewhat of a hack, for example. However, it guarantees that
 * database transactions will always be cleaned up.
 */
public class HibernateSessionFilter implements Filter {
    
    @Override public void init (FilterConfig arg0) 
        throws ServletException 
    {
    }

    @Override public void destroy () {
    }

    /**
     * Begins a Hibernate transaction, processes the request, and commits
     * or rolls back the transaction depending on the status of the 
     * request and the state of the rollback flag.
     */
    @Override public void doFilter (ServletRequest req, ServletResponse resp, FilterChain fc) 
        throws IOException, ServletException 
    {
        // hack; don't initialize hibernate session on error report page
        /*if (req instanceof HttpServletRequest) {
            String path = ((HttpServletRequest)req).getRequestURI();
            if (path.contains("/oops.jsp")) {
                fc.doFilter(req, resp);
                return;
            }
        }*/
        try {
            HibernateUtil.beginTransaction();
            fc.doFilter(req, resp);
            HibernateUtil.commitTransaction();
        } catch (Throwable t) {
            Logger.exception(t, "processing servlet request");
            HibernateUtil.rollbackTransaction();
            throw new ServletException(t);
        }
    }

}
