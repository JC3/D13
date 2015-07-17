package d13.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import d13.util.HibernateUtil;
import d13.util.Util;

/*inviteId bigint(2) not null
 * `inviteCode` varchar(8) NOT NULL,
  `inviteeEmail` varchar(254) NOT NULL,
  `createdBy` bigint(20) NOT NULL COMMENT 'user',
  `createdOn` bigint(20) NOT NULL COMMENT 'date',
  `expiresOn` bigint(20) DEFAULT NULL COMMENT 'date',
  `acceptedOn` bigint(20) DEFAULT NULL COMMENT 'date',
  `acceptedBy` bigint(20) DEFAULT NULL COMMENT 'user',
  `comment` longtext, */

public class Invite {
    
    public static final int STATUS_CANCELLED = -1;
    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_REJECTED = 2;

    private long inviteId;
    private String inviteCode;
    private String inviteeEmail;
    private String inviteeName;
    private User createdBy;
    private DateTime createdOn;
    private User cancelledBy;
    private DateTime cancelledOn;
    private DateTime expiresOn;
    private User resolvedBy;
    private DateTime resolvedOn;
    private int status;
    private String comment;
    
    public static void addInvite (Invite invite) {
        
        if (invite == null)
            throw new IllegalArgumentException("Null invite specified.");
        //if (findByEmail(user.getEmail()) != null)
        //    throw new IllegalArgumentException("This email address is already in use.");

        // todo: consider doing this in a query instead of grabbing the whole list
        for (Invite existing : findAll()) {
            if (!existing.getInviteeEmail().equalsIgnoreCase(invite.getInviteeEmail()))
                continue;
            // rejected / expired / cancelled invites can be reissued. otherwise, no go.
            if (existing.isActive())
                throw new IllegalArgumentException("An active invite for this email address already exists.");
            if (existing.getStatus() == STATUS_ACCEPTED)
                throw new IllegalArgumentException("An invite has already been accepted by this email address.");
            // check the code too, but should be ok
            if (existing.getInviteCode().equals(invite.getInviteCode()))
                throw new IllegalArgumentException("Unlucky you, the random code already exists. Try one more time.");
        }
        
        try {
            HibernateUtil.getCurrentSession().save(invite);
        } catch (HibernateException x) {
            throw new IllegalArgumentException(x);
        }
        
    }

    Invite () {
    }
    
    // bug: there is a chance that generated code will not be unique. db insert will fail. leaving unfixed for now.
    public Invite (String inviteeEmail, String inviteeName, User creator, DateTime expiresOn) {
        this.inviteCode = Util.randomString(8);
        this.inviteeEmail = Util.requireEmail(inviteeEmail, "A valid email address");
        this.inviteeName = inviteeName;
        this.createdBy = creator;
        this.createdOn = DateTime.now();
        this.cancelledBy = null;
        this.cancelledOn = null;
        this.expiresOn = expiresOn;
        this.resolvedBy = null;
        this.resolvedOn = null;
        this.status = STATUS_ACTIVE;
        this.comment = null;
    }

    public long getInviteId() {
        return inviteId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }
    
