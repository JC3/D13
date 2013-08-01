package d13.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;

import d13.dao.DueCalculator;
import d13.dao.DueItem;
import d13.dao.Invoice;
import d13.dao.InvoiceItem;
import d13.dao.User;
import d13.dao.UserSearchFilter;

public class FinanceReport {
    
    private boolean failed;
    private final List<DuesByUser> duesByUser = new ArrayList<DuesByUser>();
    private final Totals totals = new Totals();
    private List<Invoice> invoices;
    
    public static class DuesByUser implements Comparable<DuesByUser> {
        public User user;
        public boolean personalPaid;
        public int personalPaidAmount;
        public int personalOwedAmount;
        public String personalTierName;
        public DateTime personalPaidDate;
        public User personalPaidBy;
        public boolean rvOwed;
        public boolean rvPaid;
        public int rvPaidAmount;
        public int rvOwedAmount;
        public DateTime rvPaidDate;
        public User rvPaidBy;
        @Override public int compareTo (DuesByUser o) {
            if (o == this)
                return 0;
            if (o == null)
                return -1;
            // unpaid users come first
            boolean tpaid = personalPaid && (rvPaid || !rvOwed);
            boolean opaid = o.personalPaid && (o.rvPaid || !o.rvOwed);
            if (tpaid != opaid)
                return tpaid ? 1 : -1;
            // sort by personal tier
            if (!personalTierName.equals(o.personalTierName))
                return personalTierName.compareTo(o.personalTierName);
            // then by personal amount owed
            if (personalOwedAmount != o.personalOwedAmount)
                return personalOwedAmount < o.personalOwedAmount ? -1 : 1;
            // then by rv amount owed
            if (rvOwedAmount != o.rvOwedAmount)
                return rvOwedAmount < o.rvOwedAmount ? -1 : 1;
            // then by payment date
            int cmp = dcmp(personalPaidDate, o.personalPaidDate);
            if (cmp != 0)
                return cmp;
            // then by user's name
            return user.getRealName().compareToIgnoreCase(o.user.getRealName());
        }
        private static final int dcmp (DateTime a, DateTime b) {
            if (a == b)
                return 0;
            else if (a == null || b == null)
                return a == null ? -1 : 1;
            else
                return a.compareTo(b);
        }
    }
    
    public static class Totals {
        public int personalTotal;
        public int personalPaid;
        public int rvTotal;
        public int rvPaid;
        public Map<String,Integer> personalByTier = new TreeMap<String,Integer>();
        public int paypalFees;
    }
     
    public FinanceReport (SessionData session) {
        
        if (!session.isLoggedIn() || !session.getUser().getRole().canViewUsers()) {
            failed = true;
            return; // permission denied
        }
        
        List<User> users = UserSearchFilter.quickFilter(UserSearchFilter.QUICK_APPROVED);
        computeDuesByUser(users);
        
        invoices = Invoice.findAllPaid();
        
        totals.paypalFees = 0;
        for (Invoice invoice:invoices)
            totals.paypalFees += invoice.getPaypalFee();
        
    }
    
    public boolean isFailed () {
        return failed;
    }

    public List<DuesByUser> getDuesByUser () {
        return duesByUser;
    }
    
    public Totals getTotals () {
        return totals;
    }
    
    public List<Invoice> getInvoices () {
        return invoices;
    }
    
    private void computeDuesByUser (List<User> users) {

        for (User u:users) {

            DuesByUser d = new DuesByUser();
            d.user = u;
            
            if (u.isPersonalPaid()) {
                DueItem due = u.getPersonalDueItem();
                Invoice inv = u.getPersonalDueItem().getPaidInvoice();
                InvoiceItem invitem = (inv == null ? null : inv.findItem(due));
                d.personalPaid = true;
                d.personalPaidAmount = (invitem == null ? 0 : invitem.getAmount());
                if (due.getCustomAmount() < 0) {
                    DueCalculator.Amount a = DueCalculator.calculateAmount(u.getRegisteredOn(), u.getGracePeriodStart(), DueCalculator.TYPE_PERSONAL, inv.getCreated());
                    d.personalOwedAmount = a.getAmount();
                    d.personalTierName = a.getTierName();
                } else {
                    d.personalOwedAmount = due.getCustomAmount();
                    d.personalTierName = "Custom";
                }
                d.personalPaidDate = u.getPersonalPaidDate();
                d.personalPaidBy = (inv == null ? null : inv.getCreator());
            } else {
                DueItem due = u.getPersonalDueItem();
                d.personalPaid = false;
                d.personalPaidAmount = 0;
                if (due.getCustomAmount() < 0) {
                    DueCalculator.Amount a = DueCalculator.calculateAmount(u.getRegisteredOn(), u.getGracePeriodStart(), DueCalculator.TYPE_PERSONAL);
                    d.personalOwedAmount = a.getAmount();
                    d.personalTierName = a.getTierName();
                } else {
                    d.personalOwedAmount = due.getCustomAmount();
                    d.personalTierName = "Custom";
                }
                d.personalPaidDate = null;
                d.personalPaidBy = null;
            }

            d.rvOwed = u.getRvDueItem().isActive();
            if (d.rvOwed && u.isRvPaid()) {
                DueItem due = u.getRvDueItem();
                Invoice inv = u.getRvDueItem().getPaidInvoice();
                InvoiceItem invitem = (inv == null ? null : inv.findItem(due));
                d.rvPaid = true;
                d.rvPaidAmount = (invitem == null ? 0 : invitem.getAmount());
                if (due.getCustomAmount() < 0)
                    d.rvOwedAmount = DueCalculator.calculateAmount(u.getRegisteredOn(), u.getGracePeriodStart(), DueCalculator.TYPE_RV, inv.getCreated()).getAmount();
                else
                    d.rvOwedAmount = due.getCustomAmount();
                d.rvPaidDate = u.getRvPaidDate();
                d.rvPaidBy = (inv == null ? null : inv.getCreator());
            } else {
                DueItem due = u.getRvDueItem();
                d.rvPaid = false;
                d.rvPaidAmount = 0;
                if (!d.rvOwed)
                    d.rvOwedAmount = 0;
                else if (due.getCustomAmount() < 0)
                    d.rvOwedAmount = DueCalculator.calculateAmount(u.getRegisteredOn(), u.getGracePeriodStart(), DueCalculator.TYPE_RV).getAmount();
                else
                    d.rvOwedAmount = due.getCustomAmount();
                d.rvPaidDate = null;
                d.rvPaidBy = null;
            }
    
            duesByUser.add(d);
    
            // totals
            totals.personalPaid += d.personalPaidAmount;
            totals.personalTotal += d.personalOwedAmount;
            totals.rvPaid += d.rvPaidAmount;
            totals.rvTotal += d.rvOwedAmount;
            Integer cval = totals.personalByTier.get(d.personalTierName);
            totals.personalByTier.put(d.personalTierName, (cval == null ? 0 : cval) + d.personalOwedAmount);
            
        }

        Collections.sort(duesByUser);
        
    }
    
}
