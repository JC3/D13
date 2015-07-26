package d13.notify;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import d13.dao.Invite;
import d13.util.Util;

public class InviteEmail extends Email {

    private final String subject;
    private final String body;
    
    public InviteEmail (Invite invite, Configuration c) {

        super(c);
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("<html><body>");
/*        
        sb.append("<div itemscope itemtype=\"http://schema.org/EmailMessage\">");
        sb.append("<div itemprop=\"potentialAction\" itemscope itemtype=\"http://schema.org/ViewAction\">");
        sb.append("  <link itemprop=\"target\" href=\"[rsvpurl]\"/>");
        sb.append("  <meta itemprop=\"name\" content=\"RSVP\"/>");
        sb.append("</div>");
        sb.append("<meta itemprop=\"description\" content=\"Respond to your invite.\"/>");
        sb.append("</div>");
  */    
        sb.append("<p>Dear [realname],</p>");
        sb.append("<p>Preparations continue for our annual pilgrimage. This message is sent with love and a dedication to radical self-reliance. May you have your best Burn yet.</p>");
        sb.append("<p>In 2015 Disorient, we will be conducting a social experiment of our own. Disorient will ReOrient for 2015 and move to a smaller 150 person camp with an invite-only system. This is an exercise in going \"Tighter and Brighter\" as our camp population will be radically reduced to fine-tune, document, and share our best practices. Our collective experience enables us to envision a better camp on many levels. There are no tourists in D15ORIENT.</p>");
        sb.append("<p>This is an invitation to apply to join us in ReOrienting. Unfortunately, we cannot guarantee a spot in camp for all applicants. It is up to all of us to build our camp together, and the best way you can become involved this year is to participate and commit to creating Disorient for you and your fellow Disorienters.</p>");
        sb.append("<p>Here are some of the important roles available this year. If you think you can help in any of these areas, please sign up for the related cells on the registration application:</p>");
        sb.append("<ul>");
        sb.append("<li><u>Alpha Team</u>: Early arrival and build, from dust to Disorient.");
        sb.append("<li><u>Disengage Team</u>: Stay late and disengage, from Disorient to dust. Enjoy a trip to the hot springs, and get rebates on your dues.");
        sb.append("<li>Many <u>other</u> key roles, details on the web site (Stage managers, volunteer coordinators, LNT, build teams, and many more).");
        sb.append("</ul>");
        sb.append("<p>This personal invitation to apply is for YOU only! YOU MUST ACT NOW to accept or reject the invitation AND complete the application process. Your unique invitation code is:</p>");
        sb.append("<p><b>[invitecode]</b></p>");
        sb.append("<p>This code expires on August 1st! Follow these instructions to get started:</p>");
        sb.append("<ol>");
        sb.append("<li>Visit <a href=\"[rsvpurl]\">[rsvpurl]</a> to accept or reject this invitation. This link contains your personal unique invite code, so please do not share it!");
        sb.append("<li>Log in or create a new account, and complete the application form.");
        sb.append("</ol>");
        sb.append("<p>Alternatively, you may visit the registration site at <a href=\"[loginurl]\">[loginurl]</a>, log in, and enter your invitation code directly to accept.</p>");
        sb.append("<p>Again, your invitation code will expire on August 1st, please respond before then. Additionally, if you do not intend to join Disorient on the playa this year, we ask that you visit the above link and reject the invite to keep things flowing smoothly!</p>");
        sb.append("<p>Once you complete the above process, your application will be considered and you will be notified of your status as soon as possible. Please keep an eye on your email in case any clarification is needed and the registration team needs to contact you!</p>");
        sb.append("<p>While this invitation may seem like a departure from radical inclusion it is actually the exact opposite. We will have 100% participation from each camper to make 2015 a banner year.  Everyone's sleeves are rolled up and ready to help create our tightest and brightest year yet.</p>");
        sb.append("<p>If you have any questions, feel free to contact [campemail] or reply to this email.</p>");
        sb.append("<p>Thanks for helping to make D15ORIENT tighter and brighter than ever before, and see you on the playa!</p>");
        
        sb.append("</body></html>");

        String realname = Util.html(invite.getInviteeName());
        String invitecode = Util.html(invite.getInviteCode());
        String campemail = Util.html(getContactEmail());
        String rsvpurl = Util.html(makeURL("rsvp.jsp?code=" + invite.getInviteCode()));
        String loginurl = Util.html(makeURL(""));
        
        body = sb.toString()
                .replace("[invitecode]", invitecode)
                .replace("[campemail]", campemail)
                .replace("[rsvpurl]", rsvpurl)
                .replace("[loginurl]", loginurl)
                .replace("[realname]", realname); // name last so things don't get weird if user's name actually is "[campemail]"...
   
        subject = "Invitation to camp with D15ORIENT - " + invite.getInviteeName();
        
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
