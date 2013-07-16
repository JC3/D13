package d13.notify;

import java.util.List;

import d13.dao.User;

public class PendingNotificationEmail extends Email {

    
    private final String body;
    
    
    public PendingNotificationEmail (User registrant, Configuration c) {
        
        super(c);
        String detailsUrl = makeURL("details.jsp?u=" + registrant.getUserId());
        
        StringBuilder sb = new StringBuilder();
        sb.append("An admissions team member has approved/rejected a user. The decision must be confirmed before the decision becomes final.\n\n");
        sb.append("Name: ").append(registrant.getRealName()).append("\n");
        sb.append("Status: ").append(registrant.getState().toString()).append("\n");
        sb.append("Details: ").append(detailsUrl).append("\n");
        body = sb.toString();

    }
    
    
    @Override protected String getSubject () {
        
        return "REGISTRATION: An admissions decision must be finalized.";
        
    }
    
    
    @Override protected String getBody () {
        
        return body;
        
    }
    
    
    public static final void sendNow (User registrant, List<User> recipients, Configuration c) throws Exception {
        
        PendingNotificationEmail email = new PendingNotificationEmail(registrant, c);
        email.send(recipients);
        
    }

}
