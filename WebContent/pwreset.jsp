<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%

String key = request.getParameter("key");

User user;
try {
    user = User.findByPasswordResetKey(key);
} catch (Exception x) {
    x.printStackTrace();
    user = null;
}

if (user == null) {
    System.err.println("pwreset.jsp: Invalid or missing key, or other error. Denying access.");
    response.sendRedirect("index.jsp");
    return;
}

SessionData sess = new SessionData(session);
String error = (String)sess.getAndClearAttribute(SessionData.SA_PWRESET_ERROR);

boolean expired = user.isPasswordResetTimeExpired();
String userid_html = Long.toString(user.getUserId());
String email_html = Util.html(user.getEmail());
String pwkey_html = Util.html(key);
String error_html = (error == null ? null : StringEscapeUtils.escapeHtml(error));

if (expired) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, PasswordReset.EXPIRATION_INDEX_MESSAGE);
    response.sendRedirect("index.jsp");
    return;
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dis:common title="Reset Password"/>
</head>
<body>
<dis:header blank="true"/>
<br><br><br><br>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<form action="do_pwreset.jsp" method="post">
<input type="hidden" name="user" value="<%=userid_html %>">
<input type="hidden" name="key" value="<%=pwkey_html %>">
<table class="form">
<tr>
    <td>Email Address:
    <td><%=email_html%>
<tr>
    <td>New Password:
    <td><input class="dtext" type="password" name="password">
<tr>
    <td>Confirm Password:
    <td><input class="dtext" type="password" name="password2">
<tr class="section">
    <td colspan="2" style="text-align:center">
      <input class="dbutton" type="submit" value="Set Password">
      <input class="dbutton" type="button" value="Cancel">
</table>
</form>

<dis:footer/>
</body>
</html>