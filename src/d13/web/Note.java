package d13.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import d13.ThisYear;
import d13.dao.ActivityLogEntry;
import d13.dao.Cell;
import d13.dao.CellActivityLogEntry;
import d13.dao.Comment;
import d13.dao.DueCalculator.Tier;
import d13.dao.Invite;
import d13.dao.User;

public abstract class Note implements Comparable<Note> {

    private final Type type;
    
    public abstract DateTime getTime ();
    public final String getAuthorEmail () { return getAuthor() == null ? "" : getAuthor().getEmail(); }
    public abstract User getAuthor ();
    public User getTargetUser () { return null; }
    public Cell getTargetCell () { return null; }
    public abstract String getText ();
    public boolean isComment () { return false; }
    public boolean isCell () { return false; }
    public final Type getType () { return type; }
    
    protected Note (Type t) {
        this.type = t;
    }
    
    public final String getTargetName () {
        if (getTargetUser() != null)
            return getTargetUser().getRealName();
        else if (getTargetCell() != null)
            return getTargetCell().getFullName();
        else
            return null;
    }
    
    @Override public final int compareTo (Note n) { 
        return (n == null) ? -1 : getTime().compareTo(n.getTime()); 
    }
    
    public static Note from (ActivityLogEntry e) {
        return (e == null) ? null : new ActivityLogNote(e);
    }
    
    public static Note from (Comment c) {
        return (c == null) ? null : new CommentNote(c);
    }
    
    public static Note from (CellActivityLogEntry e) {
        return (e == null) ? null : new CellActivityLogNote(e);
    }
    
    public static Note from (Invite i) {
        return (i == null) ? null : new InviteNote(i);
    }
    
    public static Note from (Tier t) {
        return (t == null || t.getEnd() == null) ? null : new TierEndNote(t);
    }
    
    public static Note fromPersonalDues (User u) {
        if (u != null && u.getPersonalPaidDate() != null)
            return new PersonalDuePaymentNote(u);
        else
            return null;
    }
    
    public static Note fromRVDues (User u) {
        if (u != null && u.getRvPaidDate() != null)
            return new RVDuePaymentNote(u);
        else
            return null;
    }
    
    public static Note fromRegistration (User u) {
        if (u != null && u.getRegisteredOn() != null)
            return new RegistrationNote(u);
        else
            return null;
    }
    
    private static void addIfNotNull (List<Note> notes, Note note) {
        if (note != null)
            notes.add(note);
    }
    
    public static List<Note> all (User viewer, User u) {
        List<Note> notes = new ArrayList<Note>();
        if (viewer != null && u != null && u.isViewableBy2(viewer)) {
            if (viewer.getRole().canViewLogs())
                for (ActivityLogEntry e : u.getActivityLog())
                    notes.add(from(e));
            if (viewer.getRole().canViewComments())
                for (Comment c : u.getComments())
                    notes.add(from(c));
            addIfNotNull(notes, fromPersonalDues(u));
            addIfNotNull(notes, fromRVDues(u));
            addIfNotNull(notes, fromRegistration(u));
        }
        return notes;
    }
    
    public static List<Note> all (User viewer, Cell c) {
        List<Note> notes = new ArrayList<Note>();
        if (viewer != null && c != null && viewer.getRole().canViewFullCells())
            for (CellActivityLogEntry e : c.getActivityLog())
                notes.add(from(e));
        return notes;
    }
    
    public static List<Note> allUsers (User viewer, Collection<User> users) {
        List<Note> notes = new ArrayList<Note>();
        for (User u : users)
            notes.addAll(all(viewer, u));
        return notes;
    }
    
    public static List<Note> allCells (User viewer, Collection<Cell> cells) {
        List<Note> notes = new ArrayList<Note>();
        for (Cell c : cells)
            notes.addAll(all(viewer, c));
        return notes;
    }
    
    public static List<Note> allInvites (User viewer, Collection<Invite> invites) {
        List<Note> notes = new ArrayList<Note>();
        if (viewer != null && viewer.getRole().canViewInvites())
            for (Invite i : invites)
                notes.add(from(i));
        return notes;
    }

    private static Note nullIfFuture (Note n) {
        if (n == null || n.getTime().isAfterNow())
            return null;
        else
            return n;
    }
    
    public static List<Note> allTiers () {
        List<Note> notes = new ArrayList<Note>();
        List<Tier> p = new ArrayList<Tier>(), r = new ArrayList<Tier>();
        ThisYear.setupPersonalTiers(p);
        ThisYear.setupRVTiers(r);
        for (Tier t : p)
            addIfNotNull(notes, nullIfFuture(from(t)));
        for (Tier t : r)
            addIfNotNull(notes, nullIfFuture(from(t)));
        return notes;
    }
    
