package d13.web;

import d13.dao.DueItem;
import d13.dao.User;

public class CustomDuesBean {

    private String personalCustom;
    private String personalAmount;
    private String rvCustom;
    private String rvAmount;
    private String explain;
    
    public CustomDuesBean () {
        
    }
    
    private static String customToBoolean (DueItem item) {
        if (item == null)
            return "0";
        else if (item.isCustom())
            return "1";
        else
            return "0";
    }
    
    private static String customToAmount (DueItem item) {
        if (item == null)
            return "";
        else if (item.isCustom())
            return String.format("%.2f", (float)item.getCustomAmount() / 100.0f);
        else
            return "";
    }
    
    public CustomDuesBean (User user) {
        personalCustom = customToBoolean(user.getPersonalDueItem());
        personalAmount = customToAmount(user.getPersonalDueItem());
        rvCustom = customToBoolean(user.getRvDueItem());
        rvAmount = customToAmount(user.getRvDueItem());
        explain = user.getCustomDueComments();
    }
    
    public String getPersonalCustom() {
        return personalCustom;
    }
    public String getPersonalAmount() {
        return personalAmount;
    }
    public String getRvCustom() {
        return rvCustom;
    }
    public String getRvAmount() {
        return rvAmount;
    }
    public String getExplain() {
        return explain;
    }
    public void setPersonalCustom(String personalCustom) {
        this.personalCustom = personalCustom;
    }
    public void setPersonalAmount(String personalAmount) {
        this.personalAmount = personalAmount;
    }
    public void setRvCustom(String rvCustom) {
        this.rvCustom = rvCustom;
    }
    public void setRvAmount(String rvAmount) {
        this.rvAmount = rvAmount;
    }
    public void setExplain(String explain) {
        this.explain = explain;
    }
    
    
    
}
