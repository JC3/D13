package d13.notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import d13.dao.User;

public abstract class Email {

    // TODO: configuration options in database or something
    private static final String SMTP_SERVER = "mail.disorient.info";
    //private static final int SMTP_PORT = 25;
    private static final String SMTP_USERNAME = "camp@disorient.info";
    private static final String SMTP_PASSWORD = "the7ages";
    private static final String SMTP_FROM = "camp@disorient.info";    
    private static final String MAIL_URL_BASE = "http://camp.disorient.info/";

    public Email () {
    }
     
    private static void addRecipient (List<InternetAddress> recipients, User user) {
        
        try {
            
            if (user == null)
                throw new Exception("Null user specified.");
            
            InternetAddress addr = new InternetAddress(user.getEmail());
            addr.validate();          
            recipients.add(addr);
            
        } catch (Throwable t) {
            
            System.err.println("Email Warning: " + t.getClass().getName() + ": " + t.getMessage());
            return;
            
        }
        
    }
    
    protected final String makeURL (String url) {
        
        return MAIL_URL_BASE + url;
        
    }
    
    protected abstract String getSubject ();
    
    protected abstract String getBody ();

    private final void send (List<InternetAddress> recipients) {

        /*
        
        try {
            
            if (recipients.isEmpty())
                throw new Exception("No recipients.");
        
            // get javamail session
            Properties props = System.getProperties();
            props.setProperty("mail.smtp.host", SMTP_SERVER);
            Session session = Session.getDefaultInstance(props);
            
            // create message and header
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_FROM));            
            for (InternetAddress recipient:recipients) {
                System.out.println("Sending to " + recipient);
                message.addRecipient(Message.RecipientType.BCC, recipient);
            }
            
            // message subject and body
            message.setSubject(getSubject());
            message.setText(getBody());

            // send it
            Transport t = session.getTransport("smtp");
            t.connect(SMTP_SERVER, SMTP_USERNAME, SMTP_PASSWORD);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            
            System.out.println("Notification email sent.");
                        
        } catch (Throwable t) {
            
            System.err.println("Email Warning: " + t.getClass().getName() + ": " + t.getMessage());
            return;

        }
        
        */
        
    }
    
    public final void send (User ... recipients) {
        
        List<InternetAddress> addrs = new ArrayList<InternetAddress>();
        for (User recipient:recipients)
            addRecipient(addrs, recipient);

        send(addrs);
        
    }
    
    public final void send (Collection<User> recipients) {

        List<InternetAddress> addrs = new ArrayList<InternetAddress>();
        for (User recipient:recipients)
            addRecipient(addrs, recipient);

        send(addrs);
        
    }
    
}
