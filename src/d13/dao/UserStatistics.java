package d13.dao;

import java.util.List;

public class UserStatistics {

    public static class UserCount {
        public int total;
        public int disorientVirgins;
        public int bmVirgins;
        void count (User user) {
            if (user != null) {
                ++ total;
                if (user.getRegistration() != null) {
                    if (user.getRegistration().isDisorientVirgin())
                        ++ disorientVirgins;
                    if (user.getRegistration().isBmVirgin())
                        ++ bmVirgins;
                }
            }
        }
    }
    
    public static class Statistics {
        public final UserCount needReview = new UserCount();
        public final UserCount needAdmitted = new UserCount();
        public final UserCount needFinalized = new UserCount();
        public final UserCount needAction = new UserCount();
        public final UserCount registered = new UserCount();
        public final UserCount approved = new UserCount();
        //public final UserCount unpaid = new UserCount();
        public final UserCount rvsRegistered = new UserCount();
        public final UserCount rvsApproved = new UserCount();
    }

    public static Statistics calculateStatistics () {
        
        List<User> users = User.findAll();
        Statistics s = new Statistics();
        
        for (User u:users) {
            if (u.getState() == UserState.NEEDS_REVIEW)
                s.needReview.count(u);
            if (u.getState() == UserState.REGISTERED)
                s.needAdmitted.count(u);
            if (u.getState() == UserState.APPROVE_PENDING || u.getState() == UserState.REJECT_PENDING)
                s.needFinalized.count(u);
            if (u.getState() != UserState.APPROVED && u.getState() != UserState.REJECTED && u.getState() != UserState.NEW_USER)
                s.needAction.count(u);
            if (u.getState() != UserState.NEW_USER)
                s.registered.count(u);
            if (u.getState() == UserState.APPROVED)
                s.approved.count(u);
            //if (!u.isPaid())
            //    s.unpaid.count(u);
            if (u.getState() != UserState.NEW_USER && u.getRegistration() != null && u.getRegistration().getRvType() == RVSelection.RESPONSIBLE)
                s.rvsRegistered.count(u);
            if (u.getState() == UserState.APPROVED && u.getRegistration() != null && u.getRegistration().getRvType() == RVSelection.RESPONSIBLE)
                s.rvsApproved.count(u);
        }
        
        return s;
        
    }

}