    public static enum Type {
        ACTIVITY("activity"),
        COMMENT("comment"),
        CELL("cell"),
        PERSONAL_DUE("personaldue"),
        RV_DUE("rvdue"),
        REGISTRATION("registration"),
        INVITE("invite"),
        TIER_END("tier");
        private final String name;
        private Type (String name) { this.name = name; }
        public String getName () { return name; }
    }
    
    private static class ActivityLogNote extends Note {
        private final ActivityLogEntry e;
        ActivityLogNote (ActivityLogEntry e) { super(Type.ACTIVITY); this.e = e; }
        @Override public DateTime getTime () { return e.getTime(); }
        @Override public User getAuthor () { return e.getByWho(); }
        @Override public String getText () { return e.getDescription(); }
        @Override public User getTargetUser () { return e.getToWho(); }
    }

    private static class CommentNote extends Note {
        private final Comment e;
        CommentNote (Comment e) { super(Type.COMMENT); this.e = e; }
        @Override public DateTime getTime () { return e.getTime(); }
        @Override public User getAuthor () { return e.getAuthor(); }
        @Override public String getText () { return e.getComment(); }
        @Override public boolean isComment () { return true; }
        @Override public User getTargetUser () { return e.getSubject(); }
    }

    private static class CellActivityLogNote extends Note {
        private final CellActivityLogEntry e;
        CellActivityLogNote (CellActivityLogEntry e) { super(Type.CELL); this.e = e; }
        @Override public DateTime getTime () { return e.getTime(); }
        @Override public User getAuthor () { return e.getByWho(); }
        @Override public String getText () { return e.getDescription(); }
        @Override public Cell getTargetCell () { return e.getToCell(); }
        @Override public boolean isCell () { return true; }
    }
    
    private static abstract class UserNote extends Note {
        private final User u;
        UserNote (User u, Type t) { super(t); this.u = u; }
        @Override public User getAuthor () { return null; }
        @Override public User getTargetUser () { return u; }
    }
    
    private static class PersonalDuePaymentNote extends UserNote {
        PersonalDuePaymentNote (User u) { super(u, Type.PERSONAL_DUE); }
        @Override public DateTime getTime () { return getTargetUser().getPersonalPaidDate(); }
        @Override public String getText () { return "Personal dues paid."; }
    }
    
    private static class RVDuePaymentNote extends UserNote {
        RVDuePaymentNote (User u) { super(u, Type.RV_DUE); }
        @Override public DateTime getTime () { return getTargetUser().getRvPaidDate(); }
        @Override public String getText () { return "RV dues paid."; }
    }
    
    private static class RegistrationNote extends UserNote {
        RegistrationNote (User u) { super(u, Type.REGISTRATION); }
        @Override public DateTime getTime () { return getTargetUser().getRegisteredOn(); }
        @Override public String getText () { return "Registration form complete."; }
    }
    
    private static class InviteNote extends Note {
        private final Invite i;
        InviteNote (Invite i) { super(Type.INVITE); this.i = i; }
        @Override public DateTime getTime () { return i.getCreatedOn(); }
        @Override public User getAuthor () { return i.getCreatedBy(); }
        @Override public User getTargetUser () { return i.getResolvedBy(); }
        @Override public String getText () { 
            return String.format("%s <%s> (%s)%s%s",
                    naIfNull(i.getInviteeName()),
                    naIfNull(i.getInviteeEmail()),
                    naIfNull(i.getInviteCode()),
                    i.getExpiresOn() == null ? "" : (", Expires: " + i.getExpiresOn().toString(DateTimeFormat.shortDateTime())),
                    StringUtils.trimToNull(i.getComment()) == null ? "" : (" -- "  + i.getComment()));
        }
        private static String naIfNull (String s) { return s == null ? "n/a" : s; }
    }
    
    private static class TierEndNote extends Note {
        private final Tier t;
        TierEndNote (Tier t) { super(Type.TIER_END); this.t = t; }
        @Override public DateTime getTime () { return t.getEnd(); }
        @Override public User getAuthor () { return null; }
        @Override public String getText () { return t.getName(); }
    }

    public static final Comparator<Note> ASCENDING = new Comparator<Note> () {
        @Override public int compare (Note a, Note b) {
            return a.compareTo(b);
        }
    };
    
    public static final Comparator<Note> DESCENDING = new Comparator<Note> () {
        @Override public int compare (Note a, Note b) {
            return b.compareTo(a);
        }
    };
    
}
