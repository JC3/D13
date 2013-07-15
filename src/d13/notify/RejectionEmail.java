package d13.notify;

import d13.dao.User;

public class RejectionEmail extends Email {

    public RejectionEmail (User user, Configuration c) {
        super(c);
    }

    @Override protected String getSubject () {
        return "rejected";
    }

    @Override protected String getBody () {
        return "rejected";
    }

    public static final void sendNow (User user, Configuration c) throws Exception {
        throw new Exception("Not implemented.");
        //RejectionEmail email = new RejectionEmail(user);
        //email.send(user);
    }

}
