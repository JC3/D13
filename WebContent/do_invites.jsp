<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
ManageInvites manager = new ManageInvites(pageContext, sess);

sess.clearAttribute(SessionData.SA_MANAGE_INVITES_ERROR);
sess.clearAttribute(SessionData.SA_MANAGE_INVITES_EMAILS);
sess.clearAttribute(SessionData.SA_MANAGE_INVITES_EXPIRES);
sess.clearAttribute(SessionData.SA_MANAGE_INVITES_COMMENT);
sess.clearAttribute(SessionData.SA_MANAGE_INVITES_WARNING);

if (manager.isFailed()) {
    sess.setAttribute(SessionData.SA_MANAGE_INVITES_ERROR, manager.getErrorMessage());
}

sess.setAttribute(SessionData.SA_MANAGE_INVITES_EMAILS, manager.getSubmittedEmails());
sess.setAttribute(SessionData.SA_MANAGE_INVITES_EXPIRES, manager.getSubmittedExpires());
sess.setAttribute(SessionData.SA_MANAGE_INVITES_COMMENT, manager.getSubmittedComment());
sess.setAttribute(SessionData.SA_MANAGE_INVITES_WARNING, manager.getInviteWarningHtml());

response.sendRedirect("view_invites.jsp");
%>
