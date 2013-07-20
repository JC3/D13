package d13.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.joda.time.DateTime;

import d13.util.HibernateUtil;

public class Invoice {
    
    public static final int STATUS_IN_PROGRESS = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_COMPLETE = 2;
    public static final int STATUS_FAILED = 3;

    private long invoiceId;
    private List<InvoiceItem> items = new ArrayList<InvoiceItem>();
    private int status;
    private User creator;
    private DateTime created;
    private int invoiceAmount;
    private String paypalTransactionId;
    private String paypalSenderEmail;
    private String paypalSenderFirstName;
    private String paypalSenderLastName;
    private String paypalTransactionStatus;
    private String paypalTimestamp;
    private int paypalAmount;
    
    Invoice () {
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public int getStatus() {
        return status;
    }

    public User getCreator() {
        return creator;
    }

    public DateTime getCreated() {
        return created;
    }

    public int getInvoiceAmount () {
        return invoiceAmount;
    }
    
    public String getPaypalTransactionId() {
        return paypalTransactionId;
    }

    public String getPaypalSenderEmail() {
        return paypalSenderEmail;
    }

    public String getPaypalSenderFirstName() {
        return paypalSenderFirstName;
    }

    public String getPaypalSenderLastName() {
        return paypalSenderLastName;
    }

    public String getPaypalTransactionStatus() {
        return paypalTransactionStatus;
    }

    public String getPaypalTimestamp() {
        return paypalTimestamp;
    }

    public int getPaypalAmount() {
        return paypalAmount;
    }
    
    public List<InvoiceItem> getItems () {
        return Collections.unmodifiableList(items);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public void setInvoiceAmount(int amount) {
        this.invoiceAmount = amount;
    }
    
    public void setPaypalTransactionId(String paypalTransactionId) {
        this.paypalTransactionId = paypalTransactionId;
    }

    public void setPaypalSenderEmail(String paypalSenderEmail) {
        this.paypalSenderEmail = paypalSenderEmail;
    }

    public void setPaypalSenderFirstName(String paypalSenderFirstName) {
        this.paypalSenderFirstName = paypalSenderFirstName;
    }

    public void setPaypalSenderLastName(String paypalSenderLastName) {
        this.paypalSenderLastName = paypalSenderLastName;
    }

    public void setPaypalTransactionStatus(String paypalTransactionStatus) {
        this.paypalTransactionStatus = paypalTransactionStatus;
    }

    public void setPaypalTimestamp(String paypalTimestamp) {
        this.paypalTimestamp = paypalTimestamp;
    }

    public void setPaypalAmount(int paypalAmount) {
        this.paypalAmount = paypalAmount;
    }
    
    public void addInvoiceItem(DueItem due, int amount, String description) {
        items.add(new InvoiceItem(this, due, amount, description));
    }
    
    public static Invoice findById (Long id) {
        
        return findById(id, HibernateUtil.getCurrentSession());

    }

    public static Invoice findById (Long id, Session session) {

        Invoice invoice = (Invoice)session
                .get(Invoice.class, id);
        
        if (invoice == null)
            throw new IllegalArgumentException("There is no invoice with the specified ID.");
        
        return invoice;

    }

}
