package d13.notify;

import java.util.List;

import d13.dao.User;

public class AcceptedNotificationEmail extends Email {


private final String body;
    
    
    public AcceptedNotificationEmail (User registrant) {
        
        String detailsUrl = makeURL("details.jsp?u=" + registrant.getUserId());
        
        StringBuilder sb = new StringBuilder();
        sb.append("A registration application is ready to be reviewed.\n\n");
        sb.append("Name: ").append(registrant.getRealName()).append("\n");
        sb.append("Details: ").append(detailsUrl).append("\n");
        body = sb.toString();

    }
    
    
    @Override protected String getSubject () {
        
        return "REGISTRATION: A user is ready to be approved/rejected.";
        
    }
    
    
    @Override protected String getBody () {
        
        return body;
        
    }

    
    public static final void sendNow (User registrant, List<User> recipients) {
        
        AcceptedNotificationEmail email = new AcceptedNotificationEmail(registrant);
        email.send(recipients);
        
    }
    

}
