package d13.notify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.markdown4j.Markdown4jProcessor;

import d13.dao.RuntimeOptions;
import d13.dao.User;
import d13.util.Util;

public abstract class Email {
/*
    RuntimeOptions.setOption("notify.smtp_auth", "1");
    RuntimeOptions.setOption("notify.smtp_tls", "0");
    RuntimeOptions.setOption("notify.smtp_ssl", "0");
    RuntimeOptions.setOption("notify.smtp_host", "mail.disorient.info");
    RuntimeOptions.setOption("notify.smtp_port", "25");
    RuntimeOptions.setOption("notify.smtp_user", "camp@disorient.info");
    RuntimeOptions.setOption("notify.smtp_password", "the7ages");
    RuntimeOptions.setOption("notify.mail_from", "camp@disorient.info");
    RuntimeOptions.setOption("notify.base_url", "http://camp.disorient.info");
*/
    
    public static final String RT_SMTP_AUTH = "notify.smtp_auth";
    public static final String RT_SMTP_TLS = "notify.smtp_tls";
    public static final String RT_SMTP_SSL = "notify.smtp_ssl";
    public static final String RT_SMTP_HOST = "notify.smtp_host";
    public static final String RT_SMTP_PORT = "notify.smtp_port";
    public static final String RT_SMTP_USER = "notify.smtp_user";
    public static final String RT_SMTP_PASSWORD ="notify.smtp_password";
    public static final String RT_MAIL_FROM = "notify.mail_from";
    public static final String RT_MAIL_REPLY_TO = "notify.reply_to";
    public static final String RT_MAIL_DEBUG = "notify.debug";
    public static final String RT_BASE_URL = "notify.base_url";
    public static final String RT_APPROVAL_CONTENT = "approval";
    public static final String RT_REJECTION_CONTENT = "rejection";
    public static final String RT_INVITE_CONTENT = "invite";
    
    private final Configuration config;
    
    public static class Configuration implements Cloneable {
    
        public static class EmailContents {
            public final String title;
            public final String body;
            EmailContents (String title, String body) { this.title = title; this.body = body; }
        }
        
        public boolean auth     = false;
        public boolean tls      = false;
        public boolean ssl      = false;
        public String  host     = "mail.disorient.info";
        public int     port     = 25;
        public String  user     = "camp@disorient.info";
        public String  password = null;
        public String  from     = "camp@disorient.info";
        public String  replyTo  = "camp@disorient.info";
        public String  baseUrl  = "http://camp.disorient.info";
        public boolean single   = false; // for mailer apps; if single then TO:recipient + CC:camp@, otherwise BCC recipients and no CC.
        public String  pwExpire = User.RT_PWRESET_EXPIRE_MINUTES_DEFAULT;
        public boolean debug    = false;
        private Map<String,EmailContents> contents = new HashMap<String,EmailContents>();
        // TODO: pwExpire is a bit of a hack; if we have to start referring to lots of other config
        //       options in email, it would be cleaner to pass the hibernate session to Email 
        //       constructors and query relevant options there. for now this is fine, there is only
        //       one. 
   
        public Configuration () {
        }
        
        @Override public Configuration clone () {
            Configuration c = new Configuration();
            c.auth = auth;
            c.tls = tls;
            c.ssl = ssl;
            c.host = host;
            c.port = port;
            c.user = user;
            c.password = password;
            c.from = from;
            c.replyTo = replyTo;
            c.baseUrl = baseUrl;
            c.single = single;
            c.pwExpire = pwExpire;
            c.debug = debug;
            c.contents = new HashMap<String,EmailContents>(contents);
            return c;
        }
        
        public static Configuration fromDatabase (org.hibernate.Session session) {
            Configuration c = new Configuration();
            c.auth = "1".equals(RuntimeOptions.getOption(RT_SMTP_AUTH, c.auth ? "1" : "0", session));
            c.tls = "1".equals(RuntimeOptions.getOption(RT_SMTP_TLS, c.tls ? "1" : "0", session));
            c.ssl = "1".equals(RuntimeOptions.getOption(RT_SMTP_SSL, c.ssl ? "1" : "0", session));
            c.host = RuntimeOptions.getOption(RT_SMTP_HOST, c.host, session);
            c.port = Util.parseIntDefault(RuntimeOptions.getOption(RT_SMTP_PORT, session), c.port);
            c.user = RuntimeOptions.getOption(RT_SMTP_USER, c.user, session);
            c.password = RuntimeOptions.getOption(RT_SMTP_PASSWORD, c.password, session);
            c.from = RuntimeOptions.getOption(RT_MAIL_FROM, c.from, session);
            c.replyTo = RuntimeOptions.getOption(RT_MAIL_REPLY_TO, c.replyTo, session);
            c.baseUrl = RuntimeOptions.getOption(RT_BASE_URL, c.baseUrl, session);
            c.pwExpire = RuntimeOptions.getOption(User.RT_PWRESET_EXPIRE_MINUTES, User.RT_PWRESET_EXPIRE_MINUTES_DEFAULT, session);
            c.debug = "1".equals(RuntimeOptions.getOption(RT_MAIL_DEBUG, c.debug ? "1" : "0", session));
            c.loadContents(RT_APPROVAL_CONTENT);
            c.loadContents(RT_REJECTION_CONTENT);
            c.loadContents(RT_INVITE_CONTENT);
            return c;
        }
        
