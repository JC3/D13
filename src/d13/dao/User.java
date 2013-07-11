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

public class User {

    public long userId;
    public String email;
    public boolean admin;
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
    
    public long getUserId() {
        return userId;
    }
    
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
    
    public DateTime getCreated() {
        return created;
    }
    
    public DateTime getLastLogin() {
        return lastLogin;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public String getPlayaName() {
        return playaName;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public String getLocationOther() {
        return locationOther;
    }
    
    public String getLocationDisplay () {
        if (location == null)
            return "";
        else if (location == Location.OTHER)
            return locationOther;
        else
            return location.toDisplayString();
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public RegistrationForm getRegistration() {
        if (registration == null)
            registration = new RegistrationForm(this);
        return registration;
    }
    
    public boolean isRegistrationComplete () {
        return registration != null && registration.isCompleted();
    }
    
    public ApprovalSurvey getApproval() {
        if (approval == null)
            approval = new ApprovalSurvey(this);
        return approval;
    }
    
    public boolean isApprovalComplete () {
        return approval != null && approval.isCompleted();
    }
    
    public UserState getState() {
        return state;
    }
    
    public String getAdminComment() {
        return adminComment;
    }
    
    public boolean isTermsAgreed () {
        return termsAgreed;
    }
    
    public void setEmail(String email) {
        this.email = Util.requireEmail(email, "A valid email address");
    }
    
    public void setAdmin(boolean admin) {
        this.admin = admin;
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
            if (a.isAdmin() == b.isAdmin())
                return 0;
            else
                return a.isAdmin() ? -1 : 1;
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
