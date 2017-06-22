<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    response.sendRedirect("index.jsp");
    return;
}

User user = sess.getUser();

if (!user.isInviteCodeNeeded()) {
    response.sendRedirect("home.jsp");
    return;
}

String error = (String)sess.getAndClearAttribute(SessionData.SA_INVITE_ERROR);
String error_html = (error == null ? null : Util.html(error));
String code = request.getParameter("code");
String code_html = (code == null ? null : Util.html(code));

if (code_html == null || code_html.isEmpty()) {
    Cookie[] cs = request.getCookies();
    if (cs != null) {
        for (Cookie c : cs) {
            //System.err.println(c.getName() + " => " + c.getValue());
            if ("inviteCode".equals(c.getName())) {
                code = c.getValue();
                code_html = (code == null ? "" : Util.html(code));
            }
        }
    }
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dis:common/>
</head>
<body>
<dis:header/>
<br><br><br><br>

<% if (error_html != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<form action="do_resolveinvite.jsp" method="post">
<input type="hidden" name="action" value="accept">
<table class="form">
<tr>
    <td>Invite Code:
    <td><input class="dtext" type="text" name="code" value="<%=code_html==null?"":code_html%>">
<tr>
    <td colspan="2" class="instruct">Campers invited to apply will have received an invite code via email. Please enter your code to continue.
<tr class="section">
    <td colspan="2" style="text-align:center"><input class="dbutton" type="submit" value="Continue">
</table>
</form>

<dis:footer/>
</body>
</html>