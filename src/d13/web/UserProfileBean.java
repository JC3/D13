package d13.web;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;

public class UserProfileBean {

    private String email;
    private String password;
    private String realName;
    private String playaName;
    private String gender;
    private String phone;
    private String location;
    private String locationOther;
    private String emergencyContact;

    public UserProfileBean () {
    }
    
    public UserProfileBean (User user) {
        try {
            BeanUtils.copyProperties(this, user);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
    
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getRealName() {
        return realName;
    }
    public String getPlayaName() {
        return playaName;
    }
    public String getGender() {
        return gender;
    }
    public String getPhone() {
        return phone;
    }
    public String getLocation() {
        return location;
    }
    public String getLocationOther() {
        return locationOther;
    }
    public String getEmergencyContact() {
        return emergencyContact;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public void setPlayaName(String playaName) {
        this.playaName = playaName;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setLocationOther(String locationOther) {
        this.locationOther = locationOther;
    }
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserProfileBean [email=").append(email)
                .append(", password=").append(password).append(", realName=")
                .append(realName).append(", playaName=").append(playaName)
                .append(", gender=").append(gender).append(", phone=")
                .append(phone).append(", location=").append(location)
                .append(", locationOther=").append(locationOther)
                .append(", emergencyContact=").append(emergencyContact)
                .append("]");
        return builder.toString();
    }
    
}
