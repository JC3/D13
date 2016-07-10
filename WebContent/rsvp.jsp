<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1"%>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
String loginurl = request.getScheme() + "://" +
        request.getServerName() + 
        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) +
        request.getRequestURI().toString().replace("rsvp", "index") +
       (request.getQueryString() != null ? "?" + request.getQueryString() : "");

Invite invite;
try {
    invite = Invite.findByInviteCode(request.getParameter("code"));
    if (invite == null) throw new Exception("no invite code");
    if (!invite.isActive()) throw new Exception("invite not active");
} catch (Throwable t) {
    response.sendRedirect(loginurl);
    return;
}
if (invite != null) {
    SessionData sess = new SessionData(session);
    sess.setAttribute(SessionData.SA_LOGIN_EMAIL, invite.getInviteeEmail());
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
</head>
<body>
<dis:header blank="true"/>
<br><br><br><br>

<div class="content">

<p>Your invite code: <%= Util.html(invite.getInviteCode()) %></p>
<p>This invite is intended for <b><%=Util.html(invite.getInviteeName()) %></b>. If this is not you, <a href="index.jsp?code="> please click here</a>!</p>
<p>Thank you for considering being a part of D16ORIENT. To respond to this invite, please do one of the following:</p>
<ul>
<p>To <b>accept</b>, simply proceed to <a href="<%=loginurl %>">the login page</a> or visit this site at any point in the future, log in / create an account, and enter your invite code if prompted.
<p>To <b>reject</b>, please <a href="rejectinvite.jsp?code=<%=Util.html(invite.getInviteCode()) %>" onclick="return confirm('Are you sure you wish to reject this invite? This decision is final.')">click here</a>. Doing so indicates that you will not be joining D15ORIENT this year and opens the slot up for other campers, so if you are sure you are not going to be camping with Disorient, please choose this option! This will invalidate your invite code.</p>
</ul>
<p>If you have any questions or problems, please email <a href="mailto:camp@disorient.info">camp@disorient.info</a>. Please also note that all applications are still subject to the usual review process, this invite does not guarantee that applications will be accepted. You will be notified ASAP. Thanks again!</p>

</div>

<dis:footer/>
</body>
</html>