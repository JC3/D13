package d13.dao;

import java.util.List;

import org.joda.time.DateTime;

public class Invoice {

    public static int STATUS_NONE = 0;
    public static int STATUS_PENDING = 1;
    public static int STATUS_PAID = 2;
    public static int STATUS_FAILED = 3;
    
    private long invoiceId;
    private User user;
    private List<DueItem> items;
    private int total;
    private int status;
    private DateTime submitDate;
    private DateTime notifyDate;
    
    Invoice () {
    }
    
    public int getStatus () {
        return status;
    }
    
}
