package d13.web;

public class PasswordResetBean {

    private Long user;
    private String key;
    private String password;
    private String password2;
    
    
    public Long getUser() {
        return user;
    }
    public String getKey() {
        return key;
    }
    public String getPassword() {
        return password;
    }
    public String getPassword2() {
        return password2;
    }
    public void setUser(Long user) {
        this.user = user;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPassword2(String password2) {
        this.password2 = password2;
    }
    
    
    
}
