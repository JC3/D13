package d13.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
        
        for (DueCalculator.Tier tier:DueCalculator.getPersonalTiers(user.getRegisteredOn(), user.getGracePeriodStart(), when)) {
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

        for (DueCalculator.Tier tier:DueCalculator.getRVTiers(user.getRegisteredOn(), user.getGracePeriodStart(), when)) {
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

    
    public static List<User> getUsersWithOpenPersonalDues () {
        
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
        creator.addInvoice(invoice);
        
        int total = 0;
        for (PaymentItem p:pitems) {
            invoice.addInvoiceItem(p.due, p.invoiceAmount, p.description); 
            total += p.invoiceAmount;
        }
        
        invoice.setInvoiceAmount(total);
      
        HibernateUtil.getCurrentSession().save(invoice);
        
        return invoice;
        
    }
   
    
    private static String getRC (HttpResponse response) throws Exception {
        
        InputStream is = response.getEntity().getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String result = "", line = null;
        
        while ((line = br.readLine()) != null)
            result += line;
        
        return result;
        
    }

    /*
    item_number=8
    residence_country=US
    verify_sign=Ak.va-5qy2TtpfHuN7PrMDF0dq2cAawT9vFOOB0AfH4Rbye2pJFXdqHi
    business=jason.cipriani-facilitator@gmail.com
    payment_status=Completed
    transaction_subject=Disorient Camp Dues
    protection_eligibility=Ineligible
    shipping=0.00
    payer_id=MRJC8HSD6GAYG
    first_name=Dis
    payer_email=discamper@yopmail.com
    mc_fee=9.73
    txn_id=8NT380167Y0430610
    quantity=1
    receiver_email=jason.cipriani-facilitator@gmail.com
    notify_version=3.7
    txn_type=web_accept
    mc_gross=325.00
    payer_status=verified
    mc_currency=USD
    test_ipn=1
    custom=
    payment_date=00:46:12 Jul 20, 2013 PDT
    payment_fee=9.73
    charset=windows-1252
    payment_gross=325.00
    ipn_track_id=477cc4504af38
    tax=0.00
    handling_amount=0.00
    item_name=Disorient Camp Dues
    last_name=Camper
    payment_type=instant
    receiver_id=2NQX4D6LLGHV8*/
   
    private static void handlePaymentOK (HttpServletRequest request) throws Exception {
       
        Invoice invoice = Invoice.findById(Long.parseLong(request.getParameter("item_number")));
        String txn_id = request.getParameter("txn_id");
        
        // TODO: notify administrator about any of the errors below
        
        // filter out test mode
        if ("1".equals(request.getParameter("test_ipn")) && !"1".equals(RuntimeOptions.getOption("dues.allow_test", "0"))) {
            System.out.println("BILLING: " + txn_id + " " + invoice.getInvoiceId() + " ignoring test_ipn.");
            return;
        }
        
        // validate from address
        String paypal_receiver = RuntimeOptions.getOption("dues.paypal_email", "dues@disorient.info");
        String actual_receiver = request.getParameter("business");
        if (!paypal_receiver.equalsIgnoreCase(actual_receiver)) {
            System.out.println("BILLING: " + txn_id + " " + invoice.getInvoiceId() + " ignoring, receiver " + actual_receiver + " should be " + paypal_receiver);
            return;
        }
        
        // validate payment status
        if (!"completed".equalsIgnoreCase(request.getParameter("payment_status"))) {
            System.out.println("BILLING: " + txn_id + " " + invoice.getInvoiceId() + " ignoring payment status of " + request.getParameter("payment_status"));
            return;
        }
       
        // validate amount
        int amount = (int)(100.f * Float.parseFloat(request.getParameter("payment_gross")) + 0.5f);
        if (amount < invoice.getInvoiceAmount()) {
            System.out.println("BILLING: " + txn_id + " " + invoice.getInvoiceId() + " ignoring, amount " + amount + " should be " + invoice.getInvoiceAmount());
            return;
        }
        
        // validate invoice status
        if (invoice.getStatus() != Invoice.STATUS_IN_PROGRESS && invoice.getStatus() != Invoice.STATUS_PENDING) {
            // todo: email admin about this because it could be a duplicate payment
            System.out.println("BILLING: " + txn_id + " " + invoice.getInvoiceId() + " ignoring already complete invoice.");
            return;
        }
        
        // get fee
        int fee = (int)(100.0f * Float.parseFloat(request.getParameter("payment_fee")) + 0.5f);
        
        // mark invoice as paid
        invoice.setStatus(Invoice.STATUS_COMPLETE);
        invoice.setPaypalAmount(amount);
        invoice.setPaypalFee(fee);
        invoice.setPaypalSenderEmail(request.getParameter("payer_email"));
        invoice.setPaypalSenderFirstName(request.getParameter("first_name"));
        invoice.setPaypalSenderLastName(request.getParameter("last_name"));
        invoice.setPaypalTimestamp(request.getParameter("payment_date"));
        invoice.setPaypalTransactionId(txn_id);
        invoice.setPaypalTransactionStatus(request.getParameter("payment_status"));
        
        // mark individual items as paid
        for (InvoiceItem item:invoice.getItems()) {
            item.getDue().setOpen(false);
            item.getDue().setPaidInvoice(invoice);
        }
        
        // TODO: send dues received email
        
    }
    
    
    private static void handlePaymentFail (HttpServletRequest request, String response) throws Exception {
        
        // TODO: implement this. it's what happens if paypal response fails. should notify administrators.
        
    }
    
    
    public static void handleIPN (HttpServletRequest request) throws Exception {
        
        // todo: move default values to BillingManager (also move from dueconfirm.jsp)
        String paypal_site = RuntimeOptions.getOption("dues.paypal_site", "https://www.paypal.com/cgi-bin/webscr");

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(paypal_site);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        StringBuilder logparams = new StringBuilder();
        
        // construct response
        params.add(new BasicNameValuePair("cmd", "_notify-validate")); //You need to add this parameter to tell PayPal to verify
        for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
            String name = e.nextElement();
            String value = request.getParameter(name);
            params.add(new BasicNameValuePair(name, value));
            logparams.append(name).append("=").append(value).append("&");
        }
        post.setEntity(new UrlEncodedFormEntity(params));
        
        // submit and read reply
        String rc;
        try {
            rc = getRC(client.execute(post)).trim();
            if ("VERIFIED".equals(rc))
                handlePaymentOK(request);
            else
                handlePaymentFail(request, rc);
        } catch (Exception t) {
            RawIPNLogEntry.addEntry(logparams.toString(), t.getMessage());
            throw t;
        }
         
        // log for our records
        RawIPNLogEntry.addEntry(logparams.toString(), rc);
        
    }
   
    
}
