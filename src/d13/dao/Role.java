package d13.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Role {

    public static Role DEFAULT_ROLE = new Role();
    
    public static String VIEW_USERS = "viewu";
    public static String EDIT_USERS = "editu";
    public static String REVIEW_USERS = "reviewu";
    public static String ADMIT_USERS = "admitu";
    public static String FINALIZE_USERS = "finalizeu";
    public static String VIEW_LOGS = "viewlogs";
    public static String EDIT_DUES = "editdues";
    public static String MAINTENANCE_LOGIN = "maintlogin";
    
    private long roleId;
    private String name = "";
    private int level = Integer.MIN_VALUE;
    private boolean viewUsers;
    private boolean editUsers;
    private boolean reviewUsers;
    private boolean admitUsers;
    private boolean finalizeUsers;
    private boolean viewLogs;
    private boolean editDues;
    private boolean maintenanceLogin;
    private Set<String> rights; 
    
    Role () {
    }
   
    public long getRoleId () {
        return roleId;
    }
    
    public String getName () {
        return name;
    }
    
    public int getLevel () {
        return level;
    }
    
    public boolean isSpecial () {
        return !getRights().isEmpty();
    }
    
    public boolean canViewUsers () {
        return viewUsers;
    }
    
    public boolean canEditUsers () {
        return editUsers;
    }
    
    public boolean canReviewUsers () {
        return reviewUsers;
    }
    
    public boolean canAdmitUsers () {
        return admitUsers;
    }
    
    public boolean canFinalizeUsers () {
        return finalizeUsers;
    }
   
    public boolean canViewLogs () {
        return viewLogs;
    }
    
    public boolean canEditDues () {
        return editDues;
    }
    
    public boolean canMaintenanceLogin () {
        return maintenanceLogin;
    }
    
    public Set<String> getRights () {
        if (rights == null) {
            rights = new HashSet<String>();
            if (viewUsers) rights.add(VIEW_USERS);
            if (editUsers) rights.add(EDIT_USERS);
            if (reviewUsers) rights.add(REVIEW_USERS);
            if (admitUsers) rights.add(ADMIT_USERS);
            if (finalizeUsers) rights.add(FINALIZE_USERS);
            if (viewLogs) rights.add(VIEW_LOGS);
            if (editDues) rights.add(EDIT_DUES);
            if (maintenanceLogin) rights.add(MAINTENANCE_LOGIN);
            rights = Collections.unmodifiableSet(rights);
        }
        return rights;
    }
    
}
