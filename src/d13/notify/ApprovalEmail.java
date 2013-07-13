package d13.notify;

import d13.dao.User;

public class ApprovalEmail extends Email {

    public ApprovalEmail (User user) {
    }

    @Override protected String getSubject () {
        return "approved";
    }

    @Override protected String getBody () {
        return "approved";
    }
    
    public static final void sendNow (User user) {
        ApprovalEmail email = new ApprovalEmail(user);
        email.send(user);
    }

}
