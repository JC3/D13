package d13.dao;

import org.joda.time.DateTime;

import d13.util.Util;
import d13.web.DataView;

public class ApprovalSurvey {

    private long aformId;
    private User user;
    private DateTime completionTime;
    
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
    
    ApprovalSurvey () {
    }
    
    ApprovalSurvey (User user) {
        this.user = user;
    }
    
    public long getAformId () {
        return aformId;
    }
    
    public User getUser () {
        return user;
    }
    
    public DateTime getCompletionTime() {
        return completionTime;
    }

    @DataView(i=10, n="Sharing Space With", longtext=true)
    public String getSharingWith() {
        return sharingWith;
    }

    @DataView(i=20, n="Living In")
    public String getLivingIn() {
        return livingIn;
    }
    
    @DataView(i=25, n="Living In (Other)")
    public String getLivingInOther() {
        return livingInOther;
    }

    @DataView(i=30, n="Living Space Size", longtext=true)
    public String getLivingSpaceSize() {
        return livingSpaceSize;
    }

    @DataView(i=40, n="Meal Preference", longtext=true)
    public String getMealPreference() {
        return mealPreference;
    }
    
    @DataView(i=50, n="Placement Requests", longtext=true)
    public String getPlacementRequest() {
        return placementRequest;
    }

    @DataView(i=60, n="Disengage 9/1")
    public boolean isDisengage9_1() {
        return disengage9_1;
    }

    @DataView(i=70, n="Disengage 9/2")
    public boolean isDisengage9_2() {
        return disengage9_2;
    }

    @DataView(i=80, n="Disengage 9/3")
    public boolean isDisengage9_3() {
        return disengage9_3;
    }

    @DataView(i=90, n="Disengage 9/4")
    public boolean isDisengage9_4() {
        return disengage9_4;
    }

    @DataView(i=100, n="No Disengage")
    public boolean isDisengageNone() {
        return disengageNone;
    }

    public boolean isCompleted () {
        return completionTime != null;
    }
    
    public void setSharingWith(String sharingWith) {
        this.sharingWith = sharingWith;
    }

    public void setLivingIn(String livingIn) {
        this.livingIn = Util.require(livingIn, "Living space type");
    }
    
    public void setLivingInOther (String livingInOther) {
        this.livingInOther = livingInOther;
    }

    public void setLivingSpaceSize(String livingSpaceSize) {
        this.livingSpaceSize = Util.require(livingSpaceSize, "Living space size");
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

    public void validateMisc () throws IllegalArgumentException {
        if ("other".equalsIgnoreCase(livingIn))
            Util.require(livingInOther, "Other living space type");
    }
    
    public void setCompletionTimeNow () {
        completionTime = DateTime.now();
    }

}
