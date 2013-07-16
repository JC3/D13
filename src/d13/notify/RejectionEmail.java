package d13.notify;

import d13.dao.User;

public class RejectionEmail extends Email {

    private final String body;
    
    public RejectionEmail (User user, Configuration c) {
        super(c);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(user.getRealName()).append(",\n\n");
        sb.append("We would like to thank you for your application to camp with DISORIENT on the Playa this year. Unfortunately due to size restrictions we are unable to accept every burner who wants to camp with us this year, and are unable to offer you a place with us.\n\n");
        sb.append("We hope that you have a good burn regardless of not camping with us this year and hope that you will drop in and say hello on the playa.\n\n");
        sb.append("Wishing you all the best.\n\n");
        sb.append("D13 Camp Team.\n");
        sb.append("camp@disorient.info\n");
        body = sb.toString();
        
    }

    @Override protected String getSubject () {
        return "Your Disorient Registration Application";
    }

    @Override protected String getBody () {
        return body;
    }

    public static final void sendNow (User user, Configuration c) throws Exception {
        RejectionEmail email = new RejectionEmail(user, c);
        email.send(user);
    }

}
