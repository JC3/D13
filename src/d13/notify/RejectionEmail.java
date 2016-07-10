package d13.notify;

import d13.ThisYear;
import d13.dao.User;

public class RejectionEmail extends Email {

    private final String body;
    
    public RejectionEmail (User user, Configuration c) {
        super(c);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(user.getRealName()).append(",\n\n");
        sb.append("Thank you for your application to camp with DISORIENT on the Playa in 2016. Unfortunately, due to size restrictions of this ReOrient year, we are unable to accept every burner into camp and are regrettably unable to offer you a place with us.\n\n");
        sb.append("We hope that you have a great burn regardless of not camping with Disorient this year, and hope that you will drop in and say hello on the playa.\n\n");
        sb.append("Wishing you all the best.\n\n");
        sb.append("D16 Camp Team.\n\n");

        sb.append(getContactEmail() + "\n");
        body = sb.toString();
        
    }

    @Override protected String getSubject () {
        return "Your Disorient " + ThisYear.CAMP_YEAR + " Registration Application";
    }

    @Override protected String getBody () {
        return body;
    }

    public static final void sendNow (User user, Configuration c) throws Exception {
        RejectionEmail email = new RejectionEmail(user, c);
        email.send(user);
    }

}
