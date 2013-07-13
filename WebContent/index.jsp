<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%

SessionData sess = new SessionData(session);

String error = (String)sess.getAndClearAttribute(SessionData.SA_LOGIN_ERROR);
String message = request.getParameterMap().containsKey("loggedout") ? "You have been logged out." : null; //(String)sess.getAndClearAttribute(SessionData.SA_LOGIN_MESSAGE);
String email = (String)sess.getAttribute(SessionData.SA_LOGIN_EMAIL);
String next = request.getParameter("next");
boolean existing = Util.unbox((Boolean)sess.getAttribute(SessionData.SA_LOGIN_EXISTING));

String error_html = (error == null ? null : StringEscapeUtils.escapeHtml(error));
String message_html = (message == null ? null : StringEscapeUtils.escapeHtml(message));
String email_html = (email == null ? "" : StringEscapeUtils.escapeHtml(email));
String next_html = (next == null ? "" : StringEscapeUtils.escapeHtml(next));

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="disorient.css"/>
<title>Disorient</title>
</head>
<body>
<dis:header/>
<br><br><br><br>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<% if (message != null) { %>
<div class="message"><%=message_html%></div>
<% } %>

<form action="do_login.jsp" method="post">
<input type="hidden" name="next" value="<%=next_html%>">
<table class="form">
<tr>
    <td>Email Address:
    <td><input class="dtext" type="text" name="email" value="<%=email_html%>">
<tr class="section">
    <td><input class="dradio" type="radio" name="existing" value="0" <%=existing?"":"checked" %>>I am a new user.
    <td>
<tr>
    <td><input class="dradio" type="radio" id="existing" name="existing" value="1" <%=existing?"checked":"" %>>I am an existing user. Password:
    <td><input class="dtext" type="password" name="password" onKeyUp="document.getElementById('existing').checked=true;">
<tr class="section">
    <td colspan="2" style="text-align:center"><input class="dbutton" type="submit" value="Continue">
</table>
</form>

<div class="content">
<strong>We are now using a new registration system. Even if you have registered in previous years, you will have to create
a new account above!</strong>
</div>


</body>
</html>