package d13.web;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;

public class SurveyBean {

    private String mealPreference;
    private String placementRequest;
    private boolean disengageSun;
    private boolean disengageMon;
    private boolean disengageTue;
    private boolean disengageWed;
    private boolean disengageNone;
    private boolean tixForSale;
    private String numForSale;
    private boolean tixWanted;
    private String numWanted;
   
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
    
    public String getMealPreference() {
        return mealPreference;
    }
    public String getPlacementRequest() {
        return placementRequest;
    }
    public boolean isDisengageSun() {
        return disengageSun;
    }
    public boolean isDisengageMon() {
        return disengageMon;
    }
    public boolean isDisengageTue() {
        return disengageTue;
    }
    public boolean isDisengageWed() {
        return disengageWed;
    }
    public boolean isDisengageNone() {
        return disengageNone;
    }
    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference;
    }
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
    
    public boolean isTixForSale() {
        return tixForSale;
    }

    public String getNumForSale() {
        return numForSale;
    }

    public boolean isTixWanted() {
        return tixWanted;
    }

    public String getNumWanted() {
        return numWanted;
    }

    public void setTixForSale(boolean tixForSale) {
        this.tixForSale = tixForSale;
    }

    public void setNumForSale(String numForSale) {
        this.numForSale = numForSale;
    }

    public void setTixWanted(boolean tixWanted) {
        this.tixWanted = tixWanted;
    }

    public void setNumWanted(String numWanted) {
        this.numWanted = numWanted;
    }
}