    public String getInviteeName () {
        return inviteeName;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public DateTime getExpiresOn() {
        return expiresOn;
    }
    
    public boolean isExpired () {
        return (expiresOn != null) && (DateTime.now().isAfter(expiresOn));
    }

    public User getCancelledBy () {
        return cancelledBy;
    }

    public DateTime getCancelledOn () {
        return cancelledOn;
    }

    public User getResolvedBy () {
        return resolvedBy;
    }

    public DateTime getResolvedOn () {
        return resolvedOn;
    }

    public int getStatus () {
        return status;
    }

    public DateTime getStatusChanged () {
        if (status == STATUS_ACCEPTED || status == STATUS_REJECTED)
            return resolvedOn;
        else if (status == STATUS_CANCELLED)
            return cancelledOn;
        else
            return createdOn;
    }
    
    public String getComment () {
        return comment;
    }    
    
    public boolean isActive () {
        return getStatus() == STATUS_ACTIVE && !isExpired(); 
    }
    
    private void checkStateForResolve (User acceptor) {
        if (acceptor == null) {
            throw new IllegalArgumentException("User must be specified.");
        } else if (getStatus() == STATUS_ACCEPTED) {
            if (acceptor.getUserId() == resolvedBy.getUserId())
                throw new IllegalArgumentException("You have already accepted this invite.");
            else
                throw new IllegalArgumentException("This invite code has already been used.");
        } else if (getStatus() == STATUS_REJECTED) {
            if (acceptor.getUserId() == resolvedBy.getUserId())
                throw new IllegalArgumentException("You have already rejected this invite.");
            else
                throw new IllegalArgumentException("This invite code has already been used.");
        } else if (getStatus() == STATUS_CANCELLED) {
            throw new IllegalArgumentException("This invite code has expired."); // pretend expired; don't let user know it was cancelled. avoid drama.
        } else if (getStatus() != STATUS_ACTIVE) {
            throw new IllegalArgumentException("Something is broken (sorry). Please let an administrator know.");
        } else if (isExpired()) {
            throw new IllegalArgumentException("This invite code has expired.");
        }
    }
    
    private void checkStateForCancel (User canceller) {
        if (canceller == null) {
            throw new IllegalArgumentException("User must be specified.");
        } else if (status == STATUS_ACCEPTED) {
            throw new IllegalArgumentException("This invite has already been accepted, it cannot be cancelled.");
        } else if (status == STATUS_REJECTED) {
            throw new IllegalArgumentException("This invite has already been rejected, it cannot be cancelled.");
        } else if (status == STATUS_CANCELLED) {
            throw new IllegalArgumentException("This invite has already been cancelled.");
        } else if (isExpired()) {
            throw new IllegalArgumentException("This invite has already expired.");
        }
    }
    
    public boolean isCancellable (User canceller) {
        try {
            checkStateForCancel(canceller);
            return true;
        } catch (Exception x) {
            return false;
        }
    }
    
    public void accept (User acceptor) {
        checkStateForResolve(acceptor);
        resolvedOn = DateTime.now();
        resolvedBy = acceptor;
        status = STATUS_ACCEPTED;
    }
    
    public void reject (User rejector) {
        checkStateForResolve(rejector);
        resolvedOn = DateTime.now();
        resolvedBy = rejector;
        status = STATUS_REJECTED;
    }
    
    public void cancel (User canceller) {
        checkStateForCancel(canceller);
        cancelledOn = DateTime.now();
        cancelledBy = canceller;
        status = STATUS_CANCELLED;
    }
    
    public void setComment (String comment) {
        this.comment = comment;
    }
    
    public static Invite findById (Long id) {
        return findById(id, HibernateUtil.getCurrentSession());
    }

    public static Invite findById (Long id, Session session) {
        Invite invite = (Invite)session.get(Invite.class, id);
        if (invite == null)
            throw new IllegalArgumentException("There is no invite with the specified ID.");
        return invite;
    }    
    
    public static Invite findByInviteCode (String inviteCode) {
        
        inviteCode = (inviteCode == null ? "" : inviteCode.trim());
        if (inviteCode.isEmpty())
            throw new IllegalArgumentException("Invite code must be specified.");
        
        Invite invite = (Invite)HibernateUtil.getCurrentSession()
                .createCriteria(Invite.class)
                .add(Restrictions.eq("inviteCode", inviteCode))
                .uniqueResult();
        
        return invite;

    }


    public static List<Invite> findAll () {
        @SuppressWarnings("unchecked")
        List<Invite> invites = (List<Invite>)HibernateUtil.getCurrentSession()
                .createCriteria(Invite.class)
                .list();
        return invites;
    }

}
