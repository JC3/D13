package d13.notify;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import d13.dao.User;

public class RegistrationEmail {
    
    private static final String SMTP_SERVER = "mail.disorient.info";
    //private static final int SMTP_PORT = 25;
    private static final String SMTP_USERNAME = "camp@disorient.info";
    private static final String SMTP_PASSWORD = "the7ages";
    private static final String SMTP_FROM = "camp@disorient.info";
    
    private static final String MAIL_URL_BASE = "http://camp.disorient.info";
    
    
    public static void sendNotification (User registrant) {
        
        List<String> recipients = new ArrayList<String>();
        for (User user:User.findAdmins())
            recipients.add(user.getEmail());
        
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", SMTP_SERVER);
        
        Session session = Session.getDefaultInstance(props);
        
        try {
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_FROM));
            for (String recipient:recipients) {
                System.out.println("Sending to " + recipient);
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
            }
            message.setSubject("REGISTRATION: A new user has registered.");
            
            String url = MAIL_URL_BASE + "/details.jsp?u=" + registrant.getUserId();
            
            StringBuilder sb = new StringBuilder();
            sb.append("A new user has completed their registration form. The form must be reviewed before they can be approved/rejected.\n\n");
            sb.append("Name: ").append(registrant.getRealName()).append("\n");
            sb.append("Details: ").append(url).append("\n");
            message.setText(sb.toString());
            
            Transport t = session.getTransport("smtp");
            t.connect(SMTP_SERVER, SMTP_USERNAME, SMTP_PASSWORD);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            System.out.println("Notification email sent.");
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
    }


    public static void sendNotificationAdmissions (User registrant) {
        
        List<String> recipients = new ArrayList<String>();
        for (User user:User.findAdmissions())
            recipients.add(user.getEmail());
        
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", SMTP_SERVER);
        
        Session session = Session.getDefaultInstance(props);
        
        try {
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_FROM));
            for (String recipient:recipients) {
                System.out.println("Sending to " + recipient);
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
            }
            message.setSubject("REGISTRATION: A user is ready to be reviewed.");
            
            String url = MAIL_URL_BASE + "/details.jsp?u=" + registrant.getUserId();
            
            StringBuilder sb = new StringBuilder();
            sb.append("A registration application is ready to be reviewed.\n\n");
            sb.append("Name: ").append(registrant.getRealName()).append("\n");
            sb.append("Details: ").append(url).append("\n");
            message.setText(sb.toString());
            
            Transport t = session.getTransport("smtp");
            t.connect(SMTP_SERVER, SMTP_USERNAME, SMTP_PASSWORD);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            System.out.println("Notification email sent.");
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
    }


}
