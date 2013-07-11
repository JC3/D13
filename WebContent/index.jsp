<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%

SessionData sess = new SessionData(session);

String error = (String)sess.getAndClearAttribute(SessionData.SA_LOGIN_ERROR);
String message = request.getParameterMap().containsKey("loggedout") ? "You have been logged out." : null; //(String)sess.getAndClearAttribute(SessionData.SA_LOGIN_MESSAGE);
String email = (String)sess.getAttribute(SessionData.SA_LOGIN_EMAIL);
boolean existing = Util.unbox((Boolean)sess.getAttribute(SessionData.SA_LOGIN_EXISTING));

String error_html = (error == null ? null : StringEscapeUtils.escapeHtml(error));
String message_html = (message == null ? null : StringEscapeUtils.escapeHtml(message));
String email_html = (email == null ? "" : StringEscapeUtils.escapeHtml(email));

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
td { white-space: nowrap; }
.error { color: red; }
.message { color: #008000; }
</style>
<title>Disorient</title>
</head>
<body>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<% if (message != null) { %>
<div class="message"><%=message_html%></div>
<% } %>

<form action="do_login.jsp" method="post">
<table>
<tr>
    <td>Email Address:
    <td><input type="text" name="email" value="<%=email_html%>">
<tr>
    <td><input type="radio" name="existing" value="0" <%=existing?"":"checked" %>>I am a new user.
    <td>
<tr>
    <td><input type="radio" id="existing" name="existing" value="1" <%=existing?"checked":"" %>>I am an existing user. Password:
    <td><input type="password" name="password" onKeyUp="document.getElementById('existing').checked=true;">
<tr>
    <td colspan="2"><input type="submit" value="Continue">
</table>
</form>

</body>
</html>