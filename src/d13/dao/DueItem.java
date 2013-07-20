package d13.dao;

public class DueItem {
    
    public static final int NO_CUSTOM_AMOUNT = -1;

    private long itemId;
    private User user;
    private boolean active = false;
    private boolean open = true;
    private Invoice paidInvoice;
    private int customAmount = NO_CUSTOM_AMOUNT;
    
    DueItem () {
    }
    
    public DueItem (User user) {
        this.user = user;
    }

    public long getItemId() {
        return itemId;
    }
    
    public User getUser () {
        return user;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isPaid () {
        return paidInvoice != null || customAmount == 0;
    }
    
    public Invoice getPaidInvoice () {
        return paidInvoice;
    }
    
    public boolean isCustom () {
        return customAmount >= 0;
    }
    
    public int getCustomAmount () {
        return customAmount;
    }

    public boolean isPayable () {
        // due must be active
        // due must be open (i.e. not currently being processed)
        // due must be unpaid
        return isActive() && isOpen() && !isPaid();
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setPaidInvoice(Invoice paidInvoice) {
        this.paidInvoice = paidInvoice;
    }
    
    public void setCustomAmount (int amount) {
        this.customAmount = amount;
    }
    
}
