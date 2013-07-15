<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.util.Util" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must log in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

User user = sess.getUser();
if (!user.getRole().isSpecial())
    return; // permission denied.
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
h1 { font-size: larger; font-weight: bold; }
.section { padding-left: 4ex; }
</style>
</head>
<body>

<dis:header/>
<div class="nav">
    <a href="home.jsp">Home</a> |
    <a href="view_data.jsp">View Registration Data</a>
</div>

<p>This page describes how this site works, for administrative / registration / admissions team members.</p> 

<h1>Teams</h1>

<div class="section">

<p>Currently, you can be an Administrator, Registration Team member, or Admissions Team member. Registration Team
members can review and accept new applications and are responsible for making sure everything is in order before
accepting an application. Admissions Team members can approve or reject users. Administrators can do everything that
both of these teams can do.</p>

</div>

<h1>Users</h1>
<div class="section">

<p>Registrants are in one of the following states. Your job depends on what state the person is in. Summary:</p>

<ul>
  <li><strong>New:</strong> Somebody created an account but hasn't filled out the registration form yet.
  <li><strong>Needs Review:</strong> A new user has filled out their registration form, which must be reviewed and accepted by the registration team before the user can be approved or rejected.
  <li><strong>Registered:</strong> A user's regisration form has been accepted. This person is now waiting to be approved or rejected.
  <li><strong>Approved/Rejected:</strong> A decision has been made to approve or reject this person for camp this year.
</ul>

<p>Details:</p>

<p><strong>New Users</strong></p>
<div class="section">
<p>You probably don't care about these people. However, it's possible that this person signed up for
the site then forgot to finish registration, so if a new user sticks around for awhile, Registration 
Team members may wish to contact them and ask what's up.</p>
<p>When a new user finished their registration form, they will be marked as <strong>Needs Review</strong> and
a notification email will be sent to Administrators and Registration Team members.</p>
</div>

<p><strong>Needs Review</strong></p>
<div class="section">
<p>The Registration Team cares about these people. When a user needs review, you should view their profile,
registration info, and cell assignments, and make sure everything looks good -- emergency contact info, sponsors,
all that. You may wish to contact the person to resolve any issues, you can also edit their data yourself. If
you do contact the person, try and use the camp@disorient.info email address when possible.</p>
<p>Once you are sure that everything looks good, you can accept the application so that the Admissions Team can
make a decision.</p>
<p>When a person completes their registration form, all Administrators and Registration Team members will 
receive an email with a link to the user's detail page. This page can also be accessed through the administrator
section of the site. <em>Note: Currently, you will receive this email after the user fills out their registration
form but before they complete the cell signups. Basically what this means is that if you get the email, get
excited, and click the link right away, it may appear that the user has signed up for no cells because they are
probably going through that page at the same time. So, give it a minute.</em></p>
</div>

<p><strong>Registered</strong></p>
<div class="section">
<p>The Admissions Team cares about these people. These users have valid registration forms submitted, and are
waiting on an approve/reject decision. Carefully consider what their sponsor has to say, whether or not they'd
be a good fit for camp, and our camp head count limit before making a decision. Once you make this decision, it
is pretty much final. An Administrator can change a user's approve/reject state, so if you do change your mind
please contact an administrator -- try and avoid that, though.</p>
<p>When the Registration Team accepts an application, all Administrators and Admissions Team members will
receive an email with a link to the user's detail page. this page can also be accessed through the administrator
section of the site.</p>
</div>

<p><strong>Approved/Rejected</strong></p>
<div class="section">
<p>These are people that have been definitively approved or rejected by the Admissions Team.</p>
</div>

</div>

<h1>Using This Site</h1>
<div class="section">

<p><strong>General Usage</strong></p>
<div class="section">

<p>You can access most everything you need from the Home page. Site navigation will improve as time goes on and
I smooth things out. Feel free to <a href="mailto:jason.cipriani@gmail.com">email me</a> if you have any questions,
problems, suggestions, or compliments -- no matter how nitpicky you think you may be being, <em>all</em> input is
useful. This is a new system under development and will have some kinks.</p>

<p>Click "View Registration Data" on the Home page to access registration information. You can perform the following
tasks on this page:</p>

<ul>
<li><strong>To review and accept a registration application:</strong> Click "Review" next to a user to take you to
their details page. Once there, you can review and edit their application. There is a button at the bottom of this
page that will let you accept the application.

<li><strong>To approve / reject a user:</strong> Click "Admission" next to a user to take you to their details page.
Once there, you can review their application. There is a form at the bottom of this page that will let you approve
or reject the person.

<li><strong>To edit a user's profile (including password), registration info, or cell assignments:</strong> Click
the appropriate button next to a user to edit their information.
</ul>

<p>You can click any column header to sort the table by that column. You can also download the entire table in CSV
form if you need it for some reason. However, if you find you are working in your own spreadsheets, please let me
know why -- that's a good sign that whatever you are doing would be a handy feature to add to this system.</p>
</div>

<p><strong>Tricks</strong></p>
<div class="section">

<p>Again, this is a new site, so the navigation may be a little rough. However, here are some tricks to make your
life easier:</p>

<ul>
<li>If you sort the data table by the "Status" column, you can easily see at a glance which users need review (if
you are on Registration Team) or which users need approved or rejected (if you are on Admissions Team). In the future
I'll add filters to this page to only show you users that require an action. For now, this will have to do.
<li>You can click on a user's email address in this page to email them - although it will go through your personal
info rather than camp@disorient.info.
<li>If you sort the data table by the "Role" column, you can easily see who the administrators and other team members
are, in case you need to contact somebody. The sort order is a bit odd (special team members will be at the bottom of
the list).
</ul>
</div>

<p><strong>Other Notes</strong></p>
<div class="section">

<ul>
<li>You may notice some buttons in the "Action" column are missing for certain users. This is because you don't have
permission to perform those actions on those users. If this causes a problem, please let me know.
<li>The notification email is not very robust. If any administrator, registration or admissions team member has an
invalid email address, then the notifications will not be sent to <em>anybody</em>. This will change in the future,
but please make sure that your email address is valid!
</ul>
</div>

<p><strong>TODO</strong></p>
<div class="section">

<p>There is still a lot of work to be done on this site. Here are the urgent things I am working on now:</p>
<ul>
<li>Notification emails (including approval/rejection emails to users) are currently broken because of SMTP server
issues that are currently unresolved. This is the highest priority issue. Any users you approve / reject now, before I
get this working, will be notified once I implement this.
<li>Due payments -- Integrate due payments with PayPal.
<li>Cell view -- See all members of a given cell.
<li>Group view -- See camping groups.
<li>Mailing lists -- Get useful collections of email addresses (all users, registered users, approved campers, by cell, by group)
</ul>

<p>Here are other important things I need to work on:</p>
<ul>
<li>Social pod assignment and other administrative info tracking.
<li>Filters on the registration data page.
<li>"Inbox" to provide quick list of users that need action.
<li>Activity log to track who reviewed/approved/rejected user.
<li>Ability to add comments about users.
<li>Interface for editing rules, questions, etc.
<li>Improved site navigation; maybe a nav bar with breadcrumbs or something.
</ul>

</div>
</div>


<div class="nav">
    <a href="home.jsp">Home</a> |
    <a href="view_data.jsp">View Registration Data</a>
</div>
<dis:footer/>

</body>
</html>
