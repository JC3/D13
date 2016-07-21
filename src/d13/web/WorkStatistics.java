package d13.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import d13.dao.ActivityLogEntry;
import d13.dao.Cell;
import d13.dao.CellActivityLogEntry;
import d13.dao.Comment;
import d13.dao.Invite;
import d13.dao.User;

public class WorkStatistics {
    
    private static enum EventType {
        REGISTERED,
        SENT_INVITE,
        REVIEWED_USER,
        APPROVED_USER,
        REJECTED_USER,
        FINALIZED_APPROVAL,
        FINALIZED_REJECTION,
        MISC_ACTION
    }
    
    private static class Event implements Comparable<Event> {
        
        final EventType type;
        final User by;
        final User to;
        final DateTime when;
        long tdeltaBy = -1;
        long tdeltaTo = -1;
        
        Event (EventType type, User by, User to, DateTime when) {
            this.type = type;
            this.by = by;
            this.to = to;
            this.when = when;
        }
        
        @Override public int compareTo (Event e) {
            return when.compareTo(e.when);
        }
        
        @Override public String toString () {
            return String.format("%s - %s - %s - %s - %.1f", DefaultDataConverter.objectAsString(when, true), type, by, to, getDeltaTime() / 1000.0);
        }
        
        public long getDeltaTime () {
            if (tdeltaBy < 0)
                return tdeltaTo;
            else if (tdeltaTo < 0)
                return tdeltaBy;
            else
                return Math.min(tdeltaTo, tdeltaBy);
        }
        
    }

    public static class UserStatistics {
        
        private final User user;
        int invited;
        int reviewed;
        int approved;
        int rejected;
        int approvedFinal;
        int rejectedFinal;
        int raOverlap;
        int rfOverlap;
        int afOverlap;
        int rafOverlap;
        int anyOverlap;
        double medianResponseTime; // registration -> review
        double medianReviewTime; // registration or previous action -> review
        double medianAdmitTime; // review or previous action -> admit
        double medianFinalizeTime; // admit or previous action -> finalize
        double meanResponseTime; // registration -> review
        double meanReviewTime; // registration or previous action -> review
        double meanAdmitTime; // review or previous action -> admit
        double meanFinalizeTime; // admit or previous action -> finalize
        
        UserStatistics (User user) { this.user = user; }
        public User getUser () { return user; }        
        public int getInvited () { return invited; }
        public int getReviewed () { return reviewed; }
        public int getApproved () { return approved; }
        public int getRejected () { return rejected; }
        public int getAdmitted () { return approved + rejected; }
        public int getFinalApproved () { return approvedFinal; }
        public int getFinalRejected () { return rejectedFinal; }
        public int getFinalized () { return approvedFinal + rejectedFinal; }
        public int getActions () { return reviewed + approved + rejected + approvedFinal + rejectedFinal; }
        public DateTime getLastLogin () { return user.getLastLogin(); }
        public long getUserId () { return user.getUserId(); }
        public String getEmail () { return user.getEmail(); }
        public String getName () { return user.getRealName(); }
        public String getRoleDisplay () { return user.getRoleDisplay(); }
        public int getReviewAdmitOverlap () { return raOverlap; }
        public int getReviewFinalizeOverlap () { return rfOverlap; }
        public int getAdmitFinalizeOverlap () { return afOverlap; }
        public int getCompleteOverlap () { return rafOverlap; }
        public int getAnyOverlap () { return anyOverlap; }
        public double getMedianResponseTime () { return medianResponseTime; }
        public double getMedianReviewTime () { return medianReviewTime; }
        public double getMedianAdmitTime () { return medianAdmitTime; }
        public double getMedianFinalizeTime () { return medianFinalizeTime; }
        public double getMeanResponseTime () { return meanResponseTime; }
        public double getMeanReviewTime () { return meanReviewTime; }
        public double getMeanAdmitTime () { return meanAdmitTime; }
        public double getMeanFinalizeTime () { return meanFinalizeTime; }
        @Override
        public String toString() {
            return String
                    .format("UserStatistics [name=%s, invited=%s, reviewed=%s, approved=%s, rejected=%s, approvedFinal=%s, rejectedFinal=%s, raOverlap=%s, rfOverlap=%s, afOverlap=%s, rafOverlap=%s, anyOverlap=%s, medianResponseTime=%s/%s, medianReviewTime=%s/%s, medianAdmitTime=%s/%s, medianFinalizeTime=%s/%s]",
                            getName(), invited, reviewed, approved, rejected,
                            approvedFinal, rejectedFinal, raOverlap, rfOverlap,
                            afOverlap, rafOverlap, anyOverlap,
                            medianResponseTime, meanResponseTime, medianReviewTime, meanReviewTime,
                            medianAdmitTime, meanAdmitTime, medianFinalizeTime, meanFinalizeTime);
        }
        
    }
    
