package d13.notify;

import d13.ThisYear;
import d13.dao.User;
import d13.util.Util;

public class ApprovalEmail extends Email {

    private final String subject;
    private final String body;
    
    public ApprovalEmail (User user, Configuration c) {
    
        super(c);
        
        StringBuilder sb = new StringBuilder();
        
        String realname = Util.html(user.getRealName());
        //String surveyurl = Util.html(makeURL("survey.jsp"));
        String regurl = Util.html(makeURL("registration.jsp"));
        String duesurl = Util.html(makeURL("dues.jsp"));
        String campemail = Util.html(getContactEmail());

        sb.append("<p>Dear [realname],</p>");
        sb.append("<p>It is with great pleasure that we would like to confirm that you have been accepted to camp with Disorient this year. We are all excited and raring to go!</p>");
        sb.append("<p>Thank you for being a part of D16ORIENT. In order to create a successful camp this year we need YOUR help. Full participation is needed to bring D16ORIENT to and from the playa, especially in the areas of Alpha and Disengage. Please consider staying until Monday or Tuesday at the end of the week as every full day helps. Working together to build our camp is the key to keeping Disorient tighter and brighter than ever before.</p>");
        sb.append("<p>To secure your place in camp, please pay your camp dues promptly so that we can all get to the playa as smoothly as possible. You have [graceperiod] days from the time of this email to pay the tier that was in effect when you filled out your registration form, but after that, camp dues go up the longer you wait. Details of dues and how to pay can be found at:</p>");
        sb.append("<p><a href=\"[duesurl]\">[duesurl]</a></p>");
        sb.append("<p>You may update your registration form (please keep your info up to date!) and volunteer cells at any time at:</p>");
        sb.append("<p><a href=\"[regurl]\">[regurl]</a></p>");
        sb.append("<p><b>Some other important information you should know:</b></p>");
        sb.append("<ul>");
        sb.append("<li>You will be receiving updates about D16ORIENT from [campemail]. Please make sure you read them!");
        sb.append("<li>You may be contacted by your cell leads regarding your on-playa responsibilities.");
        sb.append("<li>Our tireless and fearless volunteer coordinators will be in touch with each of you to discuss your volunteer assignments and schedules.");
        sb.append("</ul>");
        sb.append("<p>If you have any questions please feel free to reply to this email, or contact [campemail] at any time.</p>");
        sb.append("<p>Can't wait to camp with you all on the playa!</p>");
        sb.append("<p><a href=\"mailto:[campemail]\">D16ORIENT</a></p>");
        
        body = sb.toString()
                .replace("[campemail]", campemail)
                .replace("[duesurl]", duesurl)
                .replace("[regurl]", regurl)
                .replace("[graceperiod]", Integer.toString(ThisYear.GRACE_PERIOD_DAYS))
                .replace("[realname]", realname);
    
        subject = "Your Disorient " + ThisYear.CAMP_YEAR + " Registration Application - " + user.getRealName();

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
    
    public static final void sendNow (User user, Configuration c) throws Exception {
        ApprovalEmail email = new ApprovalEmail(user, c);
        email.send(user);
    }

}
