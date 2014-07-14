package d13.questions;

import java.util.ArrayList;
import java.util.List;

import d13.dao.Location;

import d13.dao.Gender;

public class ProfileQuestions {

    private static final List<Question> qs;
    
    static {
        Question q;
        qs = new ArrayList<Question>();
        qs.add(Question.newShortText("email", "Email", "We will be sending periodic updates, which will often require a timely reply, so please give us your most used address.").setForLogin(true));
        qs.add(Question.newPassword("password", "Password", null).setForLogin(true));
        qs.add(Question.newPassword("password2", "Confirm Password", null).setForLogin(true));
        qs.add(Question.newShortText("realName", "Real Name", "Please enter your first and last name."));
        qs.add(Question.newShortText("playaName", "Playa Name", "This is how you will appear in the registration list. You can leave it blank if you don't have one."));
        q = Question.newSingleChoice("gender", "Gender", null);
        q.addChoice(Gender.MALE.toDisplayString(), Gender.MALE.toDBString());
        q.addChoice(Gender.FEMALE.toDisplayString(), Gender.FEMALE.toDBString());
        qs.add(q);
        qs.add(Question.newShortText("phone", "Cell Phone", null));
        q = Question.newSingleChoice("location", "Location", null);
        for (Location l:Location.values())
            if (l != Location.OTHER)
                q.addChoice(l.toDisplayString(), l.toDBId());
        q.addOtherChoice(Location.OTHER.toDisplayString(), Location.OTHER.toDBId());
        qs.add(q);
        qs.add(Question.newLongText("emergencyContact", "Emergency Contact", "Please provide the name and telephone number of one emergency contact person (preferably off-playa). This information is required. <em>Any submissions without valid emergency contact information will not be considered.</em>"));
    }
    
    public static final List<Question> getQuestions () {
        return qs;
    }
    
}