    private List<UserStatistics> userStatistics;
    
    private static boolean isTeamMember (User u) {
        
        return u.getRole().canInviteUsers() ||
                u.getRole().canReviewUsers() ||
                u.getRole().canAdmitUsers() ||
                u.getRole().canFinalizeUsers();
        
    }
    
    public WorkStatistics (User viewer) {
        
        if (viewer == null)
            return;
        
        if (!isTeamMember(viewer) &&
            !viewer.getRole().canViewAdminData())
            return;
        
        userStatistics = new ArrayList<UserStatistics>();
   
        List<Event> events = buildEventList();
        
        //for (Event e : events)
        //    System.out.println(e);
        
        for (User user : User.findSpecial())
            if (isTeamMember(user))
                userStatistics.add(tabUserStatistics(user, events));
        
        //for (UserStatistics us : userStatistics)
        //    System.out.println(us);
        
    }
    
    private static <T> int countUnion (Set<T> a, Set<T> b) {
        Set<T> s = new HashSet<T>(a);
        s.retainAll(b);
        return s.size();
    }

    private static <T> int countUnion (Set<T> a, Set<T> b, Set<T> c) {
        Set<T> s = new HashSet<T>(a);
        s.retainAll(b);
        s.retainAll(c);
        return s.size();
    }
    
    private static <T> int countMultiple (Set<T> a, Set<T> b, Set<T> c) {
        Set<T> ab = new HashSet<T>(a); ab.retainAll(b);
        Set<T> bc = new HashSet<T>(b); bc.retainAll(c);
        Set<T> ac = new HashSet<T>(a); ac.retainAll(c);
        ab.addAll(bc);
        ab.addAll(ac);
        return ab.size();
    }
    
