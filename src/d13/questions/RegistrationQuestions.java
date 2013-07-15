package d13.questions;

import java.util.ArrayList;
import java.util.List;

import d13.dao.Location;

public class RegistrationQuestions {

    private static final List<Question> qs;
    
    static {
        
        Question q;
        qs = new ArrayList<Question>();
        
        qs.add(Question.newLongText("helpedOffPlaya", 
                "Have you contributed to any off-playa Disorient events?", 
                "If so, which ones and in what capacity?"));
        
        q = Question.newMultiChoice("Have you contributed to any on-playa Disorient events?", 
                "If so, please check all that apply.");
        q.addChoice("helpedAlphaCamp", "Alpha Camp", 1);
        q.addChoice("helpedCampBuild", "Camp Build / DPW", 1);
        q.addChoice("helpedDisengage", "Disengage", 1);
        q.addChoice("helpedLeadRoles", "Lead Camp Roles", 1);
        q.addChoice("helpedLoveMinistry", "Love Ministry", 1);
        q.addChoice("helpedLNT", "LNT", 1);
        q.addOtherChoice("helpedOther", "Other", 1);
        qs.add(q);

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
        
        q = Question.newDropList("arrivalDate", "Date of arrival on the playa.", null);
        q.addChoice("8/19", "8/19/2013");
        q.addChoice("8/20", "8/20/2013");
        q.addChoice("8/21", "8/21/2013");
        q.addChoice("8/22", "8/22/2013");
        q.addChoice("8/23", "8/23/2013");
        q.addChoice("8/24", "8/24/2013");
        q.addChoice("8/25", "8/25/2013");
        q.addChoice("8/26", "8/26/2013");
        q.addChoice("8/27", "8/27/2013");
        q.addChoice("8/28", "8/28/2013");
        qs.add(q);
        
        q = Question.newDropList("arrivalTime", "Time of arrival on the playa.", null);
        q.addChoice("Morning", "Morning");
        q.addChoice("Afternoon", "Afternoon");
        q.addChoice("Evening", "Evening");
        q.addChoice("Late Night", "Night");
        qs.add(q);
        
        q = Question.newDropList("departureDate", "Date of departure from the playa.", null);
        q.addChoice("8/31", "8/31/2013");
        q.addChoice("9/1", "9/1/2013");
        q.addChoice("9/2", "9/2/2013");
        q.addChoice("9/3", "9/3/2013");
        q.addChoice("9/4", "9/4/2013");
        q.addChoice("9/5", "9/5/2013");
        q.addChoice("9/6", "9/6/2013");
        qs.add(q);
        
        q = Question.newDropList("departureTime", "Time of departure from the playa.", null);
        q.addChoice("Morning", "Morning");
        q.addChoice("Afternoon", "Afternoon");
        q.addChoice("Evening", "Evening");
        q.addChoice("Late Night", "Night");
        qs.add(q);

        q = Question.newSingleChoice("drivingFromToId",
                "Where will you be driving from/to?",
                "If you are flying from one area to another, choose the point at which you will be getting into an actual vehicle to make your final approach to BRC.");
        for (Location l:Location.values())
            if (l != Location.OTHER)
                q.addChoice(l.toDisplayString(), l.toDBId());
        q.addOtherChoice(Location.OTHER.toDisplayString(), Location.OTHER.toDBId());
        qs.add(q);

        q = Question.newSingleChoice("rv", "Are you staying in an R.V.?", null);
        q.addChoice("Yes", 1);
        q.addChoice("No", 0);
        qs.add(q);
        
        q = Question.newSingleChoice("driving", "Are you planning on bringing a vehicle to the playa?", null);
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
        
        qs.add(Question.newLongText("vehicleComments", "Additional vehicle comments?", null));
        
        // end if
        
        q = Question.newMultiChoice("Do you need a ride either one or both ways?", null);
        q.addChoice("needRideTo", "Arrival", 1);
        q.addChoice("needRideFrom", "Departure", 1);
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
        
        
        
        q = Question.newUserDropList("groupLeaderId",
                "Who will be your group leader?",
                "This individual is responsible for coordinating with Disorient about registration, arrival, disengage, etc. If your group leader has not yet registered, please just select yourself for now - you'll be able to change it later."); 
        qs.add(q);

        qs.add(Question.newLongText("personalProject",
                "Are you doing any personal projects on the playa this year?",
                "If so, please briefly describe your project, let us know if there are any large items that require placement, and specify who is responsible for the project's disengage and on what day. Also, let us know if your project is something you would like to set up on Disorient's frontage for the general Burning Man public."));

        qs.add(Question.newLongText("comments", "Do you have anything else to add?", null));
        
    }
    
    public static final List<Question> getQuestions () {
        return qs;
    }
    
}
