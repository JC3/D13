package d13.web;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;

public class RegistrationBean {

    private String helpedOffPlaya;
    private boolean helpedAlphaCamp;
    private boolean helpedCampBuild;
    private boolean helpedDisengage;
    private boolean helpedLeadRoles;
    private boolean helpedLoveMinistry;
    private boolean helpedLNT;
    private boolean helpedOther;
    private String helpedOtherOther;
    private boolean disorientVirgin;
    private boolean bmVirgin;
    private String sponsor;
    private String sponsorFor;
    private String arrivalDate;
    private String arrivalTime;
    private String departureDate;
    private String departureTime;
    private Long drivingFromToId;
    private String drivingFromToIdOther;
    private Long rvTypeId;
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
    private Long groupLeaderId;
    private String personalProject;
    private String comments;
    
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

    public String getHelpedOffPlaya() {
        return helpedOffPlaya;
    }

    public boolean isHelpedAlphaCamp() {
        return helpedAlphaCamp;
    }

    public boolean isHelpedCampBuild() {
        return helpedCampBuild;
    }

    public boolean isHelpedDisengage() {
        return helpedDisengage;
    }

    public boolean isHelpedLeadRoles() {
        return helpedLeadRoles;
    }

    public boolean isHelpedLoveMinistry() {
        return helpedLoveMinistry;
    }

    public boolean isHelpedLNT() {
        return helpedLNT;
    }

    public boolean isHelpedOther() {
        return helpedOther;
    }

    public String getHelpedOtherOther() {
        return helpedOtherOther;
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

    public String getArrivalTime() {
        return arrivalTime;
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

    public Long getGroupLeaderId() {
        return groupLeaderId;
    }

    public String getPersonalProject() {
        return personalProject;
    }

    public String getComments() {
        return comments;
    }

    public void setHelpedOffPlaya(String helpedOffPlaya) {
        this.helpedOffPlaya = helpedOffPlaya;
    }

    public void setHelpedAlphaCamp(boolean helpedAlphaCamp) {
        this.helpedAlphaCamp = helpedAlphaCamp;
    }

    public void setHelpedCampBuild(boolean helpedCampBuild) {
        this.helpedCampBuild = helpedCampBuild;
    }

    public void setHelpedDisengage(boolean helpedDisengage) {
        this.helpedDisengage = helpedDisengage;
    }

    public void setHelpedLeadRoles(boolean helpedLeadRoles) {
        this.helpedLeadRoles = helpedLeadRoles;
    }

    public void setHelpedLoveMinistry(boolean helpedLoveMinistry) {
        this.helpedLoveMinistry = helpedLoveMinistry;
    }

    public void setHelpedLNT(boolean helpedLNT) {
        this.helpedLNT = helpedLNT;
    }

    public void setHelpedOther(boolean helpedOther) {
        this.helpedOther = helpedOther;
    }

    public void setHelpedOtherOther(String helpedOtherOther) {
        this.helpedOtherOther = helpedOtherOther;
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

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
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

    public void setGroupLeaderId(Long groupLeader) {
        this.groupLeaderId = groupLeader;
    }

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
    
}
