package d13.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import d13.util.HibernateUtil;
import d13.util.Util;
import d13.web.CompleteIncompleteBooleanConverter;
import d13.web.DataView;

public class User {

    public long userId;
    public String email;
    public boolean admin;
    public boolean admissions;
    public String passwordSalt;
    public String passwordHash;
    public DateTime created;
    public DateTime lastLogin;
    public Gender gender;
    public String realName;
    public String playaName;
    public Location location;
    public String locationOther;
    public String phone;
    public String emergencyContact;
    public RegistrationForm registration;
    public ApprovalSurvey approval;
    public UserState state = UserState.NEW_USER;
    public String adminComment;
    public boolean termsAgreed;
    public Set<Cell> cells = new HashSet<Cell>(0);
    
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
    
    public void addToCell (Cell c) {
        if (c == null)
            throw new IllegalArgumentException("Cell must be specified.");
        cells.add(c);
        c.addUser(this);
    }
    
    public void removeFromCell (Cell c) {
        if (c == null)
            throw new IllegalArgumentException("Cell must be specified.");
        cells.remove(c);
        c.removeUser(this);
    }
    
    public boolean isInCell (Cell c) {
        return cells.contains(c);
    }
    
    public boolean isInCells () {
        return !cells.isEmpty();
    }
    
    @DataView(i=0, n="ID")
    public long getUserId() {
        return userId;
    }
    
    @DataView(i=2, n="Email")
    public String getEmail() {
        return email;
    }
   
    public boolean checkPassword (String password) {
        if (password == null)
            return false;
        String hashed = Util.hashString(this.passwordSalt + password);
        return hashed.equalsIgnoreCase(this.passwordHash);
    }
    
    public boolean isAdmin() {
        return admin;
    }
    
    public boolean isAdmissions () {
        return admissions;
    }
    
    @DataView(i=2, n="Role")
    public String getRoleDisplay () {
        if (admin && admissions) // getting hacky
            return "Administrator";
        else if (admin)
            return "Registration";
        else if (admissions)
            return "Admissions";
        else
            return null;
    }
    
    @DataView(i=3, n="Created")
    public DateTime getCreated() {
        return created;
    }
    
    @DataView(i=4, n="Last Login")
    public DateTime getLastLogin() {
        return lastLogin;
    }
    
    @DataView(i=5, n="Gender")
    public Gender getGender() {
        return gender;
    }
    
    @DataView(i=6, n="Real Name")
    public String getRealName() {
        return realName;
    }
    
    @DataView(i=7, n="Playa Name")
    public String getPlayaName() {
        return playaName;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public String getLocationOther() {
        return locationOther;
    }
    
    @DataView(i=8, n="Location")
    public String getLocationDisplay () {
        if (location == null)
            return "";
        else if (location == Location.OTHER)
            return locationOther;
        else
            return location.toDisplayString();
    }
    
    @DataView(i=9, n="Phone")
    public String getPhone() {
        return phone;
    }
    
    @DataView(i=10, n="Emergency Contact", longtext=true)
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public RegistrationForm getRegistration() {
        if (registration == null)
            registration = new RegistrationForm(this);
        return registration;
    }
    
    @DataView(i=12, n="Registration Form", value=CompleteIncompleteBooleanConverter.class)
    public boolean isRegistrationComplete () {
        return registration != null && registration.isCompleted();
    }
    
    public ApprovalSurvey getApproval() {
        if (approval == null)
            approval = new ApprovalSurvey(this);
        return approval;
    }
    
    @DataView(i=13, n="Approval Survey", value=CompleteIncompleteBooleanConverter.class)
    public boolean isApprovalComplete () {
        return approval != null && approval.isCompleted();
    }
    
    @DataView(i=1, n="Status")
    public UserState getState() {
        return state;
    }
    
    @DataView(i=14, n="Administrator Comment")
    public String getAdminComment() {
        return adminComment;
    }
    
    @DataView(i=11, n="Read Terms?")
    public boolean isTermsAgreed () {
        return termsAgreed;
    }
    
    public void setEmail(String email) {
        this.email = Util.requireEmail(email, "A valid email address");
    }
    
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    
    public void setAdmissions (boolean admissions) {
        this.admissions = admissions;
    }
     
    public void setPassword (String password) {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password must be specified.");
        else if (password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        this.passwordHash = Util.hashString(this.passwordSalt + password);
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
        this.realName = Util.require(realName, "Real name");
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
        this.phone = Util.validatePhoneNumber(phone);
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = Util.require(emergencyContact, "Emergency contact info");
    }
    
    public void setState(UserState state) {
        this.state = (state == null ? UserState.NEW_USER : state);       
    }
    
    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
    
    public void setTermsAgreed (boolean termsAgreed) {
        this.termsAgreed = termsAgreed;
    }
    
    public boolean isEditableBy (User editor) {
        if (editor == null)
            return false;
        else if (editor == this || editor.getUserId() == getUserId())
            return true;
        else if (editor.isAdmin() && !isAdmin())
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

        User user = (User)HibernateUtil.getCurrentSession()
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
    
    public static List<User> findAll () {
    
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .list();
        
        return users;

    }
    
    public static List<User> findAdmins () {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.eq("admin", true))
                .list();
        
        return users;

    }
    
    public static List<User> findAdmissions () {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.eq("admissions", true))
                .list();
        
        return users;

    }
    
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
            
            int alevel = (a.isAdmin() ? 2 : 0) + (a.isAdmissions() ? 1 : 0);
            int blevel = (b.isAdmin() ? 2 : 0) + (b.isAdmissions() ? 1 : 0);
            return blevel - alevel;
            
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
