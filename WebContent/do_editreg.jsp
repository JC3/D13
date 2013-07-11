<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
EditRegistration edit = new EditRegistration(pageContext, sess);

sess.clearAttribute(SessionData.SA_REG_DEFAULTS);
sess.clearAttribute(SessionData.SA_REG_ERROR);

if (edit.isFailed()) {
    sess.setAttribute(SessionData.SA_REG_DEFAULTS, edit.getRegistrationBean());
    sess.setAttribute(SessionData.SA_REG_ERROR, edit.getErrorMessage());
    response.sendRedirect(edit.getFailTarget());
} else {
    response.sendRedirect(edit.getSuccessTarget());
}
%>
