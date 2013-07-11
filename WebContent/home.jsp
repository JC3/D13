<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.util.Util" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    response.sendRedirect("index.jsp");
    return;
}

User user = sess.getUser();
String email_html = Util.html(user.getEmail());
String realname_html = Util.html(user.getRealName());
String role_html = user.isAdmin() ? "Administrator" : null; //(user.isSpecialRole() ? Util.html(user.getRole().getName()) : null);

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
</head>
<body>

You are logged in as:<br>
Email: <%= email_html %><br>
Name: <%= realname_html %><br>
<% if (role_html != null) { %>Role: <%= role_html %><br><% } %>
Not you? <a href="do_logout.jsp">Log Out</a><br>

<hr>

<% if (!user.isRegistrationComplete()) { %>
<p>You have not filled out the registration form yet. Please click "register for camp" below to do this! If you do not fill
out this form, then you will not be camping with us this year!</p>
<% } else if (!user.isInCells()) { %>
<p>Disorient is made by Disorienters! We need <b>you</b> to help create your camp and make Disorient happen! Please click
"sign up for cells" below to volunteer for cells. <b>Anything</b> you can do will help us all have a great burn!</p>
<% } %>


<b>Burning Man 2013 Camp Registration:</b><br>
<ul>
<% if (!user.isRegistrationComplete()) { %>
<li><b><a href="registration.jsp">Register for camp!</a></b>
<% } else if (!user.isInCells()) { %>
<li><a href="registration.jsp">Edit Registration</a>
<li><b><a href="cells.jsp">Sign up for cells!</a></b>
<% } else { %>
<li><a href="registration.jsp">Edit Registration</a>
<li><a href="cells.jsp">Edit Cell Assignments</a>
<% } %>
</ul>

<% if (user.isAdmin()) { %>
<b>For Administrators:</b><br><ul>
<li><a href="admin_users.jsp">Manage Users</a>
<li><a href="view_data.jsp">View Registration Data</a>
</ul>
<% } %> 

<b>Your Account:</b><ul>
<li><a href="personal.jsp">Edit Profile</a>
<li><a href="do_logout.jsp">Log Out</a>
</ul>


</body>
</html>
