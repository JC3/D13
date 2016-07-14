package d13.dao;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class CellActivityLogEntry {
    
    private long entryId;
    private Cell toCell;
    private User byWho;
    private DateTime time;
    private String description;
    
    CellActivityLogEntry () {
    }
    
    public CellActivityLogEntry (Cell cell, User who, String description) {
        if (cell == null)
            throw new IllegalArgumentException("No cell.");
        if (who == null)
            throw new IllegalArgumentException("No user.");
        description = StringUtils.trimToNull(description);
        if (description == null)
            throw new IllegalArgumentException("No description");
        this.toCell = cell;
        this.byWho = who;
        this.time = DateTime.now();
        this.description = description;
    }
    
    public long getEntryId () {
        return entryId;
    }
    
    public Cell getToCell () {
        return toCell;
    }
    
    public User getByWho () {
        return byWho;
    }
    
    public DateTime getTime () {
        return time;
    }
    
    public String getDescription () {
        return description;
    }

}
