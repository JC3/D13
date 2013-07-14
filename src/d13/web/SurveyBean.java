package d13.web;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;

public class SurveyBean {

    private String sharingWith;
    private String livingIn;
    private String livingInOther;
    private String livingSpaceSize;
    private String mealPreference;
    private String placementRequest;
    private boolean disengage9_1;
    private boolean disengage9_2;
    private boolean disengage9_3;
    private boolean disengage9_4;
    private boolean disengageNone;
   
    public SurveyBean () {
    }
    
    public SurveyBean (User user) {
        try {
            BeanUtils.copyProperties(this, user.getApproval());
        } catch (Exception x) {
            x.printStackTrace();
        }
        //System.out.println(toString()); // TODO: remove this
    }
    
    public String getSharingWith() {
        return sharingWith;
    }
    public String getLivingIn() {
        return livingIn;
    }
    public String getLivingInOther() {
        return livingInOther;
    }
    public String getLivingSpaceSize() {
        return livingSpaceSize;
    }
    public String getMealPreference() {
        return mealPreference;
    }
    public String getPlacementRequest() {
        return placementRequest;
    }
    public boolean isDisengage9_1() {
        return disengage9_1;
    }
    public boolean isDisengage9_2() {
        return disengage9_2;
    }
    public boolean isDisengage9_3() {
        return disengage9_3;
    }
    public boolean isDisengage9_4() {
        return disengage9_4;
    }
    public boolean isDisengageNone() {
        return disengageNone;
    }
    public void setSharingWith(String sharingWith) {
        this.sharingWith = sharingWith;
    }
    public void setLivingIn(String livingIn) {
        this.livingIn = livingIn;
    }
    public void setLivingInOther(String livingInOther) {
        this.livingInOther = livingInOther;
    }
    public void setLivingSpaceSize(String livingSpaceSize) {
        this.livingSpaceSize = livingSpaceSize;
    }
    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference;
    }
    public void setPlacementRequest(String placementRequest) {
        this.placementRequest = placementRequest;
    }
    public void setDisengage9_1(boolean disengage9_1) {
        this.disengage9_1 = disengage9_1;
    }
    public void setDisengage9_2(boolean disengage9_2) {
        this.disengage9_2 = disengage9_2;
    }
    public void setDisengage9_3(boolean disengage9_3) {
        this.disengage9_3 = disengage9_3;
    }
    public void setDisengage9_4(boolean disengage9_4) {
        this.disengage9_4 = disengage9_4;
    }
    public void setDisengageNone(boolean disengageNone) {
        this.disengageNone = disengageNone;
    }
    
}