    private static double median (List<Long> values) {
        if (values.isEmpty())
            return -1;
        else if (values.size() % 2 == 0)
            return (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2000.0;
        else
            return values.get(values.size() / 2) / 1000.0;
    }
    
    private static double mean (List<Long> values) {
        double sum = 0;
        int count = 0;
        for (Long v : values) {
            if (v != null) {
                sum += v / 1000.0;
                ++ count;
            }
        }
        sum = (count == 0 ? -1 : (sum / count));
        return sum;
    }

    private static double meanno (List<Long> values, double removepct) {
        List<Long> copy = new ArrayList<Long>(values);
        int rend = (int)(copy.size() * removepct * 0.5 + 0.5);
        for (int n = 0; n < rend && n < copy.size(); ++ n)
            copy.set(n, null);
        for (int n = copy.size() - rend; n < copy.size(); ++ n)
            if (n >= 0) copy.set(n, null);
        return mean(copy);
    }
    
    private static UserStatistics tabUserStatistics (User u, List<Event> evs) {
        
        UserStatistics s = new UserStatistics(u);
        Set<Long> reviewed = new HashSet<Long>();
        Set<Long> admitted = new HashSet<Long>();
        Set<Long> finalized = new HashSet<Long>();
        List<Long> reviewTimes = new ArrayList<Long>();
        List<Long> admitTimes = new ArrayList<Long>();
        List<Long> finalizeTimes = new ArrayList<Long>();
        List<Long> responseTimes = new ArrayList<Long>();
        
        for (Event e : evs) {
    
            if (e.by == null || e.by.getUserId() != u.getUserId())
                continue;
            
            long toid = (e.to == null ? -1 : e.to.getUserId());
            long tdelta = e.getDeltaTime();
            
            switch (e.type) {
            case APPROVED_USER: 
                ++ s.approved;
                admitted.add(toid);
                if (tdelta >= 0) admitTimes.add(tdelta);
                break;
            case REJECTED_USER: 
                ++ s.rejected;
                admitted.add(toid);
                if (tdelta >= 0) admitTimes.add(tdelta);
                break;
            case FINALIZED_APPROVAL:
                ++ s.approvedFinal;
                finalized.add(toid);
                if (tdelta >= 0) finalizeTimes.add(tdelta);
                break;
            case FINALIZED_REJECTION: 
                ++ s.rejectedFinal; 
                finalized.add(toid);
                if (tdelta >= 0) finalizeTimes.add(tdelta);
                break;
            case REVIEWED_USER: 
                ++ s.reviewed; 
                reviewed.add(toid);
                if (tdelta >= 0) reviewTimes.add(tdelta);
                if (e.to.getRegisteredOn() != null) {
                    long responseTime = e.when.getMillis() - e.to.getRegisteredOn().getMillis();
                    responseTimes.add(responseTime);
                }
                break;
            case SENT_INVITE: 
                ++ s.invited; 
                break;
            }
            
        }
        
        s.raOverlap = countUnion(reviewed, admitted);
        s.afOverlap = countUnion(admitted, finalized);
        s.rfOverlap = countUnion(reviewed, finalized);
        s.rafOverlap = countUnion(reviewed, admitted, finalized);
        s.anyOverlap = countMultiple(reviewed, admitted, finalized);
        
        Collections.sort(admitTimes);
        Collections.sort(finalizeTimes);
        Collections.sort(responseTimes);
        Collections.sort(reviewTimes);

        s.medianAdmitTime = median(admitTimes);
        s.medianFinalizeTime = median(finalizeTimes);
        s.medianResponseTime = median(responseTimes);
        s.medianReviewTime = median(reviewTimes);

        s.meanAdmitTime = meanno(admitTimes, 0.5);
        s.meanFinalizeTime = meanno(finalizeTimes, 0.5);
        s.meanResponseTime = mean(responseTimes);
        s.meanReviewTime = meanno(reviewTimes, 0.5);

        return s;
        
    }
    
    public List<UserStatistics> getUserStatistics () {
        
        return userStatistics;
        
    }
    
    private static List<Event> buildEventList () {
        
        List<Event> events = new ArrayList<Event>();
        
        // ---- INVITES -------------------------------------------------------
        
        for (Invite i : Invite.findAll()) {
            events.add(new Event(EventType.SENT_INVITE, i.getCreatedBy(), null, i.getCreatedOn()));
            if (i.getCancelledBy() != null)
                events.add(new Event(EventType.MISC_ACTION, i.getCancelledBy(), null, i.getCancelledOn()));
        }
        
        // ---- REG, REVIEWS, ADMISSIONS, FINALIZATIONS------------------------
        
        for (User u : User.findAll()) {
            for (ActivityLogEntry e : u.getActivityLog()) {
                String text = e.getDescription().toLowerCase();
                EventType type;
                if (text.contains("needs review to registered"))
                    type = EventType.REVIEWED_USER;
                else if (text.contains("registered to approval pending"))
                    type = EventType.APPROVED_USER;
                else if (text.contains("registered to rejection pending"))
                    type = EventType.REJECTED_USER;
                else if (text.contains("rejection pending to rejected"))
                    type = EventType.FINALIZED_REJECTION;
                else if (text.contains("approval pending to approved"))
                    type = EventType.FINALIZED_APPROVAL;
                else
                    type = EventType.MISC_ACTION;
                events.add(new Event(type, e.getByWho(), e.getToWho(), e.getTime()));
            }
            if (u.getLastLogin() != null)
                events.add(new Event(EventType.MISC_ACTION, u, null, u.getLastLogin()));
            for (Comment c : u.getComments())
                if (!c.getComment().startsWith("[Invite Comment]"))
                    events.add(new Event(EventType.MISC_ACTION, c.getAuthor(), null, c.getTime()));
            if (u.getRegisteredOn() != null)
                events.add(new Event(EventType.REGISTERED, u, u, u.getRegisteredOn()));
        }
        
        // ---- MORE MISC ACTIONS ---------------------------------------------
        
        for (Cell c : Cell.findAll())
            for (CellActivityLogEntry e : c.getActivityLog())
                events.add(new Event(EventType.MISC_ACTION, e.getByWho(), null, e.getTime()));
 
        // ---- TIMING --------------------------------------------------------
        
        Collections.sort(events);

        Map<Long,Event> lastBy = new HashMap<Long,Event>();
        Map<Long,Event> lastTo = new HashMap<Long,Event>();
        
        for (Event e : events) {
            if (e.by != null) {
                Event last = lastBy.get(e.by.getUserId());
                if (last != null)
                    e.tdeltaBy = e.when.getMillis() - last.when.getMillis();
                lastBy.put(e.by.getUserId(), e);
            }
            if (e.to != null) {
                Event last = lastTo.get(e.to.getUserId());
                if (last != null)
                    e.tdeltaTo = e.when.getMillis() - last.when.getMillis();
                lastTo.put(e.to.getUserId(), e);
            }
        }
        
        return events;
        
    }
    
}
