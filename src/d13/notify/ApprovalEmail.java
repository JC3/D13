package d13.notify;

import d13.ThisYear;
import d13.dao.User;
import d13.util.Util;

public class ApprovalEmail extends Email {

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

        /* 2014:
        sb.append("<p>Dear " + realname + ",</p>");
        sb.append("<p>It is with great pleasure that we would like to confirm that you have been accepted to camp"); 
        sb.append("   with Disorient this year. We are all excited and raring to go!</p>");
        sb.append("<p>In order to make D14 is a successful camp this year we need YOUR help. We are in need of"); 
        sb.append("   campers to stay for Disengage! Every full day helps, so please try and stay until Monday or");
        sb.append("   Tuesday evening and lend a helping hand.</p>");
        sb.append("<p>Please fill out the following form to assist us in completing the urban plan, place everyone");
        sb.append("   properly and let us know what days you can help with Disengage. This will help us in placing");
        sb.append("   you within our camp, so the sooner you get your form in the better for all of us :)</p>");
        sb.append("<p><a href=\"" + surveyurl + "\">" + surveyurl + "</a></p>");
        sb.append("<p>It is also imperative that you keep your information up to date at:</p>");
        sb.append("<p><a href=\"" + regurl + "\">" + regurl + "</a></p>");
        sb.append("<p>Once again we would also like to remind you that camp dues should be paid as soon as"); 
        sb.append("   possible. Details of dues and how to pay can be found at:</p>");
        sb.append("<p><a href=\"" + duesurl + "\">" + duesurl + "</a></p>");
        sb.append("<p><b>REMEMBER CAMP DUES GO UP EVERY WEEK! PAY THEM SOON!</b></p>");
        sb.append("<p><b>Some other important information you should know:</b></p>");
        sb.append("<ul>");
        sb.append("  <li>You will be receiving updates about D14 from " + campemail + ". Please make sure you read them!");
        sb.append("  <li>We try our best to provide all tent campers with shade to live under, however on the playa,"); 
        sb.append("      the more shade, the better. Campers should try and bring extra fabric, tarps, etc. to extend");
        sb.append("      shade and build walls as necessary. If you are planning to purchase tarps for your personal ");
        sb.append("      use on playa, please consider getting 10'x10' or 10'x20' orange tarps ");
        sb.append("      (<a href=\"http://www.tents-canopy.com/orange-tarp-10x10.html\">http://www.tents-canopy.com/orange-tarp-10x10.html</a>)"); 
        sb.append("      as those will work well with the current camp shade structure.");
        sb.append("  <li>You will be contacted by your cell leads regarding your on playa responsibilities.");
        sb.append("  <li>Our tireless and fearless volunteer coordinators, will be in touch with each of you to"); 
        sb.append("      discuss your volunteer assignments and schedules. ");
        sb.append("</ul>");
        sb.append("<p>If you have any questions please feel free to reply to this email, or contact us at " + campemail + " at any time.</p>");
        sb.append("<p>Can't wait to camp with you all on the playa!</p>");
        sb.append("<p>D14 Team<br>" + campemail + "</p>");
        */
        
        body = sb.toString()
                .replace("[campemail]", campemail)
                .replace("[duesurl]", duesurl)
                .replace("[regurl]", regurl)
                .replace("[graceperiod]", Integer.toString(ThisYear.GRACE_PERIOD_DAYS))
                .replace("[realname]", realname);
    
    }

    @Override protected String getSubject () {
        return "Your Disorient " + ThisYear.CAMP_YEAR + " Registration Application";
    }

    /*
     * <p>Dear XXXXX,</p>
<p>It is with great pleasure that we would like to confirm that you have been accepted to camp with Disorient this year. We are all excited and raring to go!</p>
<p>In order that D13 is a successful camp this year we need YOUR help. For us to complete the urban plan and place everyone properly, please complete the following form. This will help us in placing you within our camp, so the sooner you get your form in the better for all of us.</p>
<p><a href="http://camp.disorient.info/survey.jsp">http://camp.disorient.info/survey.jsp</a></p>
<p>It is also imperative that you keep your information up to date at:</p>
<p><a href="http://camp.disorient.info">http://camp.disorient.info</a></p>
<p>Once again we would also like to remind you that camp dues should be paid as soon as possible. Details of dues and how to pay can be found at:</p>
<p><a href="http://camp.disorient.info/dues.jsp">http://camp.disorient.info/dues.jsp</a></p>
<p><b>REMEMBER CAMP DUES GO UP EVERY WEEK! PAY THEM SOON!</b></p>
<p><b>Some other important information you should know:</b></p>
<p><ul>
  <li>You will be receiving updates about D13 from camp@disorient.info. Please make sure you read them!
  <li>Just a reminder that while Disorient tries to provide all tent campers with shade, we require that each camper purchase and bring with them one 10 by 10 orange tarp (<a href="http://www.tents-canopy.com/orange-tarp-10x10.html">http://www.tents-canopy.com/orange-tarp-10x10.html</a>) to expand shade as necessary.
  <li>You will be contacted by your cell leads regarding your on playa responsibilities.
  <li>You will be contacted by the registration team regarding your node assignments.
  <li>Jillian LoveJoy, our tireless and fearless volunteer coordinator, will be in touch with each of you to discuss your volunteer assignments and schedules. She can be reached at: <a href="mailto:langejillian@gmail.com?subject=Disorient Volunteer Assignments">langejillian@gmail.com</a>
</ul></p>
<p><b>One last thing please could you send Jillian LoveJoy (<a href="mailto:langejillian@gmail.com?subject=Disorient Headshot">langejillian@gmail.com</a>) a headshot so we all know who to look for on playa.</b></p>
<p>If you have any questions please feel free to email us at <a href="mailto:camp@disorient.info?subject=Disorient Registration">camp@disorient.info</a>.</p>
<p>Can’t wait to camp with you all on the playa!</p>
<p>D13 Team<br>
<a href="camp@disorient.info?subject=Disorient Registration">camp@disorient.info</a></p>

     */
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
