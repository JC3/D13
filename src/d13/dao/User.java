package d13.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import d13.changetrack.Track;
import d13.changetrack.Trackable;
import d13.changetrack.Tracker;
import d13.util.HibernateUtil;
import d13.util.Util;
import d13.web.CompleteIncompleteBooleanConverter;
import d13.web.DataView;

public class User implements Trackable {
    
    public static final String RT_PWRESET_EXPIRE_MINUTES = "user.pwreset_expire_minutes";
    public static final String RT_PWRESET_EXPIRE_MINUTES_DEFAULT = "10";
 
    public static final int LOW_CELL_THRESHOLD = 2;
    
    private long userId;
    private String email;
    private Role role;
    private String passwordSalt;
    private String passwordHash;
    private DateTime created;
    private DateTime lastLogin;
    private String passwordResetKey;
    private DateTime passwordResetTime;
    private Gender gender;
    private String realName;
    private String playaName;
    private Location location; 
    private String locationOther;
    private String phone;
    private String phoneRegion;
    private String phoneFromUs;
    private String emergencyContact;
    private RegistrationForm registration;
    private ApprovalSurvey approval;
    private UserState state = UserState.NEW_USER;
    private DateTime approvedOn;
    private DateTime gracePeriodStart;
    private boolean termsAgreed;
    private Invite currentInvite;
    private Set<Cell> cells = new HashSet<Cell>(0);
    private List<ActivityLogEntry> activityLog = new ArrayList<ActivityLogEntry>();
    private List<GeneralLogEntry> generalLog = new ArrayList<GeneralLogEntry>();
    private DueItem personalDue;
    private DueItem rvDue;
    private String customDueComments;
    private List<Invoice> invoices = new ArrayList<Invoice>();
    private boolean receiveNotifications = true;
    private List<Comment> comments = new ArrayList<Comment>();
    private Map<String,IPLogEntry> ipHistory = new HashMap<String,IPLogEntry>();
    
    User () {
    }
   
    public User (String email) {
        setEmail(email);
        created = DateTime.now();
        passwordSalt = Util.randomString(8);
    }
  
    public Set<Cell> getCells () {
        return Collections.unmodifiableSet(cells);
    }
    
    public boolean addToCell (Cell c) {
        if (c == null)
            throw new IllegalArgumentException("Cell must be specified.");
        boolean changed = cells.add(c);
        c.addUser(this);
        return changed;
    }
    
    public boolean removeFromCell (Cell c) {
        if (c == null)
            throw new IllegalArgumentException("Cell must be specified.");
        boolean changed = cells.remove(c);
        c.removeUser(this);
        return changed;
    }
    
    void addInvoice (Invoice invoice) {
        invoices.add(invoice);
    }
    
    public boolean isInCell (Cell c) {
        return cells.contains(c);
    }
    
    public boolean isInCells () {
        return !cells.isEmpty();
    }
    
    public boolean isInEnoughCells () {
        //return cells.size() > LOW_CELL_THRESHOLD;
        // 2016 - added ability for cells to be mandatory; mandatory cells do not count towards warning threshold
        int cellCount = 0;
        for (Cell c : cells)
            if (!c.isMandatoryFor(this))
                ++ cellCount;
        return cellCount >= LOW_CELL_THRESHOLD;
    }
    
    public boolean isInMandatoryCells () {
        for (Cell c : Cell.findMandatory())
            if (c.isMandatoryFor(this) && !isInCell(c))
                return false;
        return true;
    }
    
    public boolean isReceiveNotifications () {
        return receiveNotifications;
    }
    
    @DataView(i=0, n="ID")
    public long getUserId() {
        return userId;
    }
    
    @Track
    @DataView(i=20, n="Email", email=true)
    public String getEmail() {
        return email;
    }
   
    public boolean checkPassword (String password) {
        if (password == null)
            return false;
        String hashed = Util.hashString(this.passwordSalt + password);
        return hashed.equalsIgnoreCase(this.passwordHash);
    }
    
    @DataView(i=20, n="Role")
    public Role getRole () {
        if (role == null)
            return Role.DEFAULT_ROLE;
        else
            return role;
    }
    
