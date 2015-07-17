<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.*" %>
<%
// todo: get this code out of the jsp

SessionData sess = new SessionData(session);
if (!sess.isLoggedIn())
    return;

User user = sess.getUser();
if (!user.isInviteCodeNeeded())
    return;

sess.clearAttribute(SessionData.SA_INVITE_ERROR);

String code = request.getParameter("code");
code = (code == null ? "" : code.trim());
if (code.isEmpty()) {
    sess.setAttribute(SessionData.SA_INVITE_ERROR, "Invite code must be specified.");
    response.sendRedirect("invite.jsp");
    return;
}

Invite invite = Invite.findByInviteCode(code);
if (invite == null) {
    sess.setAttribute(SessionData.SA_INVITE_ERROR, "Invalid invite code specified. Please enter the code from the email you received. Code is case-sensitive.");
    response.sendRedirect("invite.jsp?code=" + code);
    return;
}

if ("accept".equals(request.getParameter("action"))) {
	try {
	    invite.accept(user);
	    user.setCurrentInvite(invite);
	} catch (Exception x) {
	    sess.setAttribute(SessionData.SA_INVITE_ERROR, x.getMessage());
	    response.sendRedirect("invite.jsp?code=" + code);
	    return;
	}
} else if ("reject".equals(request.getParameter("action"))) {
    // todo: invite rejection; without requiring user
    // this will probably be in a different place and not here.
} else {
    return;
}

response.sendRedirect("home.jsp");
%>