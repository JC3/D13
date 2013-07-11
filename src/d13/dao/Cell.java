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
    private Set<User> users = new HashSet<User>(0);
    private List<Cell> children = new ArrayList<Cell>();
    
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
 
    void addUser (User user) {
        if (user != null)
            users.add(user);
    }
    
    void removeUser (User user) {
        if (user != null)
            users.remove(user);
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
    
}
