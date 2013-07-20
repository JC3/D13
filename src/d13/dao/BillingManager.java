package d13.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import d13.util.HibernateUtil;

public class BillingManager {

   
    public static class PaymentChoice {
        public String description;
        public int amount;
    }
    
    
    public static class PaymentItem {
        public DueItem due;
        public String field;
        public String description;
        public int minimumAmount;
        public List<PaymentChoice> choices = new ArrayList<PaymentChoice>();
        private int invoiceAmount; // internal use by createInvoiceFromInput
    }
    
    
    private static PaymentItem getPersonalPaymentItem (User user, DateTime when) {
    
        DueItem due = user.getPersonalDueItem();
        if (!due.isPayable())
            return null;
        
        PaymentItem item = new PaymentItem();
        item.due = due;
        item.field = "personal";
        item.description = "Camper Fee";
        
        int customAmount = 0;
        if (due.isCustom()) {
            PaymentChoice choice = new PaymentChoice();
            choice.description = "Custom Rate";
            choice.amount = due.getCustomAmount();
            customAmount = due.getCustomAmount();
            item.choices.add(choice);
        }
        
        for (DueCalculator.Tier tier:DueCalculator.getPersonalTiers(user.getRegisteredOn(), user.getApprovedOn(), when)) {
            if (tier.getAmount() >= customAmount) {
                PaymentChoice choice = new PaymentChoice();
                choice.description = tier.getName();
                choice.amount = tier.getAmount();
                item.choices.add(choice);
            }
        }
        
        if (!item.choices.isEmpty())
            item.minimumAmount = item.choices.get(0).amount;
        
        return item;
        
    }
    
    
    private static PaymentItem getRVPaymentItem (User user, DateTime when) {
        
        DueItem due = user.getRvDueItem();
        if (!due.isPayable())
            return null;
        
        PaymentItem item = new PaymentItem();
        item.due = due;
        item.field = "rv";
        item.description = "RV Fee";
        
        int customAmount = 0;
        if (due.isCustom()) {
            PaymentChoice choice = new PaymentChoice();
            choice.description = "Custom Rate";
            choice.amount = due.getCustomAmount();
            customAmount = due.getCustomAmount();
            item.choices.add(choice);
        }
        
        DueCalculator.Tier tier = DueCalculator.getRVTier();
        if (tier.getAmount() >= customAmount) {
            PaymentChoice choice = new PaymentChoice();
            choice.description = tier.getName();
            choice.amount = tier.getAmount();
            item.choices.add(choice);
        }

        if (!item.choices.isEmpty())
            item.minimumAmount = item.choices.get(0).amount;
        
        return item;
        
    }
    
    
    public static List<PaymentItem> getOpenItems (User user, DateTime when) {
        
        List<PaymentItem> items = new ArrayList<PaymentItem>();
        
        PaymentItem p = getPersonalPaymentItem(user, when);
        if (p != null) items.add(p);
        
        p = getRVPaymentItem(user, when);
        if (p != null) items.add(p);
        
        return items;
        
    }
    
    
    public static List<PaymentItem> getOpenItems (User user) {
        
        return getOpenItems(user, DateTime.now());
        
    }
    /*
    public static List<User> findUnpaidPersonalDues () {
 
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createQuery("from User as user where user.state = " + UserState.APPROVED.toDBId() + " and user.personalDue.paymentRequired = true and user.personalDue.paymentComplete = false order by lower(user.realName) asc")
                .list();
        
        return users;
        
    }
    
    public static List<User> findUnpaidRVDues () {
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createQuery("from User as user where user.state = " + UserState.APPROVED.toDBId() + " and user.rvDue.paymentRequired = true and user.rvDue.paymentComplete = false order by lower(user.realName) asc")
                .list();
        
        return users;
   
    }
    */
   
    
    public static List<User> getUsersWithOpenPersonalDues () {
       
        /*
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createQuery("from User as user where user.state = " + UserState.APPROVED.toDBId() + " and user.personalDue.active = true and (user.personalDue.paidInvoice is not null or user.personalDue.customAmount = 0) order by lower(user.realName) asc")
                .list();
        */
        
        @SuppressWarnings("unchecked")
        List<User> all = (List<User>)HibernateUtil.getCurrentSession()
                .createQuery("from User as user where user.state = " + UserState.APPROVED.toDBId() + " order by lower(user.realName) asc")
                .list();
        
        List<User> users = new ArrayList<User>();
        for (User user:all)
            if (user.getPersonalDueItem().isPayable())
                users.add(user);                

        return users;
        
    }
    
    
    public static List<User> getUsersWithOpenRVDues () {
    
        @SuppressWarnings("unchecked")
        List<User> all = (List<User>)HibernateUtil.getCurrentSession()
                .createQuery("from User as user where user.state = " + UserState.APPROVED.toDBId() + " order by lower(user.realName) asc")
                .list();
        
        List<User> users = new ArrayList<User>();
        for (User user:all)
            if (user.getRvDueItem().isPayable())
                users.add(user);                

        return users;
        
    }
    
    
    public static class NewInvoiceFormInput {

