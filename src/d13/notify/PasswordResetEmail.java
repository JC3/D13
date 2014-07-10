package d13.notify;

import d13.ThisYear;
import d13.dao.User;
import d13.util.Util;

public class PasswordResetEmail extends Email {

    private final String body;
    
    public PasswordResetEmail (User user, Configuration c) {

        super(c);
        
        String realname = Util.html(user.getRealName());
        String useremail = Util.html(user.getEmail());
        String baseurl = Util.html(makeURL(""));
        String campyear = Integer.toString(ThisYear.CAMP_YEAR);
        String pwurl = Util.html(makeURL("pwreset.jsp?key=" + user.getPasswordResetKey()));
        String campemail = Util.html(getContactEmail());
        String expiretime = Util.html(c.getPwExpire());
        
        StringBuilder sb = new StringBuilder();

        sb.append("<p>Dear "+realname+",</p>");
        sb.append("<p>Somebody recently requested a password reset for this email address ("+useremail+") at the " +
                  "<a href=\""+baseurl+"\">Disorient "+campyear+" Camp Registration site</a>.</p>");
        sb.append("<p>To reset your password, please <a href=\""+pwurl+"\">click here</a> or visit the following page:</p>");
        sb.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;<a href=\""+pwurl+"\">"+pwurl+"</a></p>");
        sb.append("<p>If you do not wish to reset your password, simply ignore this email. The above link will expire " +
                  "in approximately "+expiretime+" minutes.</p>");
        sb.append("<p>If you did not request this reset, or if you have any other questions, don't hesitate to " + 
                  "contact us at <a href=\"mailto:"+campemail+"?subject=Disorient Password Reset Question\">"+campemail+"</a>.</p>");
        sb.append("<p>Sincerely,<br>Disorient</p>");
        
        body = sb.toString();
        
        System.out.println("PWRESET_EMAIL: " + pwurl);

    }

    @Override protected String getSubject () {
        return "Disorient Password Reset Request";
    }

    @Override protected String getBody () {
        return body;
    }

    @Override protected boolean isHtml () {
        return true;
    }
    
    public static final void sendNow (User user, Configuration c) throws Exception {
        PasswordResetEmail email = new PasswordResetEmail(user, c);
        email.send(user);
    }

}
