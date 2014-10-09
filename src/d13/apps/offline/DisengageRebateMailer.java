package d13.apps.offline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;

import d13.notify.Email;
import d13.util.HibernateUtil;

public class DisengageRebateMailer {

    public static void main(String[] args) throws Exception {

        Email.Configuration config = loadEmailConfig();
        BufferedReader in = new BufferedReader(new FileReader("2014_disengage_rebates.csv"));
        String line;
        List<DisengageRebateInfo> rebates = new ArrayList<DisengageRebateInfo>();
        
        while ((line = in.readLine()) != null) {
            
            String[] tokens = line.replaceAll("\"", "").split(",");
            if (tokens.length < 7) {
                System.out.println("SKIP: " + Arrays.toString(tokens));
                continue;
            }
            
            DisengageRebateInfo ri = new DisengageRebateInfo();
            ri.name = tokens[0].trim();
            ri.email = tokens[2].trim();
            ri.sunday = tokens[3].trim().equalsIgnoreCase("x");
            ri.monday = tokens[4].trim().equalsIgnoreCase("x");
            ri.tuesday = tokens[5].trim().equalsIgnoreCase("x");
            ri.wednesday = tokens[6].trim().equalsIgnoreCase("x");
            
            if (ri.name.isEmpty() || ri.email.isEmpty() || (!ri.sunday && !ri.monday && !ri.tuesday && !ri.wednesday)) {
                System.out.println("SKIP: " + Arrays.toString(tokens));
                continue;
            }
            
            rebates.add(ri);
            
        }
      
        List<DisengageRebateInfo> notsent = new ArrayList<DisengageRebateInfo>();
        
        for (DisengageRebateInfo ri : rebates) {
            
            //System.out.println("REBATE: " + ri);
            
            try {
                sendMail(ri, config);
                System.out.println("MAIL SENT:   " + ri);
            } catch (Exception x) {
                notsent.add(ri);
                System.err.println("MAIL FAILED: " + ri);
                System.err.println("             " + x.getMessage());
            }
            
        }
        
        for (DisengageRebateInfo ri : notsent) {
            System.err.println("NOT SENT: " + ri);
        }

        /*DisengageRebateInfo test = new DisengageRebateInfo();
        test.email = "testd14dis@yopmail.com";
        test.name = "Jason Cipriani ABC";
        test.sunday = true;
        test.monday = true;
        test.tuesday = true;
        test.wednesday = true;
        sendMail(test, config);*/
        
    }
    
    static void sendMail (DisengageRebateInfo ri, Email.Configuration config) throws Exception {

        //DisengageRebateEmail.sendNow(ri, config);
        
    }
    
    static Email.Configuration loadEmailConfig () throws Exception {
        
        Session session = HibernateUtil.openSession();
        Email.Configuration config = Email.Configuration.fromDatabase(session);
        session.close();
        
        return config;

    }

}
