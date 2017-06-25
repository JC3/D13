package d13.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
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
    
    private static Cell newCategory (String name, User creator) {
        Cell c = new Cell();
        c.setName(name);
        c.category = true;
        c.addCreatedActivityLogEntry(creator);
        return c;
    }
    
    private static Cell newCell (String name, User creator) {
        Cell c = new Cell();
        c.setName(name);
        c.addCreatedActivityLogEntry(creator);
        return c;
    }
    
    public Cell addCategory (String name, User creator) {
        Cell c = newCategory(name, creator);
        addChild(c);
        return c;
    }
    
    public Cell addCell (String name, User creator) {
        Cell c = newCell(name, creator);
        addChild(c);
        return c;
    }
    
    private boolean removeChild (Cell child) {
        for (int n = 0; n < children.size(); ++ n) {
            if (children.get(n).getCellId() == child.getCellId()) {
                children.remove(n);
                child.parent = null;
                return true;
            }
        }
        return false;
    }
    
    private void addChild (Cell child) {
        children.add(child);
        child.parent = this;
    }
    
    // TODO: Make this not public; move delete code here.
    public void removeFromParent () {
        if (getParent() != null)
            getParent().removeChild(this);
    }
    
    public boolean changeParent (Cell newParent) {
        // Lots of validation here.
        if (newParent == null)
            throw new IllegalArgumentException("Cell parent must be specified.");
        if (this.getParent() == null)
            throw new IllegalArgumentException("Can't change parent of root cell.");
        if (!newParent.isCategory())
            throw new IllegalArgumentException("Cell parent must be a category.");
        if (newParent.getCellId() == this.getParent().getCellId())
            return false; // Nothing to do.
        for (Cell check = newParent; check != null; check = check.getParent())
            if (check.getCellId() == this.getCellId())
                throw new IllegalArgumentException("Can't change cell parent, would create a loop.");
        // OK we're good...
        getParent().removeChild(this);
        newParent.addChild(this);
        return true;
    }
    
    public void moveUp () {
        if (parent == null)
            throw new IllegalArgumentException("This cell can't move up any more.");
        int pos = parent.children.indexOf(this);
        if (pos == -1)
            throw new IllegalStateException("Parent cell does not contain this cell?");
        if (pos == 0) {
            parent.moveUp();
        } else {
            parent.children.remove(pos);
            parent.children.add(pos - 1, this);
        }
    }
    
    public void moveDown () {
        if (parent == null)
            throw new IllegalArgumentException("This cell can't move down any more.");
        int pos = parent.children.indexOf(this);
        if (pos == -1)
            throw new IllegalStateException("Parent cell does not contain this cell?");
        if (pos == parent.children.size() - 1) {
            parent.moveDown();
        } else {
            parent.children.remove(pos);
            parent.children.add(pos + 1, this);
        }        
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
    
    public int getNonCategoryChildCount () {
        int total = 0;
        for (Cell child : getChildren()) {
            if (child.isCategory())
                total += child.getNonCategoryChildCount();
            else
                total ++;
        }
        return total;
    }
    
    public boolean isChildOf (Cell other) {
        for (Cell p = getParent(); p != null; p = p.getParent())
            if (p.getCellId() == other.getCellId())
                return true;
        return false;
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
    
    // TODO: use isReallyHidden() for logic in CellList and elsewhere, right now only used in view_cells2.jsp
    public boolean isReallyHidden () {
        return isHidden() || (isFull() && isHideWhenFull());
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
    public static int CHANGED_CATEGORY = 1<<6;
    
    public void addEditedActivityLogEntry (User who, int whatChanged) {
        String entry = "";
        if ((whatChanged & CHANGED_CATEGORY) != 0)
            entry += String.format(", Category: %s", parent.getParent() == null ? "<None>" : parent.getFullName());
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
    
    public void addCreatedActivityLogEntry (User who) {
        addActivityLogEntry(new CellActivityLogEntry(this, who, (category ? "Category " : "Cell ") + "created."));
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
    
    
    public static List<Cell> findCategories (boolean withRoot) {
       
        Criteria crit = HibernateUtil.getCurrentSession()
                .createCriteria(Cell.class)
                .add(Restrictions.ne("category", false));
        
        if (!withRoot)
            crit.add(Restrictions.isNotNull("parent"));
        
        @SuppressWarnings("unchecked")
        List<Cell> cells = (List<Cell>)crit.list();
        
        Collections.sort(cells, new Comparator<Cell> () {
            @Override public int compare (Cell a, Cell b) {
                if (a.getCellId() == b.getCellId())
                    return 0;
                else if (a.getParent() == null)
                    return -1;
                else if (b.getParent() == null)
                    return 1;
                else
                    return a.getFullName().compareToIgnoreCase(b.getFullName());
            }            
        });
        
        return cells;
        
    }
    
}
