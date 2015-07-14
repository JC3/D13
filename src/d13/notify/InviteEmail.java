package d13.notify;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import d13.dao.Invite;

public class InviteEmail extends Email {

    private final String subject;
    private final String body;
    
    public InviteEmail (Invite invite, Configuration c) {
        super(c);
        
        subject = "InviteEmail (" + invite.getInviteeName() + ")";
        body = "Name: " + invite.getInviteeName() + "\n" +
               "Code: " + invite.getInviteCode() + "\n";
        
    }

    @Override protected String getSubject () {
        return subject;
    }

    @Override protected String getBody () {
        return body;
    }

    @Override protected boolean isHtml () {
        return false;
    }

    public static final void sendNow (Invite invite, Configuration c) throws Exception {
        InviteEmail email = new InviteEmail(invite, c);
        List<InternetAddress> to = new ArrayList<InternetAddress>();
        to.add(new InternetAddress(invite.getInviteeEmail(), invite.getInviteeName()));
        to.get(0).validate();
        email.send(to);
    }

}
