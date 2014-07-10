package d13.web;

public class LoginBean {

    private String email;
    private boolean existing;
    private String password;
    private boolean forgot;

    public String getEmail() {
        return email;
    }
    
    public boolean isExisting() {
        return existing;
    }
    
    public String getPassword() {
        return password;
    }
    
    public boolean isForgot () {
        return forgot;
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
    
    public void setForgot (boolean forgot) {
        this.forgot = forgot;
    }
    
}
