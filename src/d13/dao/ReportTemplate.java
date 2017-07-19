package d13.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import d13.util.HibernateUtil;

public class ReportTemplate {
    
    // TODO: enum type and a hibernate type for it.
    public static final int CELLS_NONE = 0;
    public static final int CELLS_LIST = 1;
    public static final int CELLS_SPLIT = 2;

    private long id;
    private DateTime created;
    private User createdBy;
    private DateTime accessed;
    private String title;
    private String fields;
    private int cells;
    private int filter;
    private boolean excludeMandatoryCells;
    
    ReportTemplate () {
    }
    
    public ReportTemplate (String[] fields, int cells, int filter, boolean excludemc, User creator) {
        this.created = DateTime.now();
        this.createdBy = creator;
        this.accessed = this.created;
        setTitle(null);
        setFieldList(fields);
        setCells(cells);
        setFilter(filter);
        setExcludeMandatoryCells(excludemc);
    }

    public long getId() {
        return id;
    }

    public DateTime getCreated() {
        return created;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public DateTime getAccessed() {
        return accessed;
    }

    public String getTitle() {
        return title;
    }

    public String getFields() {
        return fields;
    }
    
    public String[] getFieldList () {
        return fields.split(",");
    }

    public int getCells() {
        return cells;
    }

    public int getFilter() {
        return filter;
    }
    
    public boolean getExcludeMandatoryCells () {
        return excludeMandatoryCells;
    }
    
    public void setTitle (String title) {
        this.title = StringUtils.trimToNull(title);
    }
    
    public void setFieldList (String[] fields) {
        if (fields == null || fields.length == 0)
            throw new IllegalArgumentException("At least one column must be specified for a report.");
        this.fields = StringUtils.join(fields, ",");
    }
    
    public void setCells (int cells) {
        if (cells != CELLS_NONE && cells != CELLS_LIST && cells != CELLS_SPLIT)
            throw new IllegalArgumentException("Invalid cell mode specified.");
        this.cells = cells;
    }
    
    public void setFilter (int filter) {
        this.filter = filter;
    }
    
    public void setAccessedNow () {
        this.accessed = DateTime.now();
    }
    
    public void setExcludeMandatoryCells (boolean excludemc) {
        this.excludeMandatoryCells = excludemc;
    }
  
      /** Does not care about title. */
    @Override public boolean equals (Object o) {
        if (o == null || !(o instanceof ReportTemplate))
            return false;
        else if (o == this)
            return true;
        ReportTemplate r = (ReportTemplate)o;
        return (cells == r.cells && filter == r.filter && fields.equals(r.fields));
    }
    
    /** @return Null if not found. */
    public static ReportTemplate findById (long id) {

        ReportTemplate rt = (ReportTemplate)HibernateUtil.getCurrentSession().get(ReportTemplate.class, id);
       
        //if (rt == null)
        //    throw new IllegalArgumentException("There is no report with the specified ID.");
        
        return rt;

    }
    
    public static ReportTemplate findOrCreate (ReportTemplate temp) {
    
        ReportTemplate existing = (ReportTemplate)HibernateUtil.getCurrentSession()
                .createCriteria(ReportTemplate.class)
                .add(Restrictions.eq("cells", temp.getCells()))
                .add(Restrictions.eq("filter", temp.getFilter()))
                .add(Restrictions.eq("fields", temp.getFields()))
                .add(Restrictions.eq("excludeMandatoryCells", temp.getExcludeMandatoryCells()))
                .uniqueResult();
        
        if (existing == null) {
            HibernateUtil.getCurrentSession().save(temp);
        } else {
            existing.setTitle(temp.getTitle());
            existing.setAccessedNow();
            temp = existing;
        }
        
        return temp;
        
    }
       
}