package d13.notify;

import java.util.HashMap;
import java.util.Map;

import d13.dao.User;

public class RejectionEmail extends Email {

    private final String subject;
    private final String body;
    
    public RejectionEmail (User user, Configuration c) {

        super(c);
        
        Map<String,String> fields = new HashMap<String,String>();
        fields.put("campemail", getContactEmail());
        fields.put("realname", user.getRealName());

        subject = replaceFields(c.getContentTitle(RT_REJECTION_CONTENT), fields);
        body = convertMarkdown(c.getContentBody(RT_REJECTION_CONTENT), fields);
        
    }

    @Override protected String getSubject () {
        return subject;
    }

    @Override protected String getBody () {
        return body;
    }

    public static final void sendNow (User user, Configuration c) throws Exception {
        RejectionEmail email = new RejectionEmail(user, c);
        email.send(user);
    }

}
