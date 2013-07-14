<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.util.Util" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    response.sendRedirect("index.jsp");
    return;
}

User user = sess.getUser();
String email_html = Util.html(user.getEmail());
String realname_html = Util.html(user.getRealName());
//String role_html = null; // = user.isAdmin() ? "Administrator" : null; //(user.isSpecialRole() ? Util.html(user.getRole().getName()) : null);
//if (user.isAdmin() && user.isAdmissions()) // this is getting hacky
//    role_html = "Administrator";
//else if (user.isAdmin())
//    role_html = "Registration";
//else if (user.isAdmissions())
//    role_html = "Admissions";
String role_html = Util.html(user.getRoleDisplay());

if (!user.isTermsAgreed()) {
    response.sendRedirect("terms.jsp");
    return;
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css"/>
</head>
<body>
<dis:header/>

<% if (user.getState() == UserState.APPROVED) { %>
  <% if (!user.isApprovalComplete()) { %>
<div class="notice">Your registration application has been reviewed and approved (yay!), but you have not filled out your approval
survey yet. This is required. Please click "approval survey" below to do this!</div>
  <% } else { %>
<div class="message">Your registration application has been reviewed and approved, and you're all ready to go! We're currently working
on our due payment service and will notify you when it's ready. Sit tight! Please <a href="mailto:camp@disorient.info?subject=Disorient 2013">contact us</a>
if you have any questions. See you on the playa!!</div>
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
here periodically for status updates!</div>
<% } %>

<div class="content">
<% if (user.getState() != UserState.REJECTED) { %>
<strong>Burning Man 2013 Camp Registration:</strong><br>
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
<% } %>
</ul>
<% } %>

<% if (user.isAdmin()) { %>
<strong>For Administrators:</strong><br><ul>
<!-- <li><a href="admin_users.jsp">Manage Users</a> -->
<li><a href="view_data.jsp">View Registration Data</a>
<li><a href="adminhelp.jsp">Help</a>
</ul>
<% } else if (user.isAdmissions()) { %>
<strong>For Admissions Team:</strong><br><ul>
<!-- <li><a href="admin_users.jsp">Manage Users</a> -->
<li><a href="view_data.jsp">View Registration Data</a>
<li><a href="adminhelp.jsp">Help</a>
</ul>
<% } %> 

<strong>Your Account:</strong><ul>
<li><a href="personal.jsp">Edit Profile</a>
<li><a href="do_logout.jsp">Log Out</a>
</ul>
</div>

</body>
</html>
