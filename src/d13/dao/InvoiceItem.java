package d13.dao;

public class InvoiceItem {

    private long invoiceItemId;
    private Invoice invoice;
    private DueItem due;
    private int amount;
    private String description;
    
    InvoiceItem () {
    }
    
    public InvoiceItem (Invoice invoice, DueItem due, int amount, String description) {
        this.invoice = invoice;
        this.due = due;
        this.amount = amount;
        this.description = description;
    }
    
    public long getInvoiceItemId () {
        return invoiceItemId;
    }
    
    public Invoice getInvoice () {
        return invoice;
    }
    
    public DueItem getDue () {
        return due;
    }
    
    public int getAmount () {
        return amount;
    }
   
    public String getDescription () {
        return description;
    }
    
}
