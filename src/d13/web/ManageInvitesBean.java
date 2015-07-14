package d13.web;

public class ManageInvitesBean {
    
    private String action;
    private String emails;
    private String expires;
    private int invite;
    
    public String getAction() {
        return action;
    }
    
    public String getEmails() {
        return emails;
    }
    
    public String getExpires () {
        return expires;
    }
    
    public int getInvite () {
        return invite;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public void setEmails(String emails) {
        this.emails = emails;
    }
    
    public void setExpires (String expires) {
        this.expires = expires;
    }
    
    public void setInvite (int invite) {
        this.invite = invite;
    }
    
}
