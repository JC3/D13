<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect(this.getServletContext().getContextPath() + "/index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

User u = sess.getUser();
boolean canExport = u.getRole().canViewUsers();
boolean canCheck = u.getRole().canViewUsers();

if (!canExport && !canCheck)
    return;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Strict//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient Registration Tools</title>
<style type="text/css">
h1 { 
    font-size: medium;
    font-weight: bold; 
}
</style>
</head>
<body>
<% if (canExport) { %>
<h1>Export Form Data</h1>
<ul>
<li><a href="export.jsp?what=cells">Cell Descriptions</a> (<a href="export.jsp?what=cells&download">Download</a>)
<li><a href="export.jsp?what=profile">User Profile Questions</a> (<a href="export.jsp?what=profile&download">Download</a>)
<li><a href="export.jsp?what=registration">Registration Questions</a> (<a href="export.jsp?what=registration&download">Download</a>)
<li><a href="export.jsp?what=survey">Approval Survey Questions</a> (<a href="export.jsp?what=survey&download">Download</a>)
</ul>
<% } %>
<% if (canCheck) { %>
<h1>Tools</h1>
<ul>
<li><a href="check.jsp?separate">Find Weird Applications</a>
<li><a href="phone.jsp">Phone Numbers</a>
</ul>
<% } %>
</body>
</html>