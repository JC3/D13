<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.util.Util" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
static String minsec (double seconds) {
    long tmil = (long)(seconds * 1000.0 + 0.5);
    long mil = tmil % 60000; tmil /= 60000;
    long min = tmil % 60; tmil /= 60;
    long hrs = tmil;
    if (hrs == 0 && min == 0)
        return String.format("%.1f", seconds);
    else if (hrs == 0)
        return String.format("%d:%04.1f", min, mil / 1000.0, seconds);
    else
        return String.format("%d:%02d:%04.1f", hrs, min, mil / 1000.0, seconds);
}

static String timeStr (double median, double mean) {
    if (median < 0 || mean < 0)
        return "-";
    else
        return Util.html(String.format("%s / %s", minsec(median), minsec(mean)));
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    response.sendRedirect("index.jsp");
    return;
}

User user = sess.getUser();
Role role = user.getRole();
String email_html = Util.html(user.getEmail());
String realname_html = Util.html(user.getRealName());
//String role_html = null; // = user.isAdmin() ? "Administrator" : null; //(user.isSpecialRole() ? Util.html(role.getName()) : null);
//if (user.isAdmin() && user.isAdmissions()) // this is getting hacky
//    role_html = "Administrator";
//else if (user.isAdmin())
//    role_html = "Registration";
//else if (user.isAdmissions())
//    role_html = "Admissions";
String role_html = Util.html(user.getRoleDisplay());

if (user.isInviteCodeNeeded()) {
    response.sendRedirect("invite.jsp");
    return;
}

if (!user.isTermsAgreed()) {
    response.sendRedirect("terms.jsp");
    return;
}

Cookie[] cs = request.getCookies();
if (cs != null) {
    for (Cookie c : cs) {
        if ("inviteCode".equals(c.getName())) {
            c.setMaxAge(0);
            response.addCookie(c);
        }
    }
}

WorkStatistics stats = new WorkStatistics(user);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dis:common/>
<style type="text/css">
table.stats {
    font-size: 80%;
    font-family: monospace;
    white-space: nowrap;
    border-spacing: 0;
}
table.stats td.n, table.stats td.t {
    text-align: right;
}
table.stats td {
    border-right: 1px solid #303030;
    padding-left: 0.5ex;
    padding-right: 0.5ex;
}
table.stats td.cant {
    color: #303030;
}
table.stats th {
    border-right: 1px solid #303030;
}
table.stats tr.odd td {
    background: #1a1a1a;
}
table.stats .right {
    border-right: 0 !important;
}
</style>
</head>
<body>
<dis:header/>

<% if (user.getState() == UserState.APPROVED) { %>
  <% if (!user.isApprovalComplete()) { %>
<div class="notice">Your registration application has been reviewed and approved (yay!), but you have not filled out your approval
survey yet. This is required. Please click "approval survey" below to do this!</div>
  <% } else if (user.isPaid()) { %>
<div class="message">Your registration application has been reviewed and approved, and you have filled out your approval survey
and paid your dues. You're all done here! Please keep your information here updated and keep an eye out for important emails.
See you on the playa!</div>
  <% } else { %>
<div class="message">Your registration application has been reviewed and approved, and you have filled out your approval survey (thanks!).
All that's left is to pay your dues. Please click "pay dues" below to do this!</div>
  <% } %>
<% } else if (user.getState() == UserState.REJECTED) { %>
<div class="error">We're sorry, your registration application has not been approved. We still want to see you on the playa,
so stop on by! Have a great burn!</div>
<% } else if (!user.isRegistrationComplete()) { %>
<div class="notice">You have not filled out the registration form yet. Please click "register for camp" below to do this! If you do not fill
out this form, then you will not be camping with us this year!</div>
<% } else if (!user.isInCells()) { %>
<div class="notice">Disorient is made by DOers! We need <strong>you</strong> to help create your camp and make Disorient happen! Please click
"sign up for cells" below to volunteer for cells. <strong>Anything</strong> you can do will help us all have a great burn!</div>
<% } else { %>
<div class="notice">Thank you for applying to Disorient! Your application is complete and is now being reviewed. Please check back
here periodically for status updates! <strong>If your application is approved, you will receive due payment instructions via email!</strong></div>
<% } %>

