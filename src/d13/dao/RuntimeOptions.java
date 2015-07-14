package d13.dao;

import org.hibernate.Session;

import d13.util.HibernateUtil;

public class RuntimeOptions {

    public static class RuntimeOption {
        String name;
        String value;
        RuntimeOption () { }
        RuntimeOption (String name) { this.name = name; }
        RuntimeOption (String name, String value) { this.name = name; this.value = value; }
    }
 
    public static void setOption (String name, String value, Session session) {
        session.merge(new RuntimeOption(name, value));
    }
    
    public static void setOption (String name, String value) {
        setOption(name, value, HibernateUtil.getCurrentSession());
    }
    
    public static void unsetOption (String name, Session session) {
        session.delete(new RuntimeOption(name));
    }
    
    public static void unsetOption (String name) {
        unsetOption(name, HibernateUtil.getCurrentSession());
    }
    
    public static String getOption (String name, String def, Session session) {
        RuntimeOption option = (RuntimeOption)session.get(RuntimeOption.class, name);
        return option == null ? def : option.value;
    }

    public static String getOption (String name, String def) {
        return getOption(name, def, HibernateUtil.getCurrentSession());
    }
    
    public static String getOption (String name, Session session) {
        return getOption(name, null, session);
    }
    
    public static String getOption (String name) {
        return getOption(name, null, HibernateUtil.getCurrentSession());
    }
    
    public static class Global {
               
        public static boolean isRegistrationClosed () {
            return isRegistrationClosed(HibernateUtil.getCurrentSession());
        }
        
        public static boolean isRegistrationClosed (Session session) {
            return !"0".equals(RuntimeOptions.getOption("closed", "0"));
        }
        
        public static boolean isMaintenanceMode () {
            return !"0".equals(RuntimeOptions.getOption("maintenance", "0"));
        }
        
        public static boolean isInviteOnly () {
            return !"0".equals(RuntimeOptions.getOption("invite_only", "0"));
        }
        
        /** Returns null or returns value. Never returns empty string. */
        private static String nullOrElse (String option) {
            String value = RuntimeOptions.getOption(option, "");
            if (value != null) {
                value = value.trim();
                if ("".equals(value))
                    value = null;
            }
            return value;
        }
        
        /** Returns null, or returns announcement. Never returns empty string. */
        public static String getGeneralAnnouncement () {
            return nullOrElse("announcement.general");
        }
        
        public static String getGeneralAnnouncementStyleOverride () {
            return nullOrElse("announcement.general.style");
        }
        
        /** Returns null, or returns announcement. Never returns empty string. */
        public static String getLoggedInAnnouncement () {
            return nullOrElse("announcement.login");
        }
        
        /** Returns null, or returns style. Never returns empty string. */
        public static String getLoggedInAnnouncementStyleOverride () {
            return nullOrElse("announcement.login.style");
        }

    }
    
}
