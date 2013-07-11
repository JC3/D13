<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
EditUser edit = new EditUser(pageContext, sess);

sess.clearAttribute(SessionData.SA_USER_PROFILE_DEFAULTS);
sess.clearAttribute(SessionData.SA_USER_PROFILE_ERROR);

if (edit.isFailed()) {
    sess.setAttribute(SessionData.SA_USER_PROFILE_DEFAULTS, edit.getUserProfileBean());
    sess.setAttribute(SessionData.SA_USER_PROFILE_ERROR, edit.getErrorMessage());
    response.sendRedirect(edit.getFailTarget());
} else {
    response.sendRedirect(edit.getSuccessTarget());
}
%>
