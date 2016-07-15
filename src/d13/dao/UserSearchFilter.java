package d13.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import d13.util.HibernateUtil;

public class UserSearchFilter {

    /*
    <li><a href="view_data.jsp?qf=1">Only users that need registration applications reviewed.</a>
    <li><a href="view_data.jsp?qf=2">Only users that need to be approved or rejected.</a>
    <li><a href="view_data.jsp?qf=3">Only users that need to be finalized.</a>
    <li><a href="view_data.jsp?qf=4">Only users that have been approved.</a>
    <li><a href="view_data.jsp?qf=5">Only users that own RVs.</a>
    <li><a href="view_data.jsp?qf=6">Only users that need to pay their dues.</a>
    <li><a href="view_data.jsp?qf=7">Only users that need to complete their approval surveys.</a>*/
   
    public static final int QUICK_ALL = 0;
    public static final int QUICK_NEED_REVIEW = 1;
    public static final int QUICK_NEED_APPROVED_OR_REJECTED = 2;
    public static final int QUICK_NEED_FINALIZED = 3;
    public static final int QUICK_APPROVED = 4;
    public static final int QUICK_OWN_RVS = 5;
    public static final int QUICK_UNPAID = 6;
    public static final int QUICK_NEED_SURVEY = 7;
    public static final int QUICK_NEED_CELLS = 8;
    public static final int QUICK_NEED_CELLS_NOT_APPROVED = 9;
    public static final int QUICK_NEED_CELLS_APPROVED = 10;
    
    public static List<User> quickFilter (int filter) throws IllegalArgumentException {
        
        String querystr;
        
        switch (filter) {
        case QUICK_ALL:
            return User.findAll();
        case QUICK_NEED_REVIEW:
            querystr = ("where user.state = " + UserState.NEEDS_REVIEW.toDBId());
            break;
        case QUICK_NEED_APPROVED_OR_REJECTED:
            querystr = ("where user.state = " + UserState.REGISTERED.toDBId());
            break;
        case QUICK_NEED_FINALIZED:
            querystr = ("where user.state = " + UserState.APPROVE_PENDING.toDBId() + " or user.state = " + UserState.REJECT_PENDING.toDBId());
            break;
        case QUICK_APPROVED:
            querystr = ("where user.state = " + UserState.APPROVED.toDBId());
            break;
        case QUICK_OWN_RVS:
            querystr = ("where user.registration.rvType = " + RVSelection.RESPONSIBLE.toDBId());
            break;
        case QUICK_UNPAID:
            /*
            public boolean isPaid () {
                return isPersonalPaid() && isRvPaid();
            }
            
            public boolean isPersonalPaid () {
                return getPersonalDueItem() != null && (!getPersonalDueItem().isActive() || getPersonalDueItem().isPaid());
            }
            
            public boolean isRvPaid () {
                return getRvDueItem() != null && (!getRvDueItem().isActive() || getRvDueItem().isPaid());
            }
        return paidInvoice != null || customAmount == 0;
        
        personal_paid and rv_paid
        (!personal.active || personal.paid) and (!rv.active || rv.paid)
        (!personal.active || (personal.paidInvoice or !customAmount)) and (!rv.active || (rv.paidInvoice or !customAmount))
        (personal.active=0 or personal.paidInvoice!=null or personal.customAmount=0) and (rv.active=0 or rv.paidInvoice!=null or rv.customAmount=0)
        
            */
            querystr = 
                "where (user.state = " + UserState.APPROVED.toDBId() + ") and not " +
                "((user.personalDue.active=false or user.personalDue.paidInvoice is not null or user.personalDue.customAmount=0) and " +
                "(user.rvDue.active=false or user.rvDue.paidInvoice is not null or user.rvDue.customAmount=0))";
            break;
        case QUICK_NEED_SURVEY:
            querystr = "left outer join user.approval a where user.state = " + UserState.APPROVED.toDBId() + " and a.completionTime is null";
            break;
        case QUICK_NEED_CELLS:
            querystr = "where user.state != " + UserState.NEW_USER.toDBId() + " and user.cells is empty or user.cells.size < " + User.LOW_CELL_THRESHOLD;
            break;
        case QUICK_NEED_CELLS_NOT_APPROVED:
            querystr = "where (user.state = " + UserState.NEEDS_REVIEW.toDBId() + " or " +
                              "user.state = " + UserState.REGISTERED.toDBId() + ") " + 
                              "and user.cells is empty";
            break;
        case QUICK_NEED_CELLS_APPROVED:
            querystr = "where (user.state = " + UserState.APPROVED.toDBId() + " or " +
                    "user.state = " + UserState.APPROVE_PENDING.toDBId() + ") " + 
                    "and user.cells is empty";
            break;
        default:
            throw new IllegalArgumentException("Invalid quick filter index " + filter);
        }
        
        //from User as user where user.receiveNotifications = true and user.role.admitUsers = true
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createQuery("select user from User as user " + querystr)
                .list();
        
        return users;
        
    }
    
    public static List<User> search (String text) {
 
        Criterion email = Restrictions.ilike("email", text, MatchMode.ANYWHERE);
        Criterion real = Restrictions.ilike("realName", text, MatchMode.ANYWHERE);
        Criterion playa = Restrictions.ilike("playaName", text, MatchMode.ANYWHERE);
        
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>)HibernateUtil.getCurrentSession()
                .createCriteria(User.class)
                .add(Restrictions.or(email, Restrictions.or(playa, real)))
                .list();
   
        return users;
        
    }
    
}
