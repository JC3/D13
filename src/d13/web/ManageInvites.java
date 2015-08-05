package d13.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;

import d13.dao.Invite;
import d13.dao.QueuedEmail;
import d13.dao.RuntimeOptions;
import d13.dao.User;
import d13.util.Util;

public class ManageInvites {

    private boolean failed;
    private List<Invite> invites;
    private String errorMessage;
    private String submittedEmails;
    private String submittedExpires;
    private String submittedComment;
    private List<String> warnings;
    
    private static class InviteViewComparator implements Comparator<Invite> {

        @Override public int compare (Invite a, Invite b) {
            int c = a.getInviteeEmail().compareToIgnoreCase(b.getInviteeEmail());
            if (c == 0)
                c = a.getCreatedOn().compareTo(b.getCreatedOn());
            return c;
        }
        
    }
    
    public ManageInvites (PageContext context, SessionData session) {
        
        if (!session.isLoggedIn()) {
            failed = true;
            errorMessage = "Permission denied.";
            return; // permission denied
        }
        
        if (!RuntimeOptions.Global.isInviteOnly()) {
            failed = true;
            errorMessage = "System is not in invite mode.";
            return; // not in invite mode
        }

        User current = session.getUser();
        
        if (!current.getRole().canViewInvites()) {
            failed = true;
            errorMessage = "Permission denied.";
            return; // permission denied
        }

        ManageInvitesBean bean = new ManageInvitesBean();
        try {
            BeanUtils.populate(bean, context.getRequest().getParameterMap());
        } catch (Throwable t) {
            t.printStackTrace();
            errorMessage = t.getMessage();
            failed = true; // parse error?
            return;
        }
        
        submittedEmails = bean.getEmails();
        submittedExpires = bean.getExpires();
        submittedComment = bean.getComment();
        
        if ("invite".equals(bean.getAction())) {
            // permissions
            if (!current.getRole().canInviteUsers()) {
                failed = true;
                errorMessage = "Permission denied.";
                return; // permission denied
            }
            // parse emails
            List<InternetAddress> emails;
            try {
                emails = parseEmailList(bean.getEmails());
            } catch (Exception x) {
                failed = true;
                errorMessage = x.getMessage();
                return;
            }
            // parse expiration time
            DateTime expires;
            if (bean.getExpires() == null || bean.getExpires().trim().isEmpty())
                expires = null;
            else {
                int expireDays;
                try {
                    expireDays = Integer.parseInt(bean.getExpires().trim());
                    if (expireDays <= 0)
                        throw new Exception();
                } catch (Exception x) {
                    failed = true;
                    errorMessage = "Expiration time must be a number greater than 0, or blank for no expiration time.";
                    return;
                }
                expires = DateTime.now().plusDays(expireDays);
            }
            // do it
            warnings = new ArrayList<String>();
            for (InternetAddress e : emails) {
                try {
                    createInvite(e, current, expires, bean.getComment());
                } catch (Exception x) {
                    warnings.add(e.getAddress() + ": " + x.getMessage());
                }
            }
        } else if ("cancel".equals(bean.getAction())) {
            // permissions
            if (!current.getRole().canInviteUsers()) {
                failed = true;
                errorMessage = "Permission denied.";
                return; // permission denied
            }
            // do it
            try {
                Invite invite = Invite.findById((long)bean.getInvite());
                invite.cancel(current);
            } catch (Exception x) {
                failed = true;
                errorMessage = x.getMessage();
                return;
            }
        } else {
            // default action view invites
            invites = Invite.findAll();
            Collections.sort(invites, new InviteViewComparator());
        }
            
    }
    
    private static List<InternetAddress> parseEmailList (String str) {
        /*Muvment <muvmental@gmail.com>
        Marius Lite <extrazmas@gmail.com>
        Ivan Fokin <ivan72@gmail.com>
        Jacob Perlman (Numbers) <numbers.at.sunrise@gmail.com>
        Mitch Davis <mitchelljd@mac.com>
        Oren Davidson (Wizzard) <orendavidson@gmail.com>,test@ok.com */
        
        List<InternetAddress> emails = new ArrayList<InternetAddress>();
        
        String[] emailstrs = str.split("[,\\n\\r]");
        for (String email : emailstrs) {
            String t = email.trim();
            if (t.isEmpty())
                continue;
            try {
                InternetAddress addr = new InternetAddress(email);
                addr.validate();
                emails.add(addr);
            } catch (Throwable x) {
                throw new IllegalArgumentException("No invites have been sent: '" + email + "' does not appear to be a valid email address: " + x.getMessage());
            }
        }
        
        if (emails.isEmpty())
            throw new IllegalArgumentException("You must enter at least one email address.");
        
        return emails;

    }
    
    public boolean isFailed () {
        return failed;
    }
    
    public String getInviteWarningHtml () {
        
        if (warnings == null || warnings.isEmpty())
            return null;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Some invites could not be sent:<br><ul>");
        for (String w : warnings)
            sb.append("<li>").append(Util.html(w));
        sb.append("</ul>The rest of the invites were successfully sent.");
        
        return sb.toString();
        
    }
    
    public Collection<Invite> getInvites () {
        return invites;
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public String getSubmittedEmails () {
        return submittedEmails;
    }
    
    public String getSubmittedExpires () {
        return submittedExpires;
    }
    
    public String getSubmittedComment () {
        return submittedComment;
    }
    
    public static String getStatusHtml (Invite i) {
        switch (i.getStatus()) {
        case Invite.STATUS_ACCEPTED:
            return Util.html(String.format("Accepted by %s", DefaultDataConverter.objectAsString(i.getResolvedBy())));
        case Invite.STATUS_REJECTED:
            return Util.html(String.format("Rejected"));
        case Invite.STATUS_CANCELLED:
            return Util.html(String.format("Cancelled by %s", DefaultDataConverter.objectAsString(i.getCancelledBy())));
        case Invite.STATUS_ACTIVE:
            return i.isExpired() ? "Expired" : "Active";
        default:
            return Integer.toString(i.getStatus());
        }
    }
    
    private static void createInvite (InternetAddress e, User current, DateTime expiresOn, String comment) {
        Invite invite = new Invite(e.getAddress(), e.getPersonal(), current, expiresOn);
        invite.setComment(comment);
        Invite.addInvite(invite);
        QueuedEmail.queueNotification(QueuedEmail.TYPE_INVITE, invite);
    }

}
