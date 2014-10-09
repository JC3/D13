package d13.apps.stats;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import d13.dao.ActivityLogEntry;
import d13.dao.RVSelection;
import d13.dao.User;
import d13.dao.UserState;
import d13.util.HibernateUtil;

public class Approvals {

    static class Approval {
        
        final DateTime when;
        final User     byWho;
        final User     toWho;
        final int      hash;
        
        public Approval (ActivityLogEntry e) {
            when = e.getTime();
            byWho = e.getByWho();
            toWho = e.getToWho();
            hash = (int)(byWho.getUserId() * 1237 + toWho.getUserId());
        }
        
        @Override public boolean equals (Object o) {
            if (this == o)
                return true;
            else if (o == null || !(o instanceof Approval))
                return false;
            Approval a = (Approval)o;
            if (hashCode() != a.hashCode())
                return false;
            else
                return (a.byWho.getUserId() == byWho.getUserId() && a.toWho.getUserId() == toWho.getUserId());
        }
        
        @Override public int hashCode () {       
            return hash;
        }
        
    }
    
    static class ApprovalDateComparator implements Comparator<Approval> {

        @Override public int compare(Approval a, Approval b) {
            return a.when.compareTo(b.when);
        }
        
    }
    
    static List<Approval> createApprovalList () {
 
        /* 
        List<ActivityLogEntry> entries = ActivityLogEntry.findAll();
        Set<Approval> approvals = new HashSet<Approval>();

        for (ActivityLogEntry entry : entries) {
            if (entry.getDescription().endsWith("to Approved."))
                approvals.add(new Approval(entry));
        }
        
        List<Approval> alist = new ArrayList<Approval>(approvals);        
        Collections.sort(alist, new ApprovalDateComparator());
        
        System.out.println("Duplicates: " + (approvals.size() - alist.size()));
        
        return alist;
        */
        
        List<User> users = User.findAll();
        List<Approval> approvals = new ArrayList<Approval>();
        
        for (User user : users) {
            if (user.getState() != UserState.APPROVED)
                continue;
            //System.err.println(user.getUserId());
            List<ActivityLogEntry> log = new LinkedList<ActivityLogEntry>(user.getActivityLog());
            Collections.reverse(log);
            for (ActivityLogEntry e : log) { 
                if (e.getDescription().endsWith("to Approved.") && e.getToWho().getUserId() != 10) {
                    approvals.add(new Approval(e));
                    break;
                }
            }
        }
        
        Collections.sort(approvals, new ApprovalDateComparator());
        
        return approvals;
        
    }
    
    static class Count {
        int countTotal = 0;
        int countRV = 0;
        int countBMVirgin = 0;
        int countDisVirgin = 0;
        void incrementTotal () { ++ countTotal; }
        void incrementRV () { ++ countRV; }
        void incrementBMVirgin () { ++ countBMVirgin; }
        void incrementDisVirgin () { ++ countDisVirgin; }
    }
    
    public static void main (String[] args) throws Exception {

        System.err.println("Connecting");
        HibernateUtil.openSession();
        HibernateUtil.beginTransaction();
        List<Approval> approvals = createApprovalList();

        PrintStream out = System.out;

        out.print("date,epoch");
        Map<Long,Count> approvecounts = new LinkedHashMap<Long,Count>();
        for (Approval a : approvals) {
            if (!approvecounts.containsKey(a.byWho.getUserId())) {
                approvecounts.put(a.byWho.getUserId(), new Count());
                out.print("," + a.byWho.getRealName() + " (Total)");
                out.print("," + a.byWho.getRealName() + " (RV)");
                out.print("," + a.byWho.getRealName() + " (BM Virgin)");
                out.print("," + a.byWho.getRealName() + " (Disorient Virgin)");
            }
        }
        out.println();
  
        for (Approval a : approvals) {
            
            Count count = approvecounts.get(a.byWho.getUserId());
            count.incrementTotal();
            
            User to = a.toWho;
            if (to.getRegistration().isBmVirgin())
                count.incrementBMVirgin();
            if (to.getRegistration().isDisorientVirgin())
                count.incrementDisVirgin();
            if (to.getRegistration().getRvType() == RVSelection.RESPONSIBLE)
                count.incrementRV();
            
            out.print(a.when.toString() + "," + a.when.getMillis());
            for (Long id : approvecounts.keySet()) {
                count = approvecounts.get(id);
                out.print("," + count.countTotal);
                out.print("," + count.countRV);
                out.print("," + count.countBMVirgin);
                out.print("," + count.countDisVirgin);
            }
            out.println();
            
        }
        
        HibernateUtil.commitTransaction();
        
    }

}
