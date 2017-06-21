package d13.dao;

import org.joda.time.DateTime;

import d13.changetrack.Track;
import d13.changetrack.Trackable;
import d13.web.DataView;

public class ApprovalSurvey implements Trackable {

    private long aformId;
    private User user;
    private DateTime completionTime;
    
    // private String mealPreference;  // removed 2016
    private String placementRequest;
    private boolean disengageSun;
    private boolean disengageMon;
    private boolean disengageTue;
    private boolean disengageWed;
    private boolean disengageNone;

    ApprovalSurvey () {
    }
    
    ApprovalSurvey (User user) {
        this.user = user;
        if (user.isRegistrationComplete()) {
            /*
            this.tixForSale = user.getRegistration().isTixForSale();
            this.numForSale = user.getRegistration().getNumForSale();
            this.tixWanted = user.getRegistration().isTixWanted();
            this.numWanted = user.getRegistration().getNumWanted();
            */
        }
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

    /* // removed 2016
    @DataView(i=40, n="Meal Preference", longtext=true)
    public String getMealPreference() {
        return mealPreference;
    }
    */
    
    @Track
    @DataView(i=50, n="Placement Requests", longtext=true)
    public String getPlacementRequest() {
        return placementRequest;
    }

    @Track
    @DataView(i=60, n="Disengage Sun.")
    public boolean isDisengageSun() {
        return disengageSun;
    }

    @Track
    @DataView(i=70, n="Disengage Mon.")
    public boolean isDisengageMon() {
        return disengageMon;
    }

    @Track
    @DataView(i=80, n="Disengage Tue.")
    public boolean isDisengageTue() {
        return disengageTue;
    }

    @Track
    @DataView(i=90, n="Disengage Wed.")
    public boolean isDisengageWed() {
        return disengageWed;
    }

    @Track
    @DataView(i=100, n="No Disengage")
    public boolean isDisengageNone() {
        return disengageNone;
    }

    public boolean isCompleted () {
        return completionTime != null;
    }
    
    /*  // removed 2016
    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference;
    }
    */

    public void setPlacementRequest(String placementRequest) {
        this.placementRequest = placementRequest;
    }

    public void setDisengageSun(boolean disengage9_1) {
        this.disengageSun = disengage9_1;
    }

    public void setDisengageMon(boolean disengage9_2) {
        this.disengageMon = disengage9_2;
    }

    public void setDisengageTue(boolean disengage9_3) {
        this.disengageTue = disengage9_3;
    }

    public void setDisengageWed(boolean disengage9_4) {
        this.disengageWed = disengage9_4;
    }

    public void setDisengageNone(boolean disengageNone) {
        this.disengageNone = disengageNone;
    }

    public void validateMisc () throws IllegalArgumentException {
        //if (tixForSale && numForSale <= 0)
        //    throw new IllegalArgumentException("Number of tickets for sale must be specified.");
        //if (tixWanted && numWanted <= 0)
        //    throw new IllegalArgumentException("Number of tickets wanted must be specified.");
    }
    
    public void setCompletionTimeNow () {
        completionTime = DateTime.now();
    }

}
