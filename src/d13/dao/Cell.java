package d13.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import d13.util.HibernateUtil;
import d13.util.Util;

public class Cell {

    public static final String ROOT_NAME = "$ROOT";
    
    private long    cellId;
    private Cell    parent;
    private String  startDate;
    private String  endDate;
    private String  name;
    private int     people;
    private String  description;
    private boolean category;
    private boolean hideWhenFull;
    private boolean mandatory;
    private boolean hidden;
    private Set<User> users = new HashSet<User>(0);
    private List<Cell> children = new ArrayList<Cell>();
    private List<CellActivityLogEntry> activityLog = new ArrayList<CellActivityLogEntry>();
    
    Cell () {
    }
   
    public static Cell newRoot () {
        Cell c = new Cell();
        c.name = ROOT_NAME;
        c.category = true;
        return c;
    }
    
    private static Cell newCategory (String name) {
        Cell c = new Cell();
        c.setName(name);
        c.category = true;
        return c;
    }
    
    private static Cell newCell (String name) {
        Cell c = new Cell();
        c.setName(name);
        return c;
    }
    
    public Cell addCategory (String name) {
        Cell c = newCategory(name);
        c.parent = this;
        children.add(c);
        return c;
    }
    
    public Cell addCell (String name) {
        Cell c = newCell(name);
        c.parent = this;
        children.add(c);
        return c;
    }

    public Set<User> getUsers () {
        return Collections.unmodifiableSet(users);
    }
    
    public List<Cell> getChildren () {
        return Collections.unmodifiableList(children);
    }
    
    public Cell getParent () {
        return parent;
    }
    
    public long getCellId() {
        return cellId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getFullName () {
        if (parent != null && !parent.getName().equals(ROOT_NAME))
            return parent.getFullName() + ": " + getName();
        else
            return getName();
    }
    
    public String getParentName () {
        if (parent != null && !parent.getName().equals(ROOT_NAME))
            return parent.getFullName();
        else
            return null;
    }
    
    public String getName() {
        return name;
    }

    public int getPeople() {
        return people;
    }

    public String getDescription() {
        return description;
    }
    
    public boolean isCategory () {
        return category;
    }
   
    public boolean isHideWhenFull () {
        return hideWhenFull;
    }
    
    public boolean isMandatory () {
        return mandatory;
    }
    
    public boolean isHidden () {
        return hidden;
    }
    
    public boolean isMandatoryFor (User user) {
        if (user == null || !isMandatory()) {
            return false;
        }
        if (isHidden()) { // cell is hidden; do not make hidden cells mandatory, users will get stuck
            System.err.println("WARNING: Mandatory cell is hidden; ignoring mandatory status: " + getCellId() + " " + getFullName());
            return false;
        }
        if (getPeople() > 0 && getUsers().size() >= getPeople()) { // cell is full; do not make full cells mandatory, users will get stuck on cell page
            System.err.println("WARNING: Mandatory cell is full; ignoring mandatory status: " + getCellId() + " " + getFullName());
            return false;
        }
        // TODO: administrators? special right for that? for now make them required for admins.
        return true;
    }
    
    // TODO: use isFull() for logic in CellList and elsewhere, right now only used in view_cells2.jsp
    public boolean isFull () {
        return (people > 0 && users.size() >= people);
    }
    
    public List<CellActivityLogEntry> getActivityLog () {
        return Collections.unmodifiableList(activityLog);
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setName(String name) {
        if (ROOT_NAME.equals(name))
            throw new IllegalArgumentException("Invalid cell name specified.");
        this.name = Util.require(name, "Cell name");
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHideWhenFull (boolean h) {
        this.hideWhenFull = h;
    }
    
    public void setMandatory (boolean m) {
        this.mandatory = m;
    }
    
    public void setHidden (boolean h) {
        this.hidden = h;
    }
    
    void addUser (User user) {
        if (user != null)
            users.add(user);
    }
    
    void removeUser (User user) {
        if (user != null)
            users.remove(user);
    }
    
    public void addActivityLogEntry (CellActivityLogEntry entry) {
        if (entry != null)
            activityLog.add(entry);
    }

    public static int CHANGED_NAME = 1<<0;
    public static int CHANGED_DESCRIPTION = 1<<1;
    public static int CHANGED_PEOPLE = 1<<2;
    public static int CHANGED_HIDEWHENFULL = 1<<3;
    public static int CHANGED_MANDATORY = 1<<4;
    public static int CHANGED_HIDDEN = 1<<5;
    
    public void addEditedActivityLogEntry (User who, int whatChanged) {
        String entry = "";
        if ((whatChanged & CHANGED_PEOPLE) != 0)
            entry += String.format(", Volunteers: %d", people);
        if ((whatChanged & CHANGED_HIDEWHENFULL) != 0)
            entry += String.format(", Auto-hide: %s", hideWhenFull ? "Yes" : "No");
        if ((whatChanged & CHANGED_MANDATORY) != 0)
            entry += String.format(", Mandatory: %s", mandatory ? "Yes" : "No");
        if ((whatChanged & CHANGED_HIDDEN) != 0)
            entry += String.format(", Hidden: %s", hidden ? "Yes" : "No");
        if ((whatChanged & CHANGED_NAME) != 0)
            entry += String.format(", Name: \"%s\"", name);
        if ((whatChanged & CHANGED_DESCRIPTION) != 0)
            entry += String.format(", Text: \"%s\"", description);
        addActivityLogEntry(new CellActivityLogEntry(this, who, "Details edited" + entry));
    }
    
    public static Cell findById (Long id) {
        
        Cell cell = (Cell)HibernateUtil.getCurrentSession()
                .get(Cell.class, id);
        
        if (cell == null)
            throw new IllegalArgumentException("There is no cell with the specified ID.");
        
        return cell;

    }
    
    public static Cell findRoot () {
        
        Cell cell = (Cell)HibernateUtil.getCurrentSession()
                .createCriteria(Cell.class)
                .add(Restrictions.eq("name", ROOT_NAME))
                .uniqueResult();
        
        return cell;

    }
    
    public static List<Cell> findAll () {
        
        @SuppressWarnings("unchecked")
        List<Cell> cells = (List<Cell>)HibernateUtil.getCurrentSession()
                .createCriteria(Cell.class)
                .list();
        
        return cells;

    }
    
    /**
     * This returns *all* mandatory cells (i.e. all cells that might be mandatory for a user). Further
     * per-user filtering should be done {@link Cell#isMandatoryFor(User)} to check for specific users.
     * All site business logic goes through that (BUG: except the view_data.jsp quick filter, TODO).
     */
    public static List<Cell> findMandatory () {
        
        @SuppressWarnings("unchecked")
        List<Cell> cells = (List<Cell>)HibernateUtil.getCurrentSession()
                .createCriteria(Cell.class)
                .add(Restrictions.ne("mandatory", false))
                .list();
        
        return cells;
        
    }
    
}
