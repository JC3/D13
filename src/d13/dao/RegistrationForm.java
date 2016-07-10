package d13.dao;

import org.joda.time.DateTime;

import d13.util.Util;
import d13.web.DataView;

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
    private String departureDate = "";
    private String departureTime = "";
    private Location drivingFromTo;
    private String drivingFromToOther;
    private RVSelection rvType = RVSelection.NOT_STAYING_IN;
    private String sharingWith;
    private String livingIn;
    private String livingInOther;
    private String livingSpaceSize;
    private boolean driving;
    private String rideSpaceTo = "";
    private String rideSpaceFrom = "";
    private boolean parkAtCamp;
    private String vehicleComments;
    private boolean needRideTo;
    private boolean needRideFrom;
    private boolean tixForSale;
    private int numForSale;
    private boolean tixWanted;
    private int numWanted;
    //private User groupLeader; // removed 2016
    private String personalProject;
    private String comments;
    private boolean haveVehiclePass;
    private int tixSource; // TODO: TixSourceSelection
    
    RegistrationForm () {
    }
    
    RegistrationForm (User user) {
        this.user = user;
        // no more default; force user to select
        //this.drivingFromTo = user.getLocation();
        //this.drivingFromToOther = user.getLocationOther();
        //this.groupLeader = user;  // removed 2016
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
    
    public DateTime getCompletionTime () {
        return completionTime;
    }

    @DataView(i=10, n="Helped Off Playa", longtext=true)
    public String getHelpedOffPlaya() {
        return helpedOffPlaya;
    }

    @DataView(i=20, n="Helped Alpha?")
    public boolean isHelpedAlphaCamp() {
        return helpedAlphaCamp;
    }

    @DataView(i=30, n="Helped DPW?")
    public boolean isHelpedCampBuild() {
        return helpedCampBuild;
    }

    @DataView(i=40, n="Helped Disengage?")
    public boolean isHelpedDisengage() {
        return helpedDisengage;
    }

    @DataView(i=50, n="Helped Lead?")
    public boolean isHelpedLeadRoles() {
        return helpedLeadRoles;
    }

    @DataView(i=60, n="Helped Love Ministry?")
    public boolean isHelpedLoveMinistry() {
        return helpedLoveMinistry;
    }

    @DataView(i=70, n="Helped LNT?")
    public boolean isHelpedLNT() {
        return helpedLNT;
    }

    @DataView(i=80, n="Helped Other?")
    public boolean isHelpedOther() {
        return helpedOther;
    }

    @DataView(i=90, n="Helped Other", longtext=true)
    public String getHelpedOtherOther() {
        return helpedOtherOther;
    }

    @DataView(i=100, n="Dis Virgin?")
    public boolean isDisorientVirgin() {
        return disorientVirgin;
    }

    @DataView(i=110, n="BM Virgin?")
    public boolean isBmVirgin() {
        return bmVirgin;
    }

    @DataView(i=120, n="Sponsor", longtext=true)
    public String getSponsor() {
        return sponsor;
    }

    @DataView(i=130, n="Sponsor For", longtext=true)
    public String getSponsorFor() {
        return sponsorFor;
    }

    @DataView(i=140, n="Arrival Date")
    public String getArrivalDate() {
        return arrivalDate;
    }

    /*@DataView(i=150, n="Arrival Time")
    public String getArrivalTime() {
        return arrivalTime;
    }*/

    @DataView(i=160, n="Departure Date")
    public String getDepartureDate() {
        return departureDate;
    }

    @DataView(i=170, n="Departure Time")
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
    
    @DataView(i=180, n="From/To Location")
    public String getDrivingFromToDisplay () {
        if (drivingFromTo == null)
            return "";
        else if (drivingFromTo == Location.OTHER)
            return drivingFromToOther;
        else
            return drivingFromTo.toDisplayString();
    }

    @DataView(i=190, n="R.V.?")
    public RVSelection getRvType() {
        return rvType;
    }
    
    public Long getRvTypeId () {
        return rvType == null ? null : (long)rvType.toDBId();
    }

    @DataView(i=191, n="Sharing Space With", longtext=true)
    public String getSharingWith() {
        return sharingWith;
    }

    @DataView(i=192, n="Living In")
    public String getLivingIn() {
        return livingIn;
    }
    
    @DataView(i=193, n="Living In (Other)")
    public String getLivingInOther() {
        return livingInOther;
    }

    @DataView(i=194, n="Living Space Size", longtext=true)
    public String getLivingSpaceSize() {
        return livingSpaceSize;
    }

    @DataView(i=200, n="Driving?")
    public boolean isDriving() {
        return driving;
    }

    @DataView(i=210, n="Ride Space To?")
    public String getRideSpaceTo() {
        return rideSpaceTo;
    }

    @DataView(i=220, n="Ride Space From?")
    public String getRideSpaceFrom() {
        return rideSpaceFrom;
    }

    @DataView(i=230, n="Parking At Camp?")
    public boolean isParkAtCamp() {
        return parkAtCamp;
    }

    @DataView(i=240, n="Vehicle Comments", longtext=true)
    public String getVehicleComments() {
        return vehicleComments;
    }

    @DataView(i=250, n="Need Ride To?")
    public boolean isNeedRideTo() {
        return needRideTo;
    }

    @DataView(i=260, n="Need Ride From?")
    public boolean isNeedRideFrom() {
        return needRideFrom;
    }

    @DataView(i=265, n="Have Vehicle Pass?")
    public boolean isHaveVehiclePass() {
        return haveVehiclePass;
    }

    /* removed 2016
    @DataView(i=2270, n="Group Leader")
    public User getGroupLeader() {
        return groupLeader;
    }
    
    public Long getGroupLeaderId () {
        return groupLeader == null ? null : groupLeader.getUserId();
    }
    */

    @DataView(i=2280, n="Personal Projects", longtext=true)
    public String getPersonalProject() {
        return personalProject;
    }

    @DataView(i=2290, n="User's Comments", longtext=true)
    public String getComments() {
        return comments;
    }

    public boolean isTixForSale() {
        return tixForSale;
    }

    @DataView(i=1000, n="Tickets For Sale")
    public int getNumForSale() {
        return tixForSale ? numForSale : 0;
    }

    public boolean isTixWanted() {
        return tixWanted;
    }

    @DataView(i=101, n="Tickets Needed")
    public int getNumWanted() {
        return tixWanted ? numWanted : 0;
    }

    @DataView(i=102, n="Ticket Source")
    public int getTixSource() {
        return tixSource;
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

    /*public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = Util.require(arrivalTime, "Arrival time");
    }*/

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

    public void setHaveVehiclePass (boolean have) {
        this.haveVehiclePass = have;
    }
    
    public void setTixSource(int tixSource) {
        this.tixSource = tixSource;
    }

    public void setDrivingFromToOther (String drivingFromToOther) {
        this.drivingFromToOther = drivingFromToOther;
    }
    
    public void setDrivingFromToIdOther (String drivingFromToOther) {
        setDrivingFromToOther(drivingFromToOther);
    }

    public void setRvType(RVSelection rvType) {
        if (rvType == null)
            throw new IllegalArgumentException("R.V. selection must be specified.");
        user.setRvDueOwed(rvType == RVSelection.RESPONSIBLE); // note: requires hack to initialize user's rvDueItem from db in EditRegistration.
        this.rvType = rvType;
    }
    
    public void setRvTypeId (Long id) {
        if (id == null)
            throw new IllegalArgumentException("R.V. selection must be specified.");
        RVSelection sel = RVSelection.fromDBId((int)(long)id);
        if (sel == null)
            throw new IllegalArgumentException("Invalid ID for RV selection.");
        user.setRvDueOwed(sel == RVSelection.RESPONSIBLE); // note: same as above
        this.rvType = sel;
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

    /*
    // removed 2016
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
    */

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
        if (helpedOther)
            Util.require(helpedOtherOther, "Other on-playa event");
        if (drivingFromTo == Location.OTHER)
            Util.require(drivingFromToOther, "Other driving location");
        if (bmVirgin && !disorientVirgin)
            throw new IllegalArgumentException("If this is your first time at Burning Man, it is also your first time with Disorient. Please correct your answers!");
        if (disorientVirgin)
            Util.require(sponsor, "Sponsor name");
        if ("other".equalsIgnoreCase(livingIn))
            Util.require(livingInOther, "Other living space type");
        if (tixForSale && numForSale <= 0)
            throw new IllegalArgumentException("Number of tickets for sale must be specified.");
        if (tixWanted && numWanted <= 0)
            throw new IllegalArgumentException("Number of tickets wanted must be specified.");
    }
    
    public void setCompletionTimeNow () {
        completionTime = DateTime.now();
    }
    
}
