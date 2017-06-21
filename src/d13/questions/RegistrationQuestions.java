package d13.questions;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import d13.ThisYear;
import d13.ThisYear.PlayaWeek;
import d13.dao.Location;
import d13.dao.RVSelection;
import d13.dao.TeeShirtSize;
import d13.dao.TicketSource;

public class RegistrationQuestions {

    private static final List<Question> qs;
    
    static void addDateChoice (Question q, ThisYear.PlayaWeek week, int daysSinceMonday) {
        
        DateTime when = week.getDate(daysSinceMonday);
        String ui = when.toString("EEEE M/d");
        String db = when.toString("M/d/YYYY");
        q.addChoice(ui, db);

    }
    
    static {
        
        Question q;
        qs = new ArrayList<Question>();
        
        q = Question.newSingleChoice("disorientVirgin", "Will this be your first time camping with Disorient?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);
        
        // if disorientVirgin:
       
        q = Question.newSingleChoice("bmVirgin", "Will this be your first time at Burning Man?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);
       
        qs.add(Question.newLongText("sponsor", 
                "Who is sponsoring you?", 
                "If you are a new camper, a reference is <i>required</i> to camp with Disorient. A sponsor is somebody who has camped with Disorient before and can provide a reference. Please speak to your sponsor(s) before you list them here! <i>Any submissions without a sponsor will not be considered.</i>"));
        
        // else

        qs.add(Question.newLongText("sponsorFor",
                "Are you a sponsor for anybody?",
                "If you are acting as a sponsor for another camper (or campers), please write their names and a ~100 word recommendation for each here. If you are a sponsor for somebody and do not write a recommendation here, we will contact you during the approval process for a recommendation."));
        
        // end if

        q = Question.newDropList("arrivalDate", "Date of arrival on the playa.", "Want to arrive early and help Alpha Camp build Disorient?");
        addDateChoice(q, PlayaWeek.ALPHA, 0); // mon
        addDateChoice(q, PlayaWeek.ALPHA, 1);
        addDateChoice(q, PlayaWeek.ALPHA, 2);
        addDateChoice(q, PlayaWeek.ALPHA, 3);
        addDateChoice(q, PlayaWeek.ALPHA, 4);
        addDateChoice(q, PlayaWeek.ALPHA, 5);
        addDateChoice(q, PlayaWeek.ALPHA, 6); // sun
        addDateChoice(q, PlayaWeek.ALPHA, 7); // mon
        addDateChoice(q, PlayaWeek.ALPHA, 8); // tue
        qs.add(q);
        
        q = Question.newDropList("departureDate", "Date of departure from the playa.", "Remember, you can get up to a $240 rebate if you stay for Disengage!");
        addDateChoice(q, PlayaWeek.DISENGAGE, -4); // thu
        addDateChoice(q, PlayaWeek.DISENGAGE, -3);
        addDateChoice(q, PlayaWeek.DISENGAGE, -2);
        addDateChoice(q, PlayaWeek.DISENGAGE, -1);
        addDateChoice(q, PlayaWeek.DISENGAGE, 0);  // mon
        addDateChoice(q, PlayaWeek.DISENGAGE, 1);
        addDateChoice(q, PlayaWeek.DISENGAGE, 2);
        addDateChoice(q, PlayaWeek.DISENGAGE, 3);  // thu
        qs.add(q);
        
        q = Question.newDropList("departureTime", "Time of departure from the playa.", null);
        q.addChoice("Morning", "Morning");
        q.addChoice("Afternoon", "Afternoon");
        q.addChoice("Evening", "Evening");
        q.addChoice("Late Night", "Night");
        qs.add(q);

        q = Question.newSingleChoice("drivingFromToId",
                "Where will you be driving or riding from?",
                "Where in North America (regardless of air travel) are you driving in from? If flying directly to the airfield at BRC, choose \"Flying to BRC\".");
        for (Location l:Location.values())
            if (l != Location.OTHER)
                q.addChoice(l.toDisplayString(), l.toDBId());
        q.addOtherChoice(Location.OTHER.toDisplayString(), Location.OTHER.toDBId());
        qs.add(q);

        /*q = Question.newSingleChoice("rv", "Are you staying in an R.V.?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);*/
        
        q = Question.newSingleChoice("rvTypeId", 
                "Are you *responsible* for an R.V.?", 
                "The person responsible for each R.V. will be responsible for paying the fee for that R.V. Please coordinate with the others in your R.V. to determine who will be responsible. <em>All R.V. fees must be paid or we will not place your R.V.!</em>");
        q.addChoice("No, and I am not staying in an R.V.", RVSelection.NOT_STAYING_IN.toDBId());
        q.addChoice("No, but I am staying in somebody else's R.V.", RVSelection.STAYING_IN.toDBId());
        q.addChoice("Yes, I am responsible for one R.V.", RVSelection.RESPONSIBLE.toDBId());
        qs.add(q);
        
        //-- added for 2014; TODO: confirm on approval survey
        
        q = Question.newLongText("sharingWith",
                "Who is sharing your space?",
                "Please provide us with the names and playa names (if they have them) of people who are staying in your tent, R.V., sleeping bag, etc.");
        qs.add(q);

        q = Question.newSingleChoice("livingIn",
                "What will you be living in for the duration of the burn?",
                null);
        q.addChoice("Tent", "Tent");
        q.addChoice("R.V.", "R.V.");
        //q.addChoice("TeePee", "TeePee");
        q.addChoice("Yurt", "Yurt");
        q.addOtherChoice("Other", "Other");
        qs.add(q);
        
        q = Question.newLongText("livingSpaceSize",
                "What size is your living space on the playa?",
                "We need to know your approximate foot print. The height of your tent, yurt, or RV. Any personal shade structures you will be constructing. This will be crucial to making sure all placement needs are honored. The more information you can give us the less likely we are to bother you with more questions. Disorient tries to provide all tent campers with shade, however if you would like to contribute to our shade or bring your own, we suggest a 10 by 10 orange tarp (<a href=\"http://www.tents-canopy.com/orange-tarp-10x10.html\">http://www.tents-canopy.com/orange-tarp-10x10.html</a>) to expand shade within our structure as necessary.");
        qs.add(q);

        //--
        
        q = Question.newSingleChoice("driving", "Are you planning on bringing a vehicle (aside from an R.V.) to the playa?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);

        // if yes
        
        q = Question.newSingleChoice("rideSpaceTo", "Will you have space for ride shares to the playa?", null);
        q.addChoice("Yes", "Yes");
        q.addChoice("Maybe", "Maybe");
        q.addChoice("No", "No");
        qs.add(q);
        
        q = Question.newSingleChoice("rideSpaceFrom", "Will you have space for ride shares leaving the playa?", null);
        q.addChoice("Yes", "Yes");
        q.addChoice("Maybe", "Maybe");
        q.addChoice("No", "No");
        qs.add(q);
        
        q = Question.newSingleChoice("parkAtCamp", "Are you expecting to park your vehicle at camp?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);

        q = Question.newSingleChoice("haveVehiclePass", "Do you have a vehicle pass for your vehicle?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);

        qs.add(Question.newLongText("vehicleComments", "Additional vehicle comments?", null));
        
        // end if
        
        q = Question.newMultiChoice("Do you need a ride either one or both ways?", null);
        q.addChoice("needRideTo", "Arrival", 1);
        q.addChoice("needRideFrom", "Departure", 1);
        qs.add(q);

        q = Question.newSingleChoice("tixSourceId", "Do you have a regular BM ticket or a Disorient Group Sale ticket?", null);
        q.addChoice("Regular", TicketSource.REGULAR.toDBId());
        q.addChoice("Group Sale", TicketSource.DGS.toDBId());
        q.addChoice("I don't have a ticket.", TicketSource.NONE.toDBId());
        qs.add(q);
        
        q = Question.newSingleChoice("tixForSale", "Do you have any BM tickets for sale?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);
        
        q = Question.newShortText("numForSale", "How many are you selling?", null);
        qs.add(q);
        
        q = Question.newSingleChoice("tixWanted", "Do you need BM tickets?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);
        
        q = Question.newShortText("numWanted", "How many do you need?", null);
        qs.add(q);
        
        /* removed in 2016
        q = Question.newUserDropList("groupLeaderId",
                "Who will be your group leader?",
                "This individual is responsible for coordinating with Disorient about registration, arrival, disengage, etc. If your group leader has not yet registered, please just select yourself for now - you'll be able to change it later."); 
        qs.add(q);
        */

        qs.add(Question.newLongText("personalProject",
                "Are you doing any personal projects on the playa this year?",
                "If so, please briefly describe your project, let us know if there are any large items that require placement, and specify who is responsible for the project's disengage and on what day. Also, let us know if your project is something you would like to set up on Disorient's frontage for the general Burning Man public."));

        q = Question.newSingleChoice("shirtSizeId", "What is your tee-shirt size?", null);
        for (TeeShirtSize s:TeeShirtSize.values())
            q.addChoice(s.toDisplayString(), s.toDBId());
        qs.add(q);

        qs.add(Question.newLongText("comments", "Do you have anything else to add?", "Do you have any special skills to share with Disorient, or any other comments?"));
        
    }
    
    public static final List<Question> getQuestions () {
        return qs;
    }
    
}
