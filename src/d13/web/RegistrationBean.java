package d13.web;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;

public class RegistrationBean {

    private boolean disorientVirgin;
    private boolean bmVirgin;
    private String sponsor;
    private String sponsorFor;
    private String arrivalDate;
    private String departureDate;
    private String departureTime;
    private Long drivingFromToId;
    private String drivingFromToIdOther;
    private Long rvTypeId;
    private String sharingWith;
    private String livingIn;
    private String livingInOther;
    private String livingSpaceSize;
    private boolean driving;
    private String rideSpaceTo;
    private String rideSpaceFrom;
    private boolean parkAtCamp;
    private String vehicleComments;
    private boolean needRideTo;
    private boolean needRideFrom;
    private boolean tixForSale;
    private String numForSale;
    private boolean tixWanted;
    private String numWanted;
    //private Long groupLeaderId;  // removed 2016
    private String personalProject;
    private String comments;
    private boolean haveVehiclePass;
    private Long tixSourceId;
    private Long shirtSizeId;
    
    public RegistrationBean () {
    }
    
    public RegistrationBean (User user) {
        try {
            BeanUtils.copyProperties(this, user.getRegistration());
        } catch (Exception x) {
            x.printStackTrace();
        }
        //System.out.println(toString()); // TODO: remove this
    }

    public boolean isDisorientVirgin() {
        return disorientVirgin;
    }

    public boolean isBmVirgin() {
        return bmVirgin;
    }

    public String getSponsor() {
        return sponsor;
    }

    public String getSponsorFor() {
        return sponsorFor;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public Long getDrivingFromToId() {
        return drivingFromToId;
    }
    
    public String getDrivingFromToIdOther () {
        return drivingFromToIdOther;
    }

    public Long getRvTypeId() {
        return rvTypeId;
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

    public boolean isDriving() {
        return driving;
    }

    public String getRideSpaceTo() {
        return rideSpaceTo;
    }

    public String getRideSpaceFrom() {
        return rideSpaceFrom;
    }

    public boolean isParkAtCamp() {
        return parkAtCamp;
    }

    public String getVehicleComments() {
        return vehicleComments;
    }

    public boolean isNeedRideTo() {
        return needRideTo;
    }

    public boolean isNeedRideFrom() {
        return needRideFrom;
    }

    /*  // removed 2016
    public Long getGroupLeaderId() {
        return groupLeaderId;
    }
    */

    public String getPersonalProject() {
        return personalProject;
    }

    public Long getShirtSizeId () {
        return shirtSizeId;
    }
    
    public String getComments() {
        return comments;
    }

    public void setDisorientVirgin(boolean disorientVirgin) {
        this.disorientVirgin = disorientVirgin;
    }

    public void setBmVirgin(boolean bmVirgin) {
        this.bmVirgin = bmVirgin;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public void setSponsorFor(String sponsorFor) {
        this.sponsorFor = sponsorFor;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public void setDrivingFromToId(Long drivingFromToId) {
        this.drivingFromToId = drivingFromToId;
    }
    
    public void setDrivingFromToIdOther (String drivingFromToIdOther) {
        this.drivingFromToIdOther = drivingFromToIdOther;
    }

    public void setRvTypeId(Long rvTypeId) {
        this.rvTypeId = rvTypeId;
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

    public void setDriving(boolean driving) {
        this.driving = driving;
    }

    public void setRideSpaceTo(String rideSpaceTo) {
        this.rideSpaceTo = rideSpaceTo;
    }

    public void setRideSpaceFrom(String rideSpaceFrom) {
        this.rideSpaceFrom = rideSpaceFrom;
    }

    public void setParkAtCamp(boolean parkAtCamp) {
        this.parkAtCamp = parkAtCamp;
    }

    public void setVehicleComments(String vehicleComments) {
        this.vehicleComments = vehicleComments;
    }

    public void setNeedRideTo(boolean needRideTo) {
        this.needRideTo = needRideTo;
    }

    public void setNeedRideFrom(boolean needRideFrom) {
        this.needRideFrom = needRideFrom;
    }

    /*  // removed 2016
    public void setGroupLeaderId(Long groupLeader) {
        this.groupLeaderId = groupLeader;
    }
    */

    public void setPersonalProject(String personalProject) {
        this.personalProject = personalProject;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
    
    public boolean isHaveVehiclePass() {
        return haveVehiclePass;
    }

    public Long getTixSourceId() {
        return tixSourceId;
    }

    public void setHaveVehiclePass(boolean haveVehiclePass) {
        this.haveVehiclePass = haveVehiclePass;
    }

    public void setTixSourceId(Long tixSourceId) {
        this.tixSourceId = tixSourceId;
    }
    
    public void setShirtSizeId (Long shirtSizeId) {
        this.shirtSizeId = shirtSizeId;
    }

}
