package d13.notify;

import java.util.List;

import d13.dao.User;

public class ReviewNotificationEmail extends Email {

    
    private final String body;
    
    
    public ReviewNotificationEmail (User registrant) {
        
        String detailsUrl = makeURL("details.jsp?u=" + registrant.getUserId());
        
        StringBuilder sb = new StringBuilder();
        sb.append("A new user has completed their registration form. The form must be reviewed before they can be approved/rejected.\n\n");
        sb.append("Name: ").append(registrant.getRealName()).append("\n");
        sb.append("Details: ").append(detailsUrl).append("\n");
        body = sb.toString();

    }
    
    
    @Override protected String getSubject () {
        
        return "REGISTRATION: A new user has registered.";
        
    }
    
    
    @Override protected String getBody () {
        
        return body;
        
    }
    
    
    public static final void sendNow (User registrant, List<User> recipients) throws Exception {
        
        ReviewNotificationEmail email = new ReviewNotificationEmail(registrant);
        email.send(recipients);
        
    }

}