        public String getPwExpire () {
            return pwExpire;
        }
        
        private void loadContents (String key) {
            String title = RuntimeOptions.getOption("notify.email." + key + ".title"); 
            String body = RuntimeOptions.getOption("notify.email." + key + ".body");
            contents.put(key, new EmailContents(title, body));
        }
        
        public String getContentTitle (String key) {
            EmailContents content = contents.get(key);
            String title = (content == null ? null : content.title);
            return (title == null ? "" : title);
        }
        
        public String getContentBody (String key) {
            EmailContents content = contents.get(key);
            String body = (content == null ? null : content.body);
            return (body == null ? "" : body);
        }
        
    }
    
    private static final ThreadLocal<Markdown4jProcessor> mdparser = new ThreadLocal<Markdown4jProcessor>() {
        @Override protected Markdown4jProcessor initialValue () {
            return new Markdown4jProcessor();
        }
    };

    public Email (Configuration c) {
        this.config = c;
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
        
        return config.baseUrl + "/" + url;
        
    }
    
    protected final String getContactEmail () {
        
        return config.replyTo;
        
    }
    
    protected final String replaceFields (String text, Map<String,String> replacements, boolean forMarkdown) {
        
        if (replacements != null)
            for (Map.Entry<String,String> r : replacements.entrySet())
                text = text.replace("{" + r.getKey() + "}", r.getValue());
        
        return text;

    }
    
    protected final String replaceFields (String text, Map<String,String> replacements) {
        return replaceFields(text, replacements, false);
    }
    
    protected final String convertMarkdown (String markdown, Map<String,String> replacements) {

        markdown = replaceFields(markdown, replacements, true);
        
        try {
            return "<html><body>" + mdparser.get().process(markdown) + "</body></html>";
        } catch (IOException x) {
            // won't happen
            return "";
        }
        
    }
    
    protected abstract String getSubject ();
    
    protected abstract String getBody ();

    protected boolean isHtml () {
        return false;
    }
    
    protected boolean ccFrom () {
        return false;
    }
    
    public final void send (List<InternetAddress> recipients) throws Exception {
   
        if (recipients.isEmpty())
            throw new Exception("No recipients.");
    
        // get javamail session
        Properties props = System.getProperties();
        String prefix = (config.ssl ? "mail.smtps" : "mail.smtp");
        props.setProperty(prefix + ".starttls.enable", config.tls ? "true" : "false");
        props.setProperty(prefix + ".auth", config.auth ? "true" : "false");
        props.setProperty(prefix + ".host", config.host);
        props.setProperty(prefix + ".port", Integer.toString(config.port));
        props.setProperty(prefix + ".connectiontimeout", "30000");
        props.setProperty(prefix + ".timeout", "30000");
        props.setProperty(prefix + ".writetimeout", "30000");
        Session session = Session.getDefaultInstance(props);
        session.setDebug(config.debug);
        
        // create message and header
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(config.from));            
        for (InternetAddress recipient:recipients) {
            System.out.println("Sending to " + recipient);
            message.addRecipient(config.single ? Message.RecipientType.TO : Message.RecipientType.BCC, recipient);
        }
        if (config.single || ccFrom()) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(config.from));
        }
        message.setReplyTo(new Address[] {new InternetAddress(config.replyTo)});
        
        // message subject and body
        message.setSubject(getSubject());
        message.setContent(getBody(), (isHtml() ? "text/html" : "text/plain") + "; charset=us-ascii");
        
        // send it
        Transport t = session.getTransport(config.ssl ? "smtps" : "smtp");
        t.connect(config.host, config.port, config.user, config.password);
        t.sendMessage(message, message.getAllRecipients());
        t.close();
        
        System.out.println("Notification email sent.");
                
    }
    
    public final void send (User ... recipients) throws Exception {
        
        List<InternetAddress> addrs = new ArrayList<InternetAddress>();
        for (User recipient:recipients)
            addRecipient(addrs, recipient);

        send(addrs);
        
    }
    
    public final void send (Collection<User> recipients) throws Exception {

        List<InternetAddress> addrs = new ArrayList<InternetAddress>();
        for (User recipient:recipients)
            addRecipient(addrs, recipient);

        send(addrs);
        
    }
        
}
