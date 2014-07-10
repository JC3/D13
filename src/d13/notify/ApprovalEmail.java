package d13.notify;

import d13.dao.User;
import d13.util.Util;

public class ApprovalEmail extends Email {

    private final String body;
    
    public ApprovalEmail (User user, Configuration c) {
        super(c);
        StringBuilder sb = new StringBuilder();
        sb.append("<p>Dear ").append(Util.html(user.getRealName())).append(",</p>");
        sb.append("<p>It is with great pleasur");
        sb.append("e that we would like to confirm that you have been accepted");
        sb.append("  to camp with Disorient this year. We are all excited and r");
        sb.append("aring to go!</p>  <p>In order to make D14 is a successful ");
        sb.append("camp this year we need YOUR help.  We are in need of campers");
        sb.append(" to stay for Disengage!  Every full day helps, so please try");
        sb.append(" and stay until Monday or Tuesday evening and lend a helping");
        sb.append(" hand.</p><p>Please fill out the following form to assist ");
        sb.append("us  in completing the urban plan, place everyone properly an");
        sb.append("d let us know what days you can help with Disengage. This wi");
        sb.append("ll help us in placing you within our camp, so the sooner you");
        sb.append(" get your form in the better for all of us :)</p>  <p><a h");
        sb.append("ref=\"http://camp.disorient.info/survey.jsp\">http://camp.diso");
        sb.append("rient.info/survey.jsp</a></p>  <p>It is also imperative th");
        sb.append("at you keep your information up to date at:</p>  <p><a hre");
        sb.append("f=\"http://camp.disorient.info\">http://camp.disorient.info</a");
        sb.append("></p>  <p>Once again we would also like to remind you that");
        sb.append(" camp dues should be paid as soon as  possible. Details of ");
        sb.append("dues and how to pay can be found at:</p>  <p><a href=\"http");
        sb.append("://camp.disorient.info/dues.jsp\">http://camp.disorient.info/");
        sb.append("dues.jsp</a></p>  <p><b>REMEMBER CAMP DUES GO UP EVERY WEE");
        sb.append("K! PAY THEM SOON!</b></p>  <p><b>Some other important info");
        sb.append("rmation you should know:</b></p>  <ul>    <li>You will be");
        sb.append(" receiving updates about D13 from camp@disorient.info. Pleas");
        sb.append("e make    sure you read them!</li>    <li>We try our best");
        sb.append(" to provide all tent campers with shade to live under, howev");
        sb.append("er on the playa, the more shade, the better. Campers should ");
        sb.append("try and bring extra fabric, tarps, etc. to extend shade and ");
        sb.append("build walls as necessary.  If you are planning to purchase t");
        sb.append("arps for your personal use on playa, please consider getting");
        sb.append(" 10'x10' or 10'x20' orange tarps (<a href=    \"http://www.t");
        sb.append("ents-canopy.com/orange-tarp-10x10.html\">http://www.tents-can");
        sb.append("opy.com/orange-tarp-10x10.html</a>) as those will work well ");
        sb.append("with the current camp shade structure..</li>    <li>You wi");
        sb.append("ll be contacted by your cell leads regarding your on playa ");
        sb.append("   responsibilities.</li>    <li>You will be contacted by ");
        sb.append("the registration team regarding your node    assignments.</");
        sb.append("li>    <li>Jillian LoveJoy, our tireless and fearless volu");
        sb.append("nteer coordinator, will be in    touch with each of you to ");
        sb.append("discuss your volunteer assignments and schedules. She can  ");
        sb.append("  be reached at: <a href=    \"mailto:langejillian@gmail.com");
        sb.append("?subject=Disorient%20Volunteer%20Assignments\">langejillian@g");
        sb.append("mail.com</a></li>  </ul>  <p><b>One last thing: Please se");
        sb.append("nd Jillian LoveJoy (<a href=  \"mailto:langejillian@gmail.co");
        sb.append("m?subject=Disorient%20Headshot\">langejillian@gmail.com</a>)");
        sb.append("  a headshot so we all know who to look for on playa.</b></p");
        sb.append(">  <p>If you have any questions please feel free to email ");
        sb.append("us at <a href=  \"mailto:camp@disorient.info?subject=Disorie");
        sb.append("nt%20Registration\">camp@disorient.info</a>.</p>  <p>Can't ");
        sb.append("wait to camp with you all on the playa!</p>  <p>D13 Team<b");
        sb.append("r>  <a href=\"camp@disorient.info?subject=Disorient%20Regist");
        sb.append("ration\">camp@disorient.info</a></p>");
        /*sb.append("<p>Dear " + Util.html(user.getRealName()) + ",</p>");
        sb.append("<p>It is with great pleasure that we would like to confirm that you have been accepted to camp with Disorient this year. We are all excited and raring to go!</p>");
        sb.append("<p>In order that D13 is a successful camp this year we need YOUR help. For us to complete the urban plan and place everyone properly, please complete the following form. This will help us in placing you within our camp, so the sooner you get your form in the better for all of us.</p>");
        sb.append("<p><a href=\"http://camp.disorient.info/survey.jsp\">http://camp.disorient.info/survey.jsp</a></p>");
        sb.append("<p>It is also imperative that you keep your information up to date at:</p>");
        sb.append("<p><a href=\"http://camp.disorient.info\">http://camp.disorient.info</a></p>");
        sb.append("<p>Once again we would also like to remind you that camp dues should be paid as soon as possible. Details of dues and how to pay can be found at:</p>");
        sb.append("<p><a href=\"http://camp.disorient.info/dues.jsp\">http://camp.disorient.info/dues.jsp</a></p>");
        sb.append("<p><b>REMEMBER CAMP DUES GO UP EVERY WEEK! PAY THEM SOON!</b></p>");
        sb.append("<p><b>Some other important information you should know:</b></p>");
        sb.append("<p><ul>");
        sb.append("<li>You will be receiving updates about D13 from camp@disorient.info. Please make sure you read them!");
        sb.append("<li>Just a reminder that while Disorient tries to provide all tent campers with shade, we require that each camper purchase and bring with them one 10 by 10 orange tarp (<a href=\"http://www.tents-canopy.com/orange-tarp-10x10.html\">http://www.tents-canopy.com/orange-tarp-10x10.html</a>) to expand shade as necessary.");
        sb.append("<li>You will be contacted by your cell leads regarding your on playa responsibilities.");
        sb.append("<li>You will be contacted by the registration team regarding your node assignments.");
        sb.append("<li>Jillian LoveJoy, our tireless and fearless volunteer coordinator, will be in touch with each of you to discuss your volunteer assignments and schedules. She can be reached at: <a href=\"mailto:langejillian@gmail.com?subject=Disorient Volunteer Assignments\">langejillian@gmail.com</a>");
        sb.append("</ul></p>");
        sb.append("<p><b>One last thing: Please send Jillian LoveJoy (<a href=\"mailto:langejillian@gmail.com?subject=Disorient Headshot\">langejillian@gmail.com</a>) a headshot so we all know who to look for on playa.</b></p>");
        sb.append("<p>If you have any questions please feel free to email us at <a href=\"mailto:camp@disorient.info?subject=Disorient Registration\">camp@disorient.info</a>.</p>");
        sb.append("<p>Can't wait to camp with you all on the playa!</p>");
        sb.append("<p>D13 Team<br>");
        sb.append("<a href=\"camp@disorient.info?subject=Disorient Registration\">camp@disorient.info</a></p>");*/
        body = sb.toString();
    }

    @Override protected String getSubject () {
        return "Your Disorient Registration Application";
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
