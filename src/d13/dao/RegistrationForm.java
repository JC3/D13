package d13.dao;

import org.joda.time.DateTime;

import d13.util.Util;

public class RegistrationForm {

    private long rformId;
    private User user;
    private DateTime completionTime;
    
    private String helpedOffPlaya;
    private boolean helpedAlphaCamp;
    private boolean helpedCampBuild;
    private boolean helpedDisengage;
    private boolean helpedLeadRoles;
    private boolean helpedLoveMinistry;
    private boolean helpedLNT;
    private boolean helpedOther;
    private String helpedOtherOther;
    private boolean disorientVirgin = true;
    private boolean bmVirgin = true;
    private String sponsor = "";
    private String sponsorFor = "";
    private String arrivalDate = "";
    private String arrivalTime = "";
    private String departureDate = "";
    private String departureTime = "";
    private Location drivingFromTo;
    private String drivingFromToOther;
    private boolean rv;
    private boolean driving;
    private String rideSpaceTo = "";
    private String rideSpaceFrom = "";
    private boolean parkAtCamp;
    private String vehicleComments;
    private boolean needRideTo;
    private boolean needRideFrom;
    private User groupLeader;
    private String personalProject;
    private String comments;
    
    RegistrationForm () {
    }
    
    RegistrationForm (User user) {
        this.user = user;
        this.drivingFromTo = user.getLocation();
        this.drivingFromToOther = user.getLocationOther();
        this.groupLeader = user;
    }
    
    public long getRformId () {
        return rformId;
    }
    
    public User getUser () {
        return user;
    }
    
    public boolean isCompleted () {
        return completionTime != null;
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

    public Location getDrivingFromTo() {
        return drivingFromTo;
    }
    
    public Long getDrivingFromToId () {
        return drivingFromTo == null ? null : (long)drivingFromTo.toDBId();
    }
    
    public String getDrivingFromToOther () {
        return drivingFromToOther;
    }
    
    public String getDrivingFromToIdOther () { // hack
        return getDrivingFromToOther();
    }

    public boolean isRv() {
        return rv;
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

    public User getGroupLeader() {
        return groupLeader;
    }
    
    public Long getGroupLeaderId () {
        return groupLeader == null ? null : groupLeader.getUserId();
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
        this.arrivalDate = Util.require(arrivalDate, "Arrival date");
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = Util.require(arrivalTime, "Arrival time");
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = Util.require(departureDate, "Departure date");
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = Util.require(departureTime, "Departure time");
    }

    public void setDrivingFromTo(Location drivingFromTo) {
        if (drivingFromTo == null)
            throw new IllegalArgumentException("Driving from/to location must be specified.");
        this.drivingFromTo = drivingFromTo;
    }
    
    public void setDrivingFromToId(Long locationId) {
        if (locationId == null)
            throw new IllegalArgumentException("Driving from/to location must be specified.");
        Location loc = Location.fromDBId((int)(long)locationId);
        if (loc == null)
            throw new IllegalArgumentException("Invalid location ID for driving location.");
        this.drivingFromTo = loc;
    }

    public void setDrivingFromToOther (String drivingFromToOther) {
        this.drivingFromToOther = drivingFromToOther;
    }
    
    public void setDrivingFromToIdOther (String drivingFromToOther) {
        setDrivingFromToOther(drivingFromToOther);
    }

    public void setRv(boolean rv) {
        this.rv = rv;
    }

    public void setDriving(boolean driving) {
        this.driving = driving;
    }

    public void setRideSpaceTo(String rideSpaceTo) {
        this.rideSpaceTo = (rideSpaceTo == null ? "No" : rideSpaceTo); // TODO: tristate yes/no/maybe type.
    }

    public void setRideSpaceFrom(String rideSpaceFrom) {
        this.rideSpaceFrom = (rideSpaceFrom == null ? "No" : rideSpaceFrom);
    }

    public void setParkAtCamp(boolean parkAtCamp) {
        this.parkAtCamp = parkAtCamp;
    }

    public void setVehicleComments(String vehicleComments) {
        if (vehicleComments != null)
            vehicleComments = vehicleComments.trim();
        this.vehicleComments = vehicleComments;
    }

    public void setNeedRideTo(boolean needRideTo) {
        this.needRideTo = needRideTo;
    }

    public void setNeedRideFrom(boolean needRideFrom) {
        this.needRideFrom = needRideFrom;
    }

    public void setGroupLeader(User groupLeader) {
        if (groupLeader == null)
            throw new IllegalArgumentException("Group leader must be specified.");
        this.groupLeader = groupLeader;
    }
    
    public void setGroupLeaderId (Long id) {
        if (id == null || id == 0)
            throw new IllegalArgumentException("Group leader must be specified.");
        groupLeader = User.findById(id);
    }

    public void setPersonalProject(String personalProject) {
        if (personalProject != null)
            personalProject = personalProject.trim();
        this.personalProject = personalProject;
    }

    public void setComments(String comments) {
        if (comments != null)
            comments = comments.trim();
        this.comments = comments;
    }

    public void validateMisc () throws IllegalArgumentException {
        if (helpedOther)
            Util.require(helpedOtherOther, "Other on-playa event");
        if (drivingFromTo == Location.OTHER)
            Util.require(drivingFromToOther, "Other driving location");
        if (bmVirgin && !disorientVirgin)
            throw new IllegalArgumentException("If this is your first time at Burning Man, it is also your first time with Disorient. Please correct your answers!");
        if (disorientVirgin)
            Util.require(sponsor, "Sponsor name");
    }
    
    public void setCompletionTimeNow () {
        completionTime = DateTime.now();
    }
    
}