    public String getRoleDisplay () {
        if (getRole().isSpecial())
            return getRole().getName();
        else
            return null;
    }
    
    @DataView(i=30, n="Created")
    public DateTime getCreated() {
        return created;
    }
    
    @DataView(i=40, n="Last Login")
    public DateTime getLastLogin() {
        return lastLogin;
    }
    
    @Track
    @DataView(i=50, n="Gender")
    public Gender getGender() {
        return gender;
    }
    
    @Track
    @DataView(i=60, n="Real Name")
    public String getRealName() {
        return realName;
    }
    
    @Track
    @DataView(i=70, n="Playa Name")
    public String getPlayaName() {
        return playaName;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public String getLocationOther() {
        return locationOther;
    }
    
    @Track 
    @DataView(i=80, n="Location")
    public String getLocationDisplay () {
        if (location == null)
            return "";
        else if (location == Location.OTHER)
            return locationOther;
        else
            return location.toDisplayString();
    }
    
    @Track
    @DataView(i=90, n="Phone")
    public String getPhone() {
        return phone;
    }
    
    @DataView(i=91, n="Phone (From US)")
    public String getPhoneFromUs () {
        if (phoneFromUs == null || phoneFromUs.trim().isEmpty())
            return null;
        else
            return String.format("%s (%s)", phoneFromUs, phoneRegion);
    }
    
    @Track
    @DataView(i=100, n="Emergency Contact", longtext=true)
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public RegistrationForm getRegistration() {
        if (registration == null)
            registration = new RegistrationForm(this);
        return registration;
    }
    
    @DataView(i=120, n="Registration Form", value=CompleteIncompleteBooleanConverter.class)
    public boolean isRegistrationComplete () {
        return registration != null && registration.isCompleted();
    }
    
    @DataView(i=125, n="Registered On")
    public DateTime getRegisteredOn () {
        return registration == null ? null : registration.getCompletionTime();
    }
    
    public ApprovalSurvey getApproval() {
        if (approval == null)
            approval = new ApprovalSurvey(this);
        return approval;
    }
    
    @DataView(i=130, n="Approval Survey", value=CompleteIncompleteBooleanConverter.class)
    public boolean isApprovalComplete () {
        return approval != null && approval.isCompleted();
    }
    
    @DataView(i=10, n="Status")
    public UserState getState() {
        return state;
    }
    
    @DataView(i=131, n="Approved On") 
    public DateTime getApprovedOn () {
        return approvedOn;
    }
    
    @DataView(i=135, n="Grace Period Start")
    public DateTime getGracePeriodStart () {
        return gracePeriodStart;
    }
    
    @DataView(i=110, n="Read Terms?")
    public boolean isTermsAgreed () {
        return termsAgreed;
    }
    
    public DueItem getPersonalDueItem () {
        if (state != UserState.APPROVED)
            return null;
        else if (personalDue == null) {
            personalDue = new DueItem(this);
            personalDue.setActive(true);
        }
        return personalDue;
    }
    
    public DueItem getRvDueItem () {
        if (state != UserState.APPROVED)
            return null;
        else if (rvDue == null) {
            rvDue = new DueItem(this);
            rvDue.setActive(registration != null && registration.getRvType() == RVSelection.RESPONSIBLE);
        }
        return rvDue;
    }
    
    void setRvDueOwed (boolean owed) {
        if (rvDue != null)
            rvDue.setActive(owed);
    }
    
    public DueItem getPersonalDueItemNoInit () {
        return personalDue;
    }
    
    public DueItem getRvDueItemNoInit () {
        return rvDue;
    }
    
    public String getCustomDueComments () {
        return customDueComments;
    }
    
    @DataView(i=137, n="Dues Paid?")
    public String getPaidDisplay () {
        if (isPaid())
            return "Paid";
        else if (isPersonalPaid())
            return "RV Unpaid";
        else if (isRvPaid())
            return "Personal Unpaid";
        else
            return "Unpaid";
    }
    
    @DataView(i=138, n="Camp Fee Paid On")
    public DateTime getPersonalPaidDate () {
        if (personalDue == null)
            return null;
        else if (personalDue.getPaidInvoice() == null)
            return null;
        else
            return personalDue.getPaidInvoice().getCreated();
    }

    @DataView(i=139, n="RV Fee Paid On")
    public DateTime getRvPaidDate () {
        if (rvDue == null)
            return null;
        else if (rvDue.getPaidInvoice() == null)
            return null;
        else
            return rvDue.getPaidInvoice().getCreated();
    }

    public boolean isPaid () {
        return isPersonalPaid() && isRvPaid();
    }
    
    public boolean isPersonalPaid () {
        return getPersonalDueItem() != null && (!getPersonalDueItem().isActive() || getPersonalDueItem().isPaid());
    }
    
    public boolean isRvPaid () {
        return getRvDueItem() != null && (!getRvDueItem().isActive() || getRvDueItem().isPaid());
    }
    
    public String getPasswordResetKey () {
        return passwordResetKey;
    }
    
    public DateTime getPasswordResetTime () {
        return passwordResetTime;
    }
    
    public boolean isPasswordResetTimeExpired () {
        if (passwordResetKey == null || passwordResetTime == null)
            return true;
        try {
            int minutes = Integer.parseInt(RuntimeOptions.getOption(RT_PWRESET_EXPIRE_MINUTES, RT_PWRESET_EXPIRE_MINUTES_DEFAULT));
            return passwordResetTime.plusMinutes(minutes).isBeforeNow();
        } catch (Exception x) {
            System.err.println("User.isPasswordResetTimeExpired: " + x.getMessage());
            return true;
        }
    }
    
    public boolean isInviteCodeNeeded () {
        if (!RuntimeOptions.Global.isInviteOnly())
            return false;
        if (getRole().isAlwaysInvited())
            return false;
        if (currentInvite != null && currentInvite.getStatus() == Invite.STATUS_ACCEPTED)
            return false;
        return true;
    }
    
    public Map<String,IPLogEntry> getIpHistory () {
        return Collections.unmodifiableMap(ipHistory);
    }
    
    public String hitIpHistory (String ip, DateTime now) {
        IPLogEntry e = ipHistory.get(ip);
        if (e == null) {
            e = new IPLogEntry(this, ip);
            ipHistory.put(ip, e);
        } else {
            e.see();
        }
        return ip;
    }
    
    public String hitIpHistory (String ip) {
        return hitIpHistory(ip, DateTime.now());
    }
    
    public String hitIpHistory (HttpServletRequest request) {
        return hitIpHistory(Util.ip(request), DateTime.now());
    }
    
    public void setCurrentInvite (Invite invite) {
        currentInvite = invite;
    }
    
    public void setEmail(String email) {
        this.email = Util.requireEmail(email, "A valid email address");
    }
     
    public void setPassword (String password) {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password must be specified.");
        else if (password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        this.passwordHash = Util.hashString(this.passwordSalt + password);
    }
    
    public void activatePasswordReset () {
        String probablyUnique = Util.hashString(this.passwordSalt + Long.toString(this.userId) + "-" + Math.random()).substring(0, 8);
        this.passwordResetKey = Util.randomString(8) + probablyUnique;
        this.passwordResetTime = DateTime.now();
    }
    
    public void clearPasswordReset () {
        this.passwordResetKey = null;
        this.passwordResetTime = null;
    }
    
    public void setLastLoginNow() {
        this.lastLogin = DateTime.now();
    }
    
    public void setGender(Gender gender) {
        if (gender == null)
            throw new IllegalArgumentException("Gender must be specified.");
        this.gender = gender;
    }
    
    public void setRealName(String realName) {
        String name = Util.require(realName, "Real name");
        if (!name.contains(" "))
            throw new IllegalArgumentException("Both first and last name must be specified.");
        this.realName = name;
    }
    
    public void setPlayaName(String playaName) {
        this.playaName = playaName;
    }
    
    public void setLocation(Location location) {
        if (location == null)
            throw new IllegalArgumentException("Location must be specified.");
        this.location = location;
    }
    
    public void setLocationOther(String locationOther) {
        this.locationOther = locationOther;
    }
    
    public void setPhone(String phone) {
        //this.phone = Util.validatePhoneNumber(phone);
        Util.PhoneNumberInfo info = Util.parsePhoneNumber(phone);
        if (info == null) {
            this.phone = null;
            this.phoneRegion = null;
            this.phoneFromUs = null;
        } else {
            this.phone = info.formatted;
            this.phoneRegion = info.region;
            this.phoneFromUs = info.fromUS;
        }
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = Util.require(emergencyContact, "Emergency contact info");
    }
    
    public void setState(UserState state) {
        this.state = (state == null ? UserState.NEW_USER : state);
    }
    
    public void setApprovedOnNow () {
        approvedOn = DateTime.now();
    }
    
    public void setApprovedOnNowIfNotSet () {
        if (approvedOn == null)
            setApprovedOnNow();
    }
        
    public void setGracePeriodStart (DateTime when) {
        gracePeriodStart = when;
    }
    
    public void setTermsAgreed (boolean termsAgreed) {
        this.termsAgreed = termsAgreed;
    }
    
    public void setCustomDueComments (String comments) {
        this.customDueComments = comments;
    }
    
    public List<Invoice> getInvoices () {
        return Collections.unmodifiableList(invoices);
    }
    
    public List<ActivityLogEntry> getActivityLog () {
        return Collections.unmodifiableList(activityLog);
    }
    
    public List<GeneralLogEntry> getGeneralLog () {
        return Collections.unmodifiableList(generalLog);
    }
    
    public void hibernateInitLogHacks () {
        Hibernate.initialize(activityLog);
        //Hibernate.initialize(generalLog); // Unnecessary
    }

    public void addGeneralLogEntry (GeneralLogEntry entry) {
        if (entry != null)
            generalLog.add(entry);
    }
    
    public void addGeneralLogEntry (String summary, int type) {
        addGeneralLogEntry(new GeneralLogEntry(summary, null, type, this));
    }

    public void addGeneralLogEntry (String summary, String detail, int type) {
        addGeneralLogEntry(new GeneralLogEntry(summary, detail, type, this));
    }

    public void addActivityLogEntry (ActivityLogEntry entry) {
        if (entry != null)
            activityLog.add(entry);
    }
    
    public void addBasicActivityLogEntry (String description, int type) {
        addActivityLogEntry(new ActivityLogEntry(this, description, type));
    }
    
    public void addBasicActivityLogEntry (String description, int type, User who) {
        addActivityLogEntry(new ActivityLogEntry(this, description, type, who));
    }

    public void addStateActivityLogEntry (User editor, UserState old, boolean silent) {
        String description = String.format("Changed from %s to %s%s.", old.toString(), state.toString(), silent ? " (no email)" : "");
        addActivityLogEntry(new ActivityLogEntry(this, description, ActivityLogEntry.TYPE_REVIEW, editor));
    }
    
    private static String dispnull (String s) {
        if (s == null)
            return "blank";
        else
            return "\"" + s + "\"";
    }
    
    public void addTrackerActivityLogEntry (User editor, String section, Map<String,Tracker.Change> changes, boolean onlyIfReviewed) {
        
        if (onlyIfReviewed && (getState() == UserState.NEW_USER || getState() == UserState.NEEDS_REVIEW))
            return;
        
        if (changes.isEmpty())
            return;
        
        String message = section + " edited:\n";
        for (Tracker.Change c : changes.values())
            message += " - " + c.getKey() + " changed from " + dispnull(c.getPrev()) + " to " + dispnull(c.getCurr()) + "\n";
        
        message = message.trim();
        
        addActivityLogEntry(new ActivityLogEntry(this, message, ActivityLogEntry.TYPE_EDIT, editor));
        
    }


    public void addCellActivityLogEntry(User editor, List<Cell> added, List<Cell> removed, boolean onlyIfReviewed) {

        if (onlyIfReviewed && (getState() == UserState.NEW_USER || getState() == UserState.NEEDS_REVIEW))
            return;
        
        String message = "";
        
        if (added != null && !added.isEmpty()) {
            message += "Joined cells:\n";
            for (Cell c : added)
                message += " - " + c.getFullName() + "\n";
        }
        
        if (removed != null && !removed.isEmpty()) {
            message += "Left cells:\n";
            for (Cell c : removed)
                message += " - " + c.getFullName() + "\n";
        }
        
        message = message.trim();
        
        if (!message.isEmpty())
            addActivityLogEntry(new ActivityLogEntry(this, message, ActivityLogEntry.TYPE_EDIT, editor));
        
    }
   
    public List<Comment> getComments () {
        return Collections.unmodifiableList(comments);
    }
    
    public void addComment (User author, String text) {
        addComment(author, text, null);
    }

    public void addComment (User author, String text, DateTime timestamp) {
        Comment comment = new Comment(this, author, text);
        if (timestamp != null)
            comment.setTime(timestamp);
        comments.add(comment);
    }
    
    public boolean hasViewableComments (User by) {
        if (by == null)
            return false;
        else if (comments == null || comments.isEmpty())
            return false;
        else if (!by.getRole().canViewComments())
            return false;
        else
            return true;
    }
    
    /*
    public boolean isEditableBy (User editor) {
        if (editor == null)
            return false;
        else if (editor == this || editor.getUserId() == getUserId())
            return true;
        else if (editor.getRole().canEditUsers())
            return true;
        else
            return false;
    }
    
    public boolean isApprovableBy (User editor) {
        // admissions can approve anybody with REGISTERED state, including themselves and administrators
        if (editor == null)
            return false;
        else if (editor.isAdmin() && editor.isAdmissions() && state != UserState.NEW_USER)
            return true;
        else if (editor.isAdmissions() && state == UserState.REGISTERED)
            return true;
        else
            return false;
    }
    
    public boolean isViewableBy (User editor) {
        // administrators can view everybody
        // admissions can view everybody who isn't a new user
        // any user can view themselves
        if (editor == null)
            return false;
        else if (editor == this || editor.getUserId() == getUserId())
            return true;
        else if (editor.isAdmin())
            return true;
        else if (editor.isAdmissions() && state != UserState.NEW_USER && state != UserState.NEEDS_REVIEW)
            return true;
        else
            return false;
    }
   */
    
    public boolean isLoginEditableBy (User editor) {
        if (editor == null)
            return false;
        else if (editor == this || editor.getUserId() == getUserId())
            return true;
        else if (!editor.getRole().canEditUsers())
            return false;
        else if (editor.getRole().getLevel() > getRole().getLevel())
            return true;
        else
            return false;
    }
    
    // TODO: rename
    public boolean isEditableBy2 (User editor) {
        if (editor == null)
            return false;
        else if (!editor.getRole().canEditUsers() && RuntimeOptions.Global.isRegistrationClosed())
            return false;
        else if (editor == this || editor.getUserId() == getUserId())
            return true;
        else if (editor.getRole().canEditUsers())
            return true;
        else
            return false;
    }
    
    // TODO: rename
    public boolean isReviewableBy2 (User editor) {
        if (editor == null)
            return false;
        else if (RuntimeOptions.Global.isRegistrationClosed())
            return false;
        else if (editor.getRole().canReviewUsers())
            return state == UserState.NEEDS_REVIEW;
        else if (editor.getRole().canFinalizeUsers())
            return true;
        else 
            return false;
    }

    public boolean isFinalizableBy (User editor) {
        if (editor == null)
            return false;
        else if (RuntimeOptions.Global.isRegistrationClosed())
            return false;
        else if (editor.getRole().canFinalizeUsers())
            return (state != UserState.NEW_USER && state != UserState.REGISTERED);
        else
            return false;
    }
    
    // TODO: rename
    public boolean isApprovableBy2 (User editor) {
        if (editor == null)
            return false;
        else if (RuntimeOptions.Global.isRegistrationClosed())
            return false;
        else if (editor.getRole().canAdmitUsers())
            return (state == UserState.REGISTERED || state == UserState.APPROVE_PENDING || state == UserState.REJECT_PENDING);
        else if (editor.getRole().canFinalizeUsers())
            return (state != UserState.NEW_USER && state != UserState.REGISTERED);
        else
            return false;
    }
    
    // TODO: rename
    public boolean isViewableBy2 (User viewer) {
        if (viewer == null)
            return false;
        else if (viewer == this || viewer.getUserId() == getUserId())
            return true;
        else if (viewer.getRole().canViewUsers())
            return true;
        else
            return false;
    }
    
    public boolean isCommentableBy (User commenter) {
        if (commenter == null)
            return false;
        else if (commenter.getRole().canLeaveComments())
            return true;
        else
            return false;
    }
    
    @Override public String toString () {
        return Long.toString(userId);
    }
    
    @Override public boolean equals (Object o) {
        if (o == null)
            return false;
        else if (o == this)
            return true;
        else if (!(o instanceof User))
            return false;
        else
            return ((User)o).getEmail().equalsIgnoreCase(email);
    }
    
    @Override public int hashCode () {
        if (email == null)
            return 0;
        else
            return email.toLowerCase().hashCode();
    }

    public static User findById (Long id) {
        
        return findById(id, HibernateUtil.getCurrentSession());

    }

    public static User findById (Long id, Session session) {

        User user = (User)session
                .get(User.class, id);
        
        if (user == null)
            throw new IllegalArgumentException("There is no user with the specified ID.");
        
        return user;

    }

    public static User findByEmail (String email) {
    
        email = (email == null ? "" : email.trim());
        if (email.isEmpty())
            throw new IllegalArgumentException("Email address must be specified.");
        
        User user = (User)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.eq("email", email).ignoreCase())
                .uniqueResult();
        
        return user;

    }
    
    public static User findByPasswordResetKey (String key) {
        
        key = (key == null ? "" : key.trim());
        if (key.isEmpty())
            return null;
        
        User user = (User)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.eq("passwordResetKey", key))
                .uniqueResult();
        
        return user;
        
    }
    
