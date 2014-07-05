package d13.tests;

import java.util.List;

import d13.dao.Invoice;
import d13.dao.RawIPNLogEntry;
import d13.util.HibernateUtil;

public class UpdatePaypalFees {

    public static Invoice getInvoice (long id, List<Invoice> invoices) {
        
        for (Invoice i:invoices)
            if (i.getInvoiceId() == id)
                return i;
        
        return null;
        
    }
    
    public static final void main (String[] args) throws Exception {
        
        HibernateUtil.beginTransaction();
        
        List<Invoice> invoices = Invoice.findAllPaid();
        List<RawIPNLogEntry> ipns = RawIPNLogEntry.findAll();
       
        for (RawIPNLogEntry ipn:ipns) {
            
            String item_number_str = null, payment_fee_str = null, payment_status = null;
            
            String[] params = ipn.getData().split("&");
            for (String param:params) {
                String[] parts = param.split("=", 2);
                String key = (parts.length >= 1) ? parts[0] : null;
                String value = (parts.length >= 2) ? parts[1] : null;
                if ("payment_fee".equalsIgnoreCase(key))
                    payment_fee_str = value;
                else if ("item_number".equalsIgnoreCase(key))
                    item_number_str = value;
                else if ("payment_status".equalsIgnoreCase(key))
                    payment_status = value;
            }
            
            if (payment_status == null) {
                System.out.println("payment_status not found.");
                continue;
            }
            
            if (!"completed".equalsIgnoreCase(payment_status)) {
                System.out.println("ignoring notification with status " + payment_status);
                continue;
            }
            
            if (payment_fee_str == null || item_number_str == null) {
                System.out.println("payment_fee or item_number not found.");
                continue;
            }
            
            long item_number = Long.parseLong(item_number_str);
            int payment_fee = (int)(100.0f * Float.parseFloat(payment_fee_str) + 0.5f);
            Invoice invoice = getInvoice(item_number, invoices);
            
            if (invoice == null) {
                System.out.println("invoice number " + item_number + " not found.");
                continue;
            }
            
            if (invoice.getPaypalFee() != 0) {
                System.out.println("warning: invoice number " + item_number + " already has fee set to " + invoice.getPaypalFee());
            }
            
            System.out.println("setting invoice " + item_number + " fee to " + payment_fee);
            invoice.setPaypalFee(payment_fee);
            
        }
        
        HibernateUtil.commitTransaction();
        
    }
    
}
