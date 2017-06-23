package d13.notify;

import java.util.HashMap;
import java.util.Map;

import d13.ThisYear;
import d13.dao.User;

public class ApprovalEmail extends Email {

    private final String subject;
    private final String body;
    
    public static final String[][] FIELD_INFO = {
        { "campemail", "Camp email address" },
        { "duesurl", "Due pay URL" },
        { "regurl", "Reg form URL" },
        { "graceperiod", "Grace period days" },
        { "realname", "User real name" }
    };
    
    public ApprovalEmail (User user, Configuration c) {
    
        super(c);
        
        Map<String,String> fields = new HashMap<String,String>();
        fields.put("campemail", getContactEmail());
        fields.put("duesurl", makeURL("dues.jsp"));
        fields.put("regurl", makeURL("registration.jsp"));
        fields.put("graceperiod", Integer.toString(ThisYear.GRACE_PERIOD_DAYS));
        fields.put("realname", user.getRealName());

        subject = replaceFields(c.getContentTitle(RT_APPROVAL_CONTENT), fields);
        body = convertMarkdown(c.getContentBody(RT_APPROVAL_CONTENT), fields);
        
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
