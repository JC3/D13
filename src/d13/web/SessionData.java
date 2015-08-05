package d13.web;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import d13.InvalidLoginException;
import d13.dao.RuntimeOptions;
import d13.dao.User;

/**
 * This wraps an HttpSession and provides safe access to common
 * user session data. Various things are cached in the session
 * data, such as the logged in user's username and Rights.
 */
public class SessionData {
     
    public static final String SA_LOGIN_ERROR = "jsp.login.error";
    public static final String SA_LOGIN_EMAIL = "jsp.login.email";
    public static final String SA_LOGIN_EXISTING = "jsp.login.existing";
    public static final String SA_LOGIN_MESSAGE = "jsp.login.message";
    public static final String SA_USER_PROFILE_ERROR = "jsp.personal.error";
    public static final String SA_USER_PROFILE_DEFAULTS = "jsp.personal.defaults";
    public static final String SA_ADMIN_FORM_ADD_DEFAULTS = "jsp.admin_form.add_default";
    public static final String SA_ADMIN_FORM_ADD_ERROR = "jsp.admin_form.error";
    public static final String SA_REG_ERROR = "jsp.registration.error";
    public static final String SA_REG_DEFAULTS = "jsp.registration.defaults";
    public static final String SA_CELL_ERROR = "jsp.cells.error";
    public static final String SA_SURVEY_ERROR = "jsp.approval.error";
    public static final String SA_SURVEY_DEFAULTS = "jsp.approval.defaults";
    public static final String SA_DUES_ERROR = "jsp.dues.error";
    public static final String SA_DUES_INVOICE_ID = "jsp.dueconfirm.invoice_id";
    public static final String SA_EDIT_DUES_ERROR = "jsp.editdues.error";
    public static final String SA_EDIT_DUES_DEFAULTS = "jsp.editdues.defaults";
    public static final String SA_PWRESET_ERROR = "jsp.pwreset.error";
    public static final String SA_MANAGE_INVITES_ERROR = "jsp.invites.error";
    public static final String SA_MANAGE_INVITES_WARNING = "jsp.invites.warning";
    public static final String SA_MANAGE_INVITES_EMAILS = "jsp.invites.emails";
    public static final String SA_MANAGE_INVITES_EXPIRES = "jsp.invites.expires";
    public static final String SA_MANAGE_INVITES_COMMENT = "jsp.invites.comment";
    public static final String SA_INVITE_ERROR = "jsp.invite.error";
    
    //private static final String SA_EMAIL = "info.disorient.SessionData.email";
    private static final String SA_USERID = "info.disorient.SessionData.userid";
    
    private HttpSession session;
    
    
    /**
     * Construct a SessionData that accesses the data stored in a given
     * HttpSession.
     * @param session HttpSession
     * @throws Exception If session is null or some other error occurs.
     */
    public SessionData (HttpSession session) 
        throws Exception
    {
        
        if (session == null)
            throw new Exception("No HTTP session specified.");
        
        this.session = session;
    
    }
    
    
    /**
     * Sets arbitrary session data. This has the same semantics as
     * HttpSession.setAtttribute, except it silently ignores errors
     * resulting from invalidated sessions.
     * @param key    Key.
     * @param value  Value.
     */
    public void setAttribute (String key, Object value) {
        
        try {
            session.setAttribute(key, value);
        } catch (Exception x) {
        }
        
    }
    
    
    public void clearAttribute (String key) {
        
        try {
            session.removeAttribute(key);
        } catch (Exception x) {
        }
        
    }
    

    /**
     * Gets arbitrary session data. This has the same semantics as
     * HttpSession.getAttribute, except it returns null on errors
     * resulting from invalidated sessions (rather than throwing an
     * exception).
     * @param key   Key.
     * @return Session attribute value, null if attribute not set.
     */
    public Object getAttribute (String key) {
        
        try {
            return session.getAttribute(key);
        } catch (Exception x) {
            return null;
        }
        
    }
    
    
    public Object getAndClearAttribute (String key) {
        
        Object o = null;
        
        try {
            o = session.getAttribute(key);
            session.removeAttribute(key);
        } catch (Exception x) {
            o = null;
        }
        
        return o;

    }
    
    
    /**
     * Check to see if a user is logged in.
     * @return True if a user is currently logged in, false if not.
     */
    public boolean isLoggedIn () {
        
        return (getUserId() != null);
        
    }
   
   
    public Long getUserId () {
        
        try {
            return (Long)session.getAttribute(SA_USERID);
        } catch (Throwable t) {
            return null;
        }
        
    }
   
    
    public User getUser () {
        
        try {
            return User.findById(getUserId());
        } catch (Throwable t) {
            return null;
        }
        
    }

    
    /**
     * Log a user in (logs the current user out first, if any). Note
     * that the current user will be logged out even if an exception
     * is thrown from this function.
     * @param username Username.
     * @param password Password.
     * @throws Exception If username or password are empty or null,
     *         or if a general error occurs.
     * @throws SecurityException If the login is incorrect.
     */
    public void login (String username, String password)
        throws Exception
    {
        
        // always log out first, but don't invalidate session.
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements())
            session.removeAttribute(names.nextElement());
        
        // log user in
        
        User user = null;
        try {
            user = User.findByEmail(username);
        } catch (Throwable t) {
        }
        
        if (user == null)
            throw new InvalidLoginException("Invalid email address or password.");
        
        if (!user.checkPassword(password))
            throw new InvalidLoginException("Invalid email address or password.");
        
        if (RuntimeOptions.Global.isMaintenanceMode() && !user.getRole().canMaintenanceLogin())
            throw new SecurityException("Sorry, the system is temporarily down for maintenance.");
        
        user.setLastLoginNow();
        session.setAttribute(SA_USERID, user.getUserId());
    
    }


    /**
     * Log the current user out, if one is logged in. Does nothing if no
     * user is logged in.
     */
    public void logout () {
        
        try { 
            session.invalidate(); 
        } catch (Throwable t) { 
        }
            
    }
    
}
