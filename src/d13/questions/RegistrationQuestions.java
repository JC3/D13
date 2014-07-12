package d13.questions;

import java.util.ArrayList;
import java.util.List;

import d13.dao.Location;
import d13.dao.RVSelection;

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
        
        q = Question.newDropList("arrivalDate", "Date of arrival on the playa.", "Want to arrive early and help Alpha Camp build Disorient?");
        q.addChoice("8/18", "8/18/2014");
        q.addChoice("8/19", "8/19/2014");
        q.addChoice("8/20", "8/20/2014");
        q.addChoice("8/21", "8/21/2014");
        q.addChoice("8/22", "8/22/2014");
        q.addChoice("8/23", "8/23/2014");
        q.addChoice("8/24", "8/24/2014");
        q.addChoice("8/25", "8/25/2014");
        q.addChoice("8/26", "8/26/2014");
        q.addChoice("8/27", "8/27/2014");
        qs.add(q);
        
        q = Question.newDropList("arrivalTime", "Time of arrival on the playa.", null);
        q.addChoice("Morning", "Morning");
        q.addChoice("Afternoon", "Afternoon");
        q.addChoice("Evening", "Evening");
        q.addChoice("Late Night", "Night");
        qs.add(q);
        
        q = Question.newDropList("departureDate", "Date of departure from the playa.", "Remember, you can get up to a $250 rebate if you stay for Disengage!");
        q.addChoice("8/30", "8/30/2014");
        q.addChoice("8/31", "8/31/2014");
        q.addChoice("9/1", "9/1/2014");
        q.addChoice("9/2", "9/2/2014");
        q.addChoice("9/3", "9/3/2014");
        q.addChoice("9/4", "9/4/2014");
        q.addChoice("9/5", "9/5/2014");
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
                "We need to know your approximate foot print. The height of your tent, yurt, RV or teepee. Any personal shade structures you will be constructing. This will be crucial to making sure all placement needs are honored. The more information you can give us the less likely we are to bother you with more questions. While Disorient provides all tent campers with shade, we require that each camper purchase and bring with them one 10 by 10 orange tarp (<a href=\"http://www.tents-canopy.com/orange-tarp-10x10.html\">http://www.tents-canopy.com/orange-tarp-10x10.html</a>) to expand shade as necessary.");
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
