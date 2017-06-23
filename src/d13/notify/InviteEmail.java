package d13.notify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import d13.dao.Invite;
import d13.web.DefaultDataConverter;

public class InviteEmail extends Email {

    private final String subject;
    private final String body;
    
    public static final String[][] FIELD_INFO = {
        { "invitecode", "Invite code" },
        { "campemail", "Camp email address" },
        { "rsvpurl", "RSVP URL" },
        { "loginurl", "Login URL" },
        { "realname", "User real name" },
        { "expires1", "Description coming soon (maybe), or ask Jason." },
        { "expires2", "Description coming soon (maybe), or ask Jason." }
    };
    
    public InviteEmail (Invite invite, Configuration c) {

        super(c);
                
        String expires1, expires2;
        if (invite.getExpiresOn() != null) {
            expires1 = "This code expires on " + DefaultDataConverter.objectAsString(invite.getExpiresOn()) + "! ";
            expires2 = "Again, your invitation code will expire on " + DefaultDataConverter.objectAsString(invite.getExpiresOn()) + ", please respond before then. Additionally, if ";
        } else {
            expires1 = "";
            expires2 = "If ";
        }

        Map<String,String> fields = new HashMap<String,String>();
        fields.put("invitecode", invite.getInviteCode());
        fields.put("campemail", getContactEmail());
        fields.put("rsvpurl", makeURL("rsvp.jsp?code=" + invite.getInviteCode()));
        fields.put("loginurl", makeURL(""));
        fields.put("realname", invite.getInviteeName());
        fields.put("expires1", expires1);
        fields.put("expires2", expires2);
        
        subject = replaceFields(c.getContentTitle(RT_INVITE_CONTENT), fields);
        body = convertMarkdown(c.getContentBody(RT_INVITE_CONTENT), fields);
        
    }

    @Override protected String getSubject () {
        return subject;
    }

    @Override protected String getBody () {
        return body;
    }

    @Override protected boolean isHtml () {
        return true;
    }
    
    @Override protected boolean ccFrom () {
        return true;
    }

    public static final void sendNow (Invite invite, Configuration c) throws Exception {
        InviteEmail email = new InviteEmail(invite, c);
        List<InternetAddress> to = new ArrayList<InternetAddress>();
        to.add(new InternetAddress(invite.getInviteeEmail(), invite.getInviteeName()));
        to.get(0).validate();
        email.send(to);
    }

}
