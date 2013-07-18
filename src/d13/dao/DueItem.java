package d13.dao;

import org.joda.time.DateTime;

public class DueItem {

    public static final int TYPE_PERSONAL = 0;
    public static final int TYPE_RV = 1;
    public static final int AMOUNT_AUTO = -1;
        
    private long itemId;
    private int type;
    private boolean paymentRequired;
    private int amount; // if AMOUNT_AUTO then amount is calculated based on type, approval date, and current date
    private DateTime registrationDate;
    private DateTime approvalDate;
    private Invoice invoice;
    private int invoicedAmount;
 
    DueItem () {
    }
    
    public static DueItem newPersonalItem (User user) {
        if (user.getState() != UserState.APPROVED || user.getApprovedOn() == null)
            throw new IllegalArgumentException("Cannot create due item: User is not approved.");
        DueItem item = new DueItem();
        item.type = TYPE_PERSONAL;
        item.paymentRequired = true;
        item.amount = AMOUNT_AUTO;
        item.registrationDate = user.getRegisteredOn();
        item.approvalDate = user.getApprovedOn();
        item.invoice = null;
        item.invoicedAmount = 0;
        return item;
    }
    
    public static DueItem newRVItem (User user) {
        if (user.getState() != UserState.APPROVED || user.getApprovedOn() == null)
            throw new IllegalArgumentException("Cannot create due item: User is not approved.");
        if (!user.isRegistrationComplete())
            throw new IllegalArgumentException("Cannot create due item: User registration form is not complete.");
        if (user.getRegistration().getRvType() == RVSelection.NEED_CLARIFICATION)
            throw new IllegalArgumentException("Cannot create due item: User needs to clarify RV status in registration form.");
        DueItem item = new DueItem();
        item.type = TYPE_RV;
        item.paymentRequired = (user.getRegistration().getRvType() == RVSelection.RESPONSIBLE);
        item.amount = AMOUNT_AUTO;
        item.registrationDate = user.getRegisteredOn();
        item.approvalDate = user.getApprovedOn();
        item.invoice = null;
        item.invoicedAmount = 0;
        return item;
    }
    
    public long getItemId () {
        return itemId;
    }
    
    void setPaymentRequired (boolean required) {
        paymentRequired = required;
    }
    
    public boolean isOutstanding () {
        
        return (paymentRequired && invoice != null);
        
    }
    
    public DueCalculator.Amount calculateAmountOwed () {
        
        if (!isOutstanding())
            return null;
        else if (amount >= 0)
            return new DueCalculator.Amount("Special Rate", amount);
        else
            return DueCalculator.calculateAmount(registrationDate, approvalDate, type);
        
    }
   
}
