<%@page import="d13.ThisYear"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.dao.RuntimeOptions" %>
<%@ page import="d13.util.Util" %>
<%@ page import="d13.notify.BackgroundNotificationManager" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%

SessionData sess = new SessionData(session);
if (sess.isLoggedIn()) {
    response.sendRedirect("home.jsp");
    return;
}

boolean closed = RuntimeOptions.Global.isRegistrationClosed();

String error = (String)sess.getAndClearAttribute(SessionData.SA_LOGIN_ERROR);
String message = request.getParameterMap().containsKey("loggedout") ? "You have been logged out." : null; //(String)sess.getAndClearAttribute(SessionData.SA_LOGIN_MESSAGE);
String xmessage = (String)sess.getAndClearAttribute(SessionData.SA_LOGIN_MESSAGE);
if (xmessage != null) message = xmessage;
String email = (String)sess.getAttribute(SessionData.SA_LOGIN_EMAIL);
String next = request.getParameter("next");
boolean existing = Util.unbox((Boolean)sess.getAttribute(SessionData.SA_LOGIN_EXISTING));

String error_html = (error == null ? null : StringEscapeUtils.escapeHtml(error));
String message_html = (message == null ? null : StringEscapeUtils.escapeHtml(message));
String email_html = (email == null ? "" : StringEscapeUtils.escapeHtml(email));
String next_html = (next == null ? "" : StringEscapeUtils.escapeHtml(next));

// to avoid confusing users, and since notifications are often disabled because of smtp server
// issues; do not display the pw reset link of notifications are disabled (since pw reset relies
// on notification emails to function correctly).
boolean pwResetDisabled = !"1".equals(RuntimeOptions.getOption(BackgroundNotificationManager.RT_ENABLE_NOTIFY, "1"));

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="disorient.css"/>
<title>Disorient</title>
<script language="JavaScript" type="text/javascript">
function doForgot () {
	document.getElementById("forgot").value = "1";
    document.getElementById("loginForm").submit();
}
</script>
</head>
<body>
<dis:header blank="true"/>
<br><br><br><br>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<% if (message != null) { %>
<div class="message"><%=message_html%></div>
<% } %>

<form action="do_login.jsp" method="post" id="loginForm">
<input type="hidden" name="next" value="<%=next_html%>">
<table class="form">
<tr>
    <td>Email Address:
    <td><input class="dtext" type="text" name="email" value="<%=email_html%>">
<tr class="section">
    <td><% if (!closed) { %><input class="dradio" type="radio" name="existing" value="0" <%=existing?"":"checked" %>>I am a new user.<% } %>
    <td>
<tr>
    <td><input class="dradio" type="radio" id="existing" name="existing" value="1" <%=(existing||closed)?"checked":"" %>>I am an existing user. Password:
    <td><input class="dtext" type="password" name="password" onKeyUp="document.getElementById('existing').checked=true;">
<tr>
    <td>
    <td>
        <% if (!pwResetDisabled) { %><a href="javascript:doForgot();" style="font-size:smaller;">I forgot my password.</a><% } %>
        <input type="hidden" id="forgot" name="forgot" value="0">
<tr class="section">
    <td colspan="2" style="text-align:center"><input class="dbutton" type="submit" value="Continue">
</table>
</form>

<div class="content">
<strong>If you registered in 2013 or later, you may use the same email and password this year. If you last registered in 2012 or earlier, you will have to create
a new account above!</strong>
</div>

<dis:footer/>

</body>
</html>