<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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

<% if (!user.isRegistrationComplete()) { %>
<div class="notice">You have not filled out the registration form yet. Please click "register for camp" below to do this! If you do not fill
out this form, then you will not be camping with us this year!</div>
<% } else if (!user.isInCells()) { %>
<div class="notice">Disorient is made by DOers! We need <strong>you</strong> to help create your camp and make Disorient happen! Please click
"sign up for cells" below to volunteer for cells. <strong>Anything</strong> you can do will help us all have a great burn!</div>
<% } %>

<div class="content">
<strong>Burning Man 2013 Camp Registration:</strong><br>
<ul>
<li><a href="terms.jsp">Rules and Info</a>
<% if (!user.isRegistrationComplete()) { %>
<li><b>NEXT STEP: <a href="registration.jsp">Register for camp!</a></b>
<% } else if (!user.isInCells()) { %>
<li><a href="registration.jsp">Edit Registration</a>
<li><b>NEXT STEP: <a href="cells.jsp">Sign up for cells!</a></b>
<% } else { %>
<li><a href="registration.jsp">Edit Registration</a>
<li><a href="cells.jsp">Edit Cell Assignments</a>
<% } %>
</ul>

<% if (user.isAdmin()) { %>
<strong>For Administrators:</strong><br><ul>
<!-- <li><a href="admin_users.jsp">Manage Users</a> -->
<li><a href="view_data.jsp">View Registration Data</a>
</ul>
<% } else if (user.isAdmissions()) { %>
<strong>For Admissions Team:</strong><br><ul>
<!-- <li><a href="admin_users.jsp">Manage Users</a> -->
<li><a href="view_data.jsp">View Registration Data</a>
</ul>
<% } %> 

<strong>Your Account:</strong><ul>
<li><a href="personal.jsp">Edit Profile</a>
<li><a href="do_logout.jsp">Log Out</a>
</ul>
</div>

</body>
</html>