<div class="content">
<% if (user.getState() != UserState.REJECTED) { %>
<strong>Burning Man <%=ThisYear.CAMP_YEAR %> Camp Registration:</strong><br>
<ul>
<li><a href="terms.jsp">Rules and Info</a>
<% if (!user.isRegistrationComplete()) { %>
  <li><b>NEXT STEP: <a href="registration.jsp">Register for camp!</a></b>
<% } else { %>
  <li><a href="registration.jsp">Edit Registration</a>
  <% if (!user.isInCells()) { %>
    <li><b>NEXT STEP: <a href="cells.jsp">Sign up for cells!</a></b>
  <% } else { %>
    <li><a href="cells.jsp">Edit Cell Assignments</a>
  <% } %>
<% } %>
<% if (user.getState() == UserState.APPROVED) { %>
  <% if (!user.isApprovalComplete()) { %>
    <li><b>NEXT STEP: <a href="survey.jsp">Complete approval survey!</a></b>
  <% } else { %>
    <li><a href="survey.jsp">Edit Approval Survey</a>
  <% } %>
  <% if (!user.isPaid()) { %>
    <li><b>NEXT STEP: <a href="dues.jsp">Pay dues!</a></b>
  <% } else { %>
    <li><a href="dues.jsp">Dues Payments</a>
  <% } %>
<% } %>
</ul>
<% } %>

<% if (role.isSpecial()) { %>
<strong>For Administrators:</strong><br><ul>
<!-- <li><a href="admin_users.jsp">Manage Users</a> -->
<li><form action="view_data.jsp" method="get">Search for user: <input type="text" name="search" class="dtext" style="width:20ex;"> <input type="submit" value="Search" class="dbutton" style="width:10ex;"></form>
<li><a href="view_data.jsp">View Registration Data</a>
<ul>
  <li><a href="view_data.jsp?qf=1">Only users that need registration applications reviewed.</a>
  <li><a href="view_data.jsp?qf=2">Only users that need to be approved or rejected.</a>
  <li><a href="view_data.jsp?qf=3">Only users that need to be finalized.</a>
  <li><a href="view_data.jsp?qf=4">Only users that have been approved.</a>
  <li><a href="view_data.jsp?qf=5">Only users that own RVs.</a>
  <li><a href="view_data.jsp?qf=6">Only users that need to pay their dues.</a>
  <li><a href="view_data.jsp?qf=7">Only users that need to complete their approval surveys.</a>
  <li><a href="view_data.jsp?qf=8">All users that need to sign up for more work cells.</a>
  <li><a href="view_data.jsp?qf=9">Only not-yet-approved users that need to sign up for work cells.</a>
  <li><a href="view_data.jsp?qf=10">Only approved users that need to sign up for work cells.</a>  
</ul>
<li><a href="view_cells2.jsp">View<%= role.canEditCells() ? " / Edit" : "" %> Cells</a>
<!-- <li><a href="view_groups.jsp">View Camper Groups</a> (removed for 2016 when group leader went away) -->
<li><a href="view_finance.jsp">View Dues Report</a>
<%   if (RuntimeOptions.Global.isInviteOnly()) { %>
<%     if (role.canInviteUsers()) { %>
<li><a href="view_invites.jsp">View / Manage Invites</a>
<%     } else if (role.canViewInvites()) { %>
<li><a href="view_invites.jsp">View Invites</a>
<%     } %>
<%   } %>
<li><a href="activity.jsp">View Site Activity</a>
<%   if (role.canViewAdminData() || role.canEditTerms() || role.canEditAnnouncements() || role.canEditMailTemplates()) { %>
<li>System Info / Settings
<ul>
<%     if (role.canEditAnnouncements()) { %>
<li><a href="editannounce.jsp">Edit Announcement Message</a>
<%     } %>
<%     if (role.canEditTerms()) { %>
  <li><a href="editterms.jsp">Edit Registration Terms</a>
<%     } %>
<%     if (role.canEditMailTemplates()) { %>
  <li><a href="editmails.jsp">Edit Email Templates</a>
<%     } %>
<%     if (role.canViewAdminData()) { %>
  <li><a href="view_roles.jsp">View Special Users / Privileges / Roles</a>
  <li><a href="view_options.jsp">View Site Configuration</a>
<%     } %>
</ul>
<%   } %>
<li><a href="adminhelp.jsp">Help (way out of date, but mostly valid)</a>
</ul>
<% } %>

