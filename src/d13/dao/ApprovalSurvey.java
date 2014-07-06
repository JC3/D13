package d13.dao;

import org.joda.time.DateTime;

import d13.web.DataView;

public class ApprovalSurvey {

    private long aformId;
    private User user;
    private DateTime completionTime;
    
    private String mealPreference;
    private String placementRequest;
    private boolean disengage9_1;
    private boolean disengage9_2;
    private boolean disengage9_3;
    private boolean disengage9_4;
    private boolean disengageNone;
    private boolean tixForSale;
    private int numForSale;
    private boolean tixWanted;
    private int numWanted;

    ApprovalSurvey () {
    }
    
    ApprovalSurvey (User user) {
        this.user = user;
        if (user.isRegistrationComplete()) {
            this.tixForSale = user.getRegistration().isTixForSale();
            this.numForSale = user.getRegistration().getNumForSale();
            this.tixWanted = user.getRegistration().isTixWanted();
            this.numWanted = user.getRegistration().getNumWanted();
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

    public boolean isTixForSale() {
        return tixForSale;
    }

    @DataView(i=110, n="Tickets For Sale")
    public int getNumForSale() {
        return tixForSale ? numForSale : 0;
    }

    public boolean isTixWanted() {
        return tixWanted;
    }

    @DataView(i=120, n="Tickets Needed")
    public int getNumWanted() {
        return tixWanted ? numWanted : 0;
    }

    public boolean isCompleted () {
        return completionTime != null;
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

    public void setTixForSale (boolean tixForSale) {
        this.tixForSale = tixForSale;
    }

    public void setNumForSale (int numForSale) {
        if (numForSale < 0) throw new IllegalArgumentException("Number of tickets for sale can't be negative.");
        this.numForSale = numForSale;
    }

    public void setTixWanted (boolean tixWanted) {
        this.tixWanted = tixWanted;
    }

    public void setNumWanted (int numWanted) {
        if (numWanted < 0) throw new IllegalArgumentException("Number of tickets wanted can't be negative.");
        this.numWanted = numWanted;
    }

    public void validateMisc () throws IllegalArgumentException {
        if (tixForSale && numForSale <= 0)
            throw new IllegalArgumentException("Number of tickets for sale must be specified.");
        if (tixWanted && numWanted <= 0)
            throw new IllegalArgumentException("Number of tickets wanted must be specified.");
    }
    
    public void setCompletionTimeNow () {
        completionTime = DateTime.now();
    }

}