    public static List<User> findAll () {
    
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .list();
        
        return users;

    }
    
    public static List<User> findAllExceptNew () {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.ne("state", UserState.NEW_USER))
                .list();
        
        return users;
        
    }
    
    /*
    public static User findSystemAdministrator () {
        
        User user = (User)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.eq("role", 1))
                .uniqueResult();
        
        return user;
        
    }*/
    
    public static List<User> findSpecial () {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.isNotNull("role"))
                .createAlias("role", "role")
                .addOrder(Order.desc("role.level"))
                .addOrder(Order.asc("role.id"))
                .addOrder(Order.asc("userId"))
                .list();
        
        return users;
        
    }
    
    public static List<User> findReviewersForEmail (Session session) {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)session
                .createQuery("from User as user where user.receiveNotifications = true and user.role.reviewUsers = true")
                .list();
        
        return users;
        
    }
    
    public static List<User> findAdmissionsForEmail (Session session) {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)session
                .createQuery("from User as user where user.receiveNotifications = true and user.role.admitUsers = true")
                .list();
        
        return users;

    }
    
    public static List<User> findFinalizersForEmail (Session session) {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)session
                .createQuery("from User as user where user.receiveNotifications = true and user.role.finalizeUsers = true")
                .list();
        
        return users;

    }
    
    /*
    public static List<User> findUnpaidPersonalDues () {
 
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createQuery("from User as user where user.state = " + UserState.APPROVED.toDBId() + " and user.personalDue.paymentRequired = true and user.personalDue.paymentComplete = false order by lower(user.realName) asc")
                .list();
        
        return users;
        
    }
    
    public static List<User> findUnpaidRVDues () {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createQuery("from User as user where user.state = " + UserState.APPROVED.toDBId() + " and user.rvDue.paymentRequired = true and user.rvDue.paymentComplete = false order by lower(user.realName) asc")
                .list();
        
        return users;
   
    }
    */
    
    /*
    public static List<User> findAdmins (Session session) {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)session
                .createCriteria(User.class)
                .add(Restrictions.eq("admin", true))
                .list();
        
        return users;

    }
    
    public static List<User> findAdmins () {
        
        return findAdmins(HibernateUtil.getCurrentSession());
        
    }
    
    public static List<User> findAdmissions (Session session) {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)session
                .createCriteria(User.class)
                .add(Restrictions.eq("admissions", true))
                .list();
        
        return users;

    }
    
    public static List<User> findAdmissions () {
        
        return findAdmissions(HibernateUtil.getCurrentSession());
        
    }
    */
    
    public static void addUser (User user) {
        
        if (user == null)
            throw new IllegalArgumentException("Null user specified.");
        if (findByEmail(user.getEmail()) != null)
            throw new IllegalArgumentException("This email address is already in use.");

        try {
            HibernateUtil.getCurrentSession().save(user);
        } catch (HibernateException x) {
            throw new IllegalArgumentException(x);
        }
        
    }
 
    
    /**
     * Sorts a user list by full name.
     */
    public static class RealNameComparator implements Comparator<User> {
        
        @Override public int compare (User a, User b) {
            return a.getRealName().compareTo(b.getRealName());
        }
        
    }
    

    /**
     * Sorts a user list by the user's location display string.
     */
    private static class LocationDisplayComparator implements Comparator<User> {
        
        //private final User current;
        
        LocationDisplayComparator (/*User current*/) {
            //this.current = current;
        }
        
        @Override public int compare (User a, User b) {
            if (a == b)
                return 0;
            else if (a == null)
                return -1;
            else if (b == null)
                return 1;
            else
                return a.getLocationDisplay().compareToIgnoreCase(b.getLocationDisplay());
        }
        
    }
    
    
    /**
     * Sorts a user list in descending order of the level of the user's role.
     * D13 just has admin or not admin
     */
    private static class RoleLevelComparator implements Comparator<User> {

        //private final User current;
        
        RoleLevelComparator (/*User current*/) {
            //this.current = current;
        }
        
        @Override public int compare (User a, User b) {
            if (a == b)
                return 0;
            else if (a == null)
                return -1;
            else if (b == null)
                return 1;
       
            /*
            int alevel = (a.isAdmin() ? 2 : 0) + (a.isAdmissions() ? 1 : 0);
            int blevel = (b.isAdmin() ? 2 : 0) + (b.isAdmissions() ? 1 : 0);
            return blevel - alevel;
            */
            
            //return b.getRole().getName().compareToIgnoreCase(a.getRole().getName());
           
            int alevel = a.getRole().getLevel();
            int blevel = b.getRole().getLevel();
            
            if (alevel < blevel)
                return 1;
            else if (alevel > blevel)
                return -1;
            else
                return b.getRole().getName().compareToIgnoreCase(a.getRole().getName());
            
            /*
            if (a.isAdmin() == b.isAdmin())
                return 0;
            else
                return a.isAdmin() ? -1 : 1;
                */
            
            /*
            Role arole = a.getRole();
            Role brole = b.getRole();
            int alevel = (arole == null ? Role.LOWEST_LEVEL : arole.getLevel());
            int blevel = (brole == null ? Role.LOWEST_LEVEL : brole.getLevel());
            if (alevel < blevel)
                return 1;
            else if (alevel > blevel)
                return -1;
            else
                return 0;
                */
        }
        
    }


    /**
     * @param current Ignored
     * @param sortby Sort key.
     * @return Sorted list of all users.
     * @throws SecurityException
     */
    public static List<User> findAll (String sortby) throws SecurityException {
        
        Criteria crit = HibernateUtil.getCurrentSession()
                .createCriteria(User.class);

        Comparator<User> postsort = null;
        
        // explicitly check sortby field so app can't sort by columns it shouldn't be accessing.
        // note that since "email" is the default, we have no special action for "email".
        if ("id".equalsIgnoreCase(sortby) ||
            "created".equalsIgnoreCase(sortby) ||
            "gender".equalsIgnoreCase(sortby) ||
            "lastLogin".equalsIgnoreCase(sortby) ||
            "realName".equalsIgnoreCase(sortby) ||
            "playaName".equalsIgnoreCase(sortby) ||
            "emergencyContact".equalsIgnoreCase(sortby) ||
            "phone".equalsIgnoreCase(sortby)) 
        {
            crit.addOrder(Order.asc(sortby).ignoreCase());
        } else if ("role".equalsIgnoreCase(sortby)) {
            postsort = new RoleLevelComparator();
        } else if ("location".equalsIgnoreCase(sortby)) {
            postsort = new LocationDisplayComparator();
        }
        
        crit.addOrder(Order.asc("email").ignoreCase());            

        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)crit.list();

        if (postsort != null)
            Collections.sort(users, postsort); // stable sort, pre-sorted by email
        
        return users;
                
    }
    
}
