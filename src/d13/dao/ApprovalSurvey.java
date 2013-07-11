package d13.dao;

import org.joda.time.DateTime;

public class ApprovalSurvey {

    private long aformId;
    private User user;
    private DateTime completionTime;
    
    ApprovalSurvey () {
    }
    
    ApprovalSurvey (User user) {
        this.user = user;
    }
    
    public boolean isCompleted () {
        return completionTime != null;
    }
    
}
