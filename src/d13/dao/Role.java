package d13.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Order;

import d13.util.HibernateUtil;

public class Role implements Comparable<Role> {

    public static Role DEFAULT_ROLE = new Role();
    
    public static String VIEW_USERS = "viewu";
    public static String EDIT_USERS = "editu";
    public static String REVIEW_USERS = "reviewu";
    public static String ADMIT_USERS = "admitu";
    public static String FINALIZE_USERS = "finalizeu";
    public static String VIEW_LOGS = "viewlogs";
    public static String EDIT_DUES = "editdues";
    public static String MAINTENANCE_LOGIN = "maintlogin";
    public static String VIEW_FULL_CELLS = "viewfullcells";
    public static String EDIT_CELLS = "editcells";
    public static String INVITE_USERS = "inviteu";
    public static String ALWAYS_INVITED = "alwaysinvited";
    public static String VIEW_INVITES = "viewinvites";
    public static String LEAVE_COMMENTS = "comment";
    public static String VIEW_COMMENTS = "viewcomments";
    public static String VIEW_ADMIN_DATA = "viewadmindata";
    public static String EDIT_ANNOUNCEMENTS = "editannounce";
    public static String EDIT_TERMS = "editterms";
    public static String EDIT_EMAILS = "editemails";
    
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
    private boolean viewFullCells;
    private boolean editCells;
    private boolean inviteUsers;
    private boolean alwaysInvited;
    private boolean viewInvites;
    private boolean leaveComments;
    private boolean viewComments;
    private boolean viewAdminData;
    private boolean editAnnouncements;
    private boolean editTerms;
    private boolean editMailTemplates;
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
    
    @Privilege public boolean canViewUsers () {
        return viewUsers;
    }
    
    @Privilege public boolean canEditUsers () {
        return editUsers;
    }
    
    @Privilege public boolean canReviewUsers () {
        return reviewUsers;
    }
    
    @Privilege public boolean canAdmitUsers () {
        return admitUsers;
    }
    
    @Privilege public boolean canFinalizeUsers () {
        return finalizeUsers;
    }
   
    @Privilege public boolean canViewLogs () {
        return viewLogs;
    }
    
    @Privilege public boolean canEditDues () {
        return editDues;
    }
    
    @Privilege public boolean canMaintenanceLogin () {
        return maintenanceLogin;
    }
    
    @Privilege public boolean canViewFullCells () {
        return viewFullCells;
    }
    
    @Privilege public boolean canEditCells () {
        return editCells;
    }
   
    @Privilege public boolean canInviteUsers () {
        return inviteUsers;
    }
    
    @Privilege public boolean isAlwaysInvited () {
        return alwaysInvited;
    }
    
    @Privilege public boolean canViewInvites () {
        return viewInvites;
    }
    
    @Privilege public boolean canLeaveComments () {
        return leaveComments;
    }
    
    @Privilege public boolean canViewComments () {
        return viewComments;
    }
    
    @Privilege public boolean canViewAdminData () {
        return viewAdminData;
    }
    
    @Privilege public boolean canEditAnnouncements () {
        return editAnnouncements;
    }
    
    @Privilege public boolean canEditTerms () {
        return editTerms;
    }
    
    @Privilege public boolean canEditMailTemplates () {
        return editMailTemplates;
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
            if (viewFullCells) rights.add(VIEW_FULL_CELLS);
            if (editCells) rights.add(EDIT_CELLS);
            if (inviteUsers) rights.add(INVITE_USERS);
            if (alwaysInvited) rights.add(ALWAYS_INVITED);
            if (viewInvites) rights.add(VIEW_INVITES);
            if (leaveComments) rights.add(LEAVE_COMMENTS);
            if (viewComments) rights.add(VIEW_COMMENTS);
            if (viewAdminData) rights.add(VIEW_ADMIN_DATA);
            if (editAnnouncements) rights.add(EDIT_ANNOUNCEMENTS);
            if (editTerms) rights.add(EDIT_TERMS);
            if (editMailTemplates) rights.add(EDIT_EMAILS);
            rights = Collections.unmodifiableSet(rights);
        }
        return rights;
    }
   
    @Override public String toString () {
        return getName();
    }
    
    @Override public int compareTo (Role b) {
        return compare(this, b);
    }
    
    public static int compare (Role a, Role b) {
        if (a == b)
            return 0;
        else if (a == null)
            return 1;
        else if (b == null)
            return -1;
        else if (a.getLevel() == b.getLevel())
            return a.getName().compareToIgnoreCase(b.getName());
        else if (a.getLevel() < b.getLevel())
            return 1;
        else
            return -1;
    }
    
    public static List<Role> findAll () {
        
        @SuppressWarnings("unchecked")
        List<Role> roles = (List<Role>)HibernateUtil.getCurrentSession()
                .createCriteria(Role.class)
                .addOrder(Order.desc("level"))
                .addOrder(Order.asc("roleId"))
                .list();
        
        return roles;

    }

}