        public int personalAmount;
        public int rvAmount;
        public List<Long> personalUsers = new ArrayList<Long>();
        public List<Long> rvUsers = new ArrayList<Long>();
        
    }
   
    
    private static void createNewInvoiceItem (List<PaymentItem> items, long userId, int type, DateTime when) {
       
        User user = User.findById(userId);
        
        PaymentItem pitem;
        if (type == DueCalculator.TYPE_PERSONAL)
            pitem = getPersonalPaymentItem(user, when);
        else if (type == DueCalculator.TYPE_RV)
            pitem = getRVPaymentItem(user, when);
        else
            pitem = null;
        
        if (pitem == null)
            return;
        
        pitem.invoiceAmount = pitem.minimumAmount;
        items.add(pitem);
        
    }
    
    
    public static Invoice createInvoiceFromInput (User creator, NewInvoiceFormInput input) {
      
        DateTime now = DateTime.now();
        List<PaymentItem> pitems = new ArrayList<PaymentItem>();
        PaymentItem pitem;
      
        // validate all amounts and build item list
        
        pitem = getPersonalPaymentItem(creator, now);
        if (pitem == null) {
            if (input.personalAmount != 0)
                throw new IllegalArgumentException("Personal due is not payable at this time.");
        } else if (input.personalAmount < pitem.minimumAmount)
            throw new IllegalArgumentException("Personal due amount is too low.");
        else {
            pitem.invoiceAmount = input.personalAmount;
            pitems.add(pitem);
        }
        
        pitem = getRVPaymentItem(creator, now);
        if (pitem == null) {
            if (input.rvAmount != 0)
                throw new IllegalArgumentException("RV due is not payable at this time.");
        } else if (input.rvAmount < pitem.minimumAmount)
            throw new IllegalArgumentException("RV due amount is too low.");
        else {
            pitem.invoiceAmount = input.rvAmount;
            pitems.add(pitem);
        }
       
        Set<Long> used = new HashSet<Long>();
        for (Long userId:input.personalUsers) {
            if (userId != null && !used.contains(userId)) {
                createNewInvoiceItem(pitems, userId, DueCalculator.TYPE_PERSONAL, now);
                used.add(userId);
            }
        }

        used.clear();
        for (Long userId:input.rvUsers) {
            if (userId != null && !used.contains(userId)) {
                createNewInvoiceItem(pitems, userId, DueCalculator.TYPE_RV, now);
                used.add(userId);
            }
        }
        
        // create invoice
        
        Invoice invoice = new Invoice();
        invoice.setStatus(Invoice.STATUS_IN_PROGRESS);
        invoice.setCreator(creator);
        invoice.setCreated(now);
  
        int total = 0;
        for (PaymentItem p:pitems) {
            invoice.addInvoiceItem(p.due, p.invoiceAmount, p.description); 
            total += p.invoiceAmount;
        }
        
        invoice.setInvoiceAmount(total);
      
        HibernateUtil.getCurrentSession().save(invoice);
        
        return invoice;
        
    }
    
    
}
