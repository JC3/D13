package d13.apps.offline;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import d13.notify.Email;

public class DisengageRebateEmail extends Email {

    
    private final String body;
    
    
    private static Configuration asSingle (Configuration c) {
        Configuration c2 = c.clone();
        c2.single = true;
        return c2;
    }
    
    
    public DisengageRebateEmail (DisengageRebateInfo ri, Configuration c) {

        super(asSingle(c));

        int total = 0;
        if (ri.sunday) total += 50;
        if (ri.monday) total += 50;
        if (ri.tuesday) total += 75;
        if (ri.wednesday) total += 75;
                
        StringBuilder s = new StringBuilder();
        s.append("<div dir=\"ltr\">");
        s.append("<div style=\"font-family:arial,sans-serif;font-size:13px\">");
        s.append("<div>Thank you for being a ");
        s.append("<b>");
        s.append("<font color=\"#ff00ff\">D14 Disengage rock star");
        s.append("</font>");
        s.append("</b>!!");
        s.append("</div>");
        s.append("<span class=\"im\">");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>'Tis the time for settling camp dues refunds in return for you being AWESOME!");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("</span>");
        s.append("<div>");
        s.append("<b>Here is your currently recorded Disengage work and rebate information:");
        s.append("</b>");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("</div>");
        s.append("<blockquote style=\"font-family:arial,sans-serif;font-size:13px;margin:0px 0px 0px 40px;border:none;padding:0px\">");
        if (ri.sunday)
            s.append("<div>Sunday 8/31 (+$50)</div>");
        if (ri.monday)
            s.append("<div>Monday 9/1 (+$50)</div>");
        if (ri.tuesday)
            s.append("<div>Tuesday 9/2 (+$75)</div>");
        if (ri.wednesday)
            s.append("<div>Wednesday 9/3 (+$75)</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>");
        s.append("<b>Rebate Total: $" + total);
        s.append("</b>");
        s.append("</div>");
        s.append("</blockquote>");
        s.append("<div style=\"font-family:arial,sans-serif;font-size:13px\">");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>If the above information is accurate, ");
        s.append("<b>please fill out ");
        s.append("<a href=\"http://goo.gl/forms/fZCzK5th34\" target=\"_blank\">this form");
        s.append("</a>");
        s.append("</b>, which helps expedite the refund process:");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>  ");
        s.append("<a href=\"http://goo.gl/forms/fZCzK5th34\" target=\"_blank\">D14 Disengage Rebate Form");
        s.append("</a>");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>This form must be filled out by the ");
        s.append("<b>end of the day on November 10, 2014");
        s.append("</b>! If you believe the above information is ");
        s.append("<b>not ");
        s.append("</b>accurate, please");
        s.append("<font color=\"#ff00ff\"> ");
        s.append("<b>reply to this email");
        s.append("</b>");
        s.append("</font> to discuss further before filling out the form to sort everything out!");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>Please help ");
        s.append("<b>make your rebate accurately reflect your Disengage contributions");
        s.append("</b>, so that these rebates can continue to be offered in future years: if you did not contribute as many full days of work as listed above, please consider taking a lesser amount. If you worked more full days than recorded above, please reply to this email so the records can be updated and your contribution recognized!");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>If confirming your rebate, you have several options!");
        s.append("</div>");
        s.append("<div>");
        s.append("<ol>");
        s.append("<li style=\"margin-left:15px\">");
        s.append("<b>");
        s.append("<font color=\"#ff9900\">");
        s.append("<u>Donate part or all of your rebate");
        s.append("</u>");
        s.append("</font>");
        s.append("</b> to Disorient to help achieve our camp's lofty goals! Our camp relies on a complex web of art, talent, construction, materials, logistics, communications, supplies, and services to make the Disorient magic happen both on and off playa year round. Your generosity will not go unnoticed.");
        s.append("<br>");
        s.append("<br>");
        s.append("</li>");
        s.append("<li style=\"margin-left:15px\">");
        s.append("<font color=\"#ff9900\">");
        s.append("<b>");
        s.append("<u>Hold your disengage rebate as a deposit");
        s.append("</u>");
        s.append("</b>");
        s.append("</font> on Disorient camp dues for Burning Man 2015. As part of the 2015 Disorient registration process you will be given an option to apply your disengage rebate to dues.");
        s.append("<br>");
        s.append("<br>");
        s.append("</li>");
        s.append("<li style=\"margin-left:15px\">");
        s.append("<span class=\"im\">");
        s.append("<font color=\"#ff9900\">");
        s.append("<b>");
        s.append("<u>Receive your refund");
        s.append("</u>");
        s.append("</b>");
        s.append("</font>:");
        s.append("<br>");
        s.append("<font color=\"#000000\">(a) via PayPal. This is the fastest way!");
        s.append("<br>");
        s.append("</font>");
        s.append("</span>");
        s.append("<span class=\"im\">");
        s.append("<font color=\"#000000\">(b) by check mailed via snail mail.");
        s.append("</font>");
        s.append("</span>");
        s.append("</li>");
        s.append("</ol>");
        s.append("</div>");
        s.append("<div>");
        s.append("<font color=\"#000000\">If you hav");
        s.append("</font>e questions, concerns, or anything else you'd like to discuss regarding your refund, please reply to this email and we'll get everything sorted!");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>Thank you!!");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>");
        s.append("<b>");
        s.append("<font color=\"#ff9900\">DISORIENT ");
        s.append("</font>");
        s.append("<font color=\"#ff00ff\">&lt;3");
        s.append("</font> ");
        s.append("<font color=\"#ff9900\">YOU");
        s.append("</font>");
        s.append("<font color=\"#ff00ff\">!");
        s.append("</font>");
        s.append("</b>");
        s.append("</div>");
        s.append("<div>");
        s.append("<br>");
        s.append("</div>");
        s.append("<div>Much love from Jason, Laura and the entire D14 team!");
        s.append("</div>");
        s.append("</div>");
        s.append("</div>");

        body = s.toString();
        
    }

    
    @Override protected String getSubject () {
        
        return "D14 Disengage Rebates (Deadline November 10)";
        
    }

    
    @Override protected String getBody () {

        return body;
        
    }
    
    
    @Override protected boolean isHtml () {
        
        return true;
        
    }
    
    
    public static final void sendNow (DisengageRebateInfo ri, Configuration c) throws Exception {
        DisengageRebateEmail email = new DisengageRebateEmail(ri, c);
        List<InternetAddress> to = new ArrayList<InternetAddress>();
        to.add(new InternetAddress(ri.email, ri.name));
        to.get(0).validate();
        email.send(to);
    }

    
}
