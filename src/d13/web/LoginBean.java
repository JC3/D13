package d13.web;

public class LoginBean {

    private String email;
    private boolean existing;
    private String password;

    public String getEmail() {
        return email;
    }
    
    public boolean isExisting() {
        return existing;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setExisting(boolean existing) {
        this.existing = existing;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
}
