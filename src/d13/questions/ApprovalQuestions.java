package d13.questions;

import java.util.ArrayList;
import java.util.List;

import d13.ThisYear;

public class ApprovalQuestions {

    private static final List<Question> qs;
    
    static {
        Question q;
        qs = new ArrayList<Question>();
        
        /* moved to registration form for 2014:
         
        q = Question.newLongText("sharingWith",
                "Who is sharing your space?",
                "Please provide us with the names and playa names (if they have them) of people who are staying in your tent, R.V., sleeping bag, etc.");
        qs.add(q);
        
        q = Question.newSingleChoice("livingIn",
                "What will you be living in for the duration of the burn?",
                null);
        q.addChoice("Tent", "Tent");
        q.addChoice("R.V.", "R.V.");
        q.addChoice("TeePee", "TeePee");
        q.addChoice("Yurt", "Yurt");
        q.addOtherChoice("Other", "Other");
        qs.add(q);
        
        q = Question.newLongText("livingSpaceSize",
                "What size is your living space on the playa?",
                "We need to know your approximate foot print. The height of your tent, yurt, RV or teepee. Any personal shade structures you will be constructing. This will be crucial to making sure all placement needs are honored. The more information you can give us the less likely we are to bother you with more questions. While Disorient provides all tent campers with shade, we require that each camper purchase and bring with them one 10 by 10 orange tarp (<a href=\"http://www.tents-canopy.com/orange-tarp-10x10.html\">http://www.tents-canopy.com/orange-tarp-10x10.html</a>) to expand shade as necessary.");
        qs.add(q);
        */
        
        q = Question.newLongText("mealPreference",
                "What I Eat",
                "Yes Disorient provides 2 meals a day to campers help us prepare and tell us any dietary restrictions you may have. This should include things like vegan, nut allergies etc and NOT things like \"I don't like tomatoes\".");
        qs.add(q);
        
        q = Question.newLongText("placementRequest",
                "Placement Requests",
                "Please let us know any special request such as proximity to the kitchen, pornj star lounge or people you absolutely HAVE to camp near. We will do our best when working out placement to honor all requests.");
        qs.add(q);
        
        q = Question.newMultiChoice("I am a Disengage Rock Star",
                "Disengage rebate is up to $240. Please check EACH day that you plan to help DisEngage. Each full work day may entitle you to a rebate. **IMPORTANT: Keep this information updated if your travel plans change! We need an accurate headcount. Email <a href=\"mailto:disengage@disorient.info?subject=Disorient " + ThisYear.CAMP_YEAR + " Disengage\">disengage@disorient.info</a> with updates, questions, suggestions, etc.");
        q.addChoice("disengageSun", "Sunday, September 6th", 1);
        q.addChoice("disengageMon", "Monday, September 7th", 1);
        q.addChoice("disengageTue", "Tuesday, September 8th", 1);
        q.addChoice("disengageWed", "Wednesday, September 9th", 1);
        q.addChoice("disengageNone", "I'm sorry, I cannot stay to help.", 1);
        qs.add(q);
        
    }
    
    public static final List<Question> getQuestions () {
        return qs;
    }
    
    public static final boolean isBlank () {
        return getQuestions().isEmpty();
    }
    
}