<strong>Your Account:</strong><ul>
<li><a href="personal.jsp">Edit Profile</a>
<li><a href="do_logout.jsp">Log Out</a>
</ul>
</div>

<% if (stats.getUserStatistics() != null) { %>
<table class="form stats">
<tr>
  <th>
  <th>
  <th>
  <th colspan="5">Count
  <th colspan="4">Median / Mean Time (HH:MM:SS.S)
  <th colspan="5" class="right">Multiple Actions
<tr>
  <th>Name
  <th>Role
  <th>Login
  <th>Inv.
  <th>Rev.
  <th>Adm.
  <th>Fin.
  <th>Tot.
  <th>Rev.
  <th>Adm.
  <th>Fin.
  <th>Response
  <th>R&amp;A
  <th>A&amp;F
  <th>R&amp;F
  <th>All
  <th class="right">Any
<%   int row = 0; 
     for (WorkStatistics.UserStatistics s : stats.getUserStatistics()) { 
       ++ row; %>
<tr class="<%= (row % 2 == 0) ? "even" : "odd" %>">
  <td class="s"><%= Util.html(s.getName()) %>
  <td class="s"><%= Util.html(s.getRoleDisplay()) %>
  <td class="s"><%= Util.html(DefaultDataConverter.objectAsString(s.getLastLogin())) %>
  <td class="n<%= s.getUser().getRole().canInviteUsers() ? "" : " cant" %>"><%= s.getInvited() %>
  <td class="n<%= s.getUser().getRole().canReviewUsers() ? "" : " cant" %>"><%= s.getReviewed() %>
  <td class="n<%= s.getUser().getRole().canAdmitUsers() ? "" : " cant" %>"><%= s.getAdmitted() %>
  <td class="n<%= s.getUser().getRole().canFinalizeUsers() ? "" : " cant" %>"><%= s.getFinalized() %>
  <td class="n"><%= s.getActions() %>
  <td class="t<%= s.getUser().getRole().canReviewUsers() ? "" : " cant" %>"><%= timeStr(s.getMedianReviewTime(), s.getMeanReviewTime()) %>
  <td class="t<%= s.getUser().getRole().canAdmitUsers() ? "" : " cant" %>"><%= timeStr(s.getMedianAdmitTime(), s.getMeanAdmitTime()) %>
  <td class="t<%= s.getUser().getRole().canFinalizeUsers() ? "" : " cant" %>"><%= timeStr(s.getMedianFinalizeTime(), s.getMeanFinalizeTime()) %>
  <td class="t<%= s.getUser().getRole().canReviewUsers() ? "" : " cant" %>"><%= timeStr(s.getMedianResponseTime(), s.getMeanResponseTime()) %>
  <td class="n"><%= s.getReviewAdmitOverlap() %>
  <td class="n"><%= s.getAdmitFinalizeOverlap() %>
  <td class="n"><%= s.getReviewFinalizeOverlap() %>
  <td class="n"><%= s.getCompleteOverlap() %>
  <td class="n right"><%= s.getAnyOverlap() %>
<%   } %>
</table>
<% } %>

<dis:footer/>

</body>
</html>
