package d13.dao;

import org.joda.time.DateTime;

import d13.changetrack.Track;
import d13.changetrack.Trackable;
import d13.util.Util;
import d13.web.DataView;

public class RegistrationForm implements Trackable {

    private long rformId;
    private User user;
    private DateTime completionTime;
    
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
    private TicketSource tixSource = TicketSource.NONE; 
    private TeeShirtSize shirtSize;
    
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

    @Track
    @DataView(i=100, n="Dis Virgin?")
    public boolean isDisorientVirgin() {
        return disorientVirgin;
    }

    @Track
    @DataView(i=110, n="BM Virgin?")
    public boolean isBmVirgin() {
        return bmVirgin;
    }

    @Track
    @DataView(i=120, n="Sponsor", longtext=true)
    public String getSponsor() {
        return sponsor;
    }

    @Track
    @DataView(i=130, n="Sponsor For", longtext=true)
    public String getSponsorFor() {
        return sponsorFor;
    }

    @Track
    @DataView(i=140, n="Arrival Date")
    public String getArrivalDate() {
        return arrivalDate;
    }

    /*@DataView(i=150, n="Arrival Time")
    public String getArrivalTime() {
        return arrivalTime;
    }*/

    @Track
    @DataView(i=160, n="Departure Date")
    public String getDepartureDate() {
        return departureDate;
    }

    @Track
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
    
    public TeeShirtSize getShirtSize () {
        return shirtSize;
    }
    
    public Long getShirtSizeId () {
        return shirtSize == null ? null : (long)shirtSize.toDBId();
    }
    
    public String getDrivingFromToOther () {
        return drivingFromToOther;
    }
    
    public String getDrivingFromToIdOther () { // hack
        return getDrivingFromToOther();
    }
    
    @Track
    @DataView(i=180, n="From/To Location")
    public String getDrivingFromToDisplay () {
        if (drivingFromTo == null)
            return "";
        else if (drivingFromTo == Location.OTHER)
            return drivingFromToOther;
        else
            return drivingFromTo.toDisplayString();
    }

    @Track
    @DataView(i=190, n="R.V.?")
    public RVSelection getRvType() {
        return rvType;
    }
    
    public Long getRvTypeId () {
        return rvType == null ? null : (long)rvType.toDBId();
    }
    
    public Long getTixSourceId () {
        return tixSource == null ? null : (long)tixSource.toDBId();
    }

    @Track
    @DataView(i=191, n="Sharing Space With", longtext=true)
    public String getSharingWith() {
        return sharingWith;
    }

    @Track
    @DataView(i=192, n="Living In")
    public String getLivingIn() {
        return livingIn;
    }
    
    @Track
    @DataView(i=193, n="Living In (Other)")
    public String getLivingInOther() {
        return livingInOther;
    }

    @Track
    @DataView(i=194, n="Living Space Size", longtext=true)
    public String getLivingSpaceSize() {
        return livingSpaceSize;
    }

    @Track
    @DataView(i=200, n="Driving?")
    public boolean isDriving() {
        return driving;
    }

    @Track
    @DataView(i=210, n="Ride Space To?")
    public String getRideSpaceTo() {
        return rideSpaceTo;
    }

    @Track
    @DataView(i=220, n="Ride Space From?")
    public String getRideSpaceFrom() {
        return rideSpaceFrom;
    }

    @Track
    @DataView(i=230, n="Parking At Camp?")
    public boolean isParkAtCamp() {
        return parkAtCamp;
    }

    @Track
    @DataView(i=240, n="Vehicle Comments", longtext=true)
    public String getVehicleComments() {
        return vehicleComments;
    }

    @Track
    @DataView(i=250, n="Need Ride To?")
    public boolean isNeedRideTo() {
        return needRideTo;
    }

    @Track
    @DataView(i=260, n="Need Ride From?")
    public boolean isNeedRideFrom() {
        return needRideFrom;
    }

    @Track
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

    @Track
    @DataView(i=2280, n="Personal Projects", longtext=true)
    public String getPersonalProject() {
        return personalProject;
    }

    @Track
    @DataView(i=2285, n="Shirt Size")
    public String getShirtSizeDisplay () {
        if (shirtSize == null)
            return "";
        else
            return shirtSize.toDisplayString();
    }

    @Track
    @DataView(i=2290, n="User's Comments", longtext=true)
    public String getComments() {
        return comments;
    }

    public boolean isTixForSale() {
        return tixForSale;
    }

    @Track
    @DataView(i=1000, n="Tickets For Sale")
    public int getNumForSale() {
        return tixForSale ? numForSale : 0;
    }

    public boolean isTixWanted() {
        return tixWanted;
    }

    @Track
    @DataView(i=101, n="Tickets Needed")
    public int getNumWanted() {
        return tixWanted ? numWanted : 0;
    }

    @Track
    @DataView(i=102, n="Ticket Source")
    public TicketSource getTixSource() {
        return tixSource;
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
    
    public void setShirtSize (TeeShirtSize shirtSize) {
        if (shirtSize == null)
            throw new IllegalArgumentException("Tee-shirt size must be specified.");
        this.shirtSize = shirtSize;
    }
    
    public void setShirtSizeId (Long shirtSizeId) {
        if (shirtSizeId == null || shirtSizeId == 0)
            throw new IllegalArgumentException("Tee-shirt size must be specified.");
        TeeShirtSize size = TeeShirtSize.fromDBId((int)(long)shirtSizeId);
        if (size == null)
            throw new IllegalArgumentException("Invalid tee-shirt size ID.");
        this.shirtSize = size;
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
    
    public void setTixSource(TicketSource tixSource) {
        if (tixSource == null)
            throw new IllegalArgumentException("Ticket source must be specified.");
        this.tixSource = tixSource;
    }
    
    public void setTixSourceId (Long tixSourceId) {
        if (tixSourceId == null)
            throw new IllegalArgumentException("Ticket source must be specified.");
        TicketSource ts = TicketSource.fromDBId((int)(long)tixSourceId);
        if (ts == null)
            throw new IllegalArgumentException("Invalid ticket source ID for ticket source.");
        this.tixSource = ts;
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
    
    @Override public String toString () {
        return String.format("%s[user=%s]", rformId, user);
    }
    
}
