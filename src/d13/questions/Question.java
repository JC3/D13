package d13.questions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {

    public static final int TYPE_SHORT_TEXT = 0;
    public static final int TYPE_LONG_TEXT = 1;
    public static final int TYPE_SINGLE_CHOICE = 2;
    public static final int TYPE_MULTI_CHOICE = 3;
    public static final int TYPE_PASSWORD = 4;
    public static final int TYPE_DROPLIST = 5;
    public static final int TYPE_USER_DROPLIST = 6;
    
    public static class Choice {
        private String field;
        private String text;
        private String value;
        private boolean other;
        private Choice (String field, String text, String value, boolean other) {
            this.field = field;
            this.text = text;
            this.value = value;
            this.other = other;
        }
        public String getField () { return field; }
        public String getText () { return text; }
        public String getValue () { return value; }
        public boolean isOther () { return other; }
    }
    
    private int type;
    private String brief;
    private String detail;
    private String field;
    private List<Choice> choices = new ArrayList<Choice>();
    
    public static Question newShortText (String field, String brief, String detail) {
        Question q = new Question();
        q.type = TYPE_SHORT_TEXT;
        q.field = field;
        q.brief = brief;
        q.detail = detail;
        return q;
    }
    
    public static Question newPassword (String field, String brief, String detail) {
        Question q = new Question();
        q.type = TYPE_PASSWORD;
        q.field = field;
        q.brief = brief;
        q.detail = detail;
        return q;
    }

    public static Question newLongText (String field, String brief, String detail) {
        Question q = new Question();
        q.type = TYPE_LONG_TEXT;
        q.field = field;
        q.brief = brief;
        q.detail = detail;
        return q;
    }

    public static Question newSingleChoice (String field, String brief, String detail) {
        Question q = new Question();
        q.type = TYPE_SINGLE_CHOICE;
        q.field = field;
        q.brief = brief;
        q.detail = detail;
        return q;
    }
    
    public static Question newMultiChoice (String brief, String detail) {
        Question q = new Question();
        q.type = TYPE_MULTI_CHOICE;
        q.brief = brief;
        q.detail = detail;
        return q;        
    }
    
    public static Question newDropList (String field, String brief, String detail) {
        Question q = new Question();
        q.type = TYPE_DROPLIST;
        q.field = field;
        q.brief = brief;
        q.detail = detail;
        return q;        
    }
    
    public static Question newUserDropList (String field, String brief, String detail) {
        Question q = new Question();
        q.type = TYPE_USER_DROPLIST;
        q.field = field;
        q.brief = brief;
        q.detail = detail;
        return q;        
    }
    
    public void addChoice (String choice, Object value) {
        Choice c = new Choice(field, choice, value.toString(), false);
        choices.add(c);
    }

    public void addOtherChoice (String choice, Object value) {
        Choice c = new Choice(field, choice, value.toString(), true);
        choices.add(c);
    }
    
    public void addChoice (String field, String choice, Object value) {
        Choice c = new Choice(field, choice, value.toString(), false);
        choices.add(c);
    }
    
    public void addOtherChoice (String field, String choice, Object value) {
        Choice c = new Choice(field, choice, value.toString(), true);
        choices.add(c);
    }
 
    public int getType () {
        return type;
    }
    
    public String getBrief () {
        return brief;
    }
    
    public String getDetail () { 
        return detail;
    }
    
    public String getField () {
        return field;
    }
    
    public List<Choice> getChoices () {
        return Collections.unmodifiableList(choices);
    }
    
}
