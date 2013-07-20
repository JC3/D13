<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
EditDues edit = new EditDues(pageContext, sess);

sess.clearAttribute(SessionData.SA_EDIT_DUES_DEFAULTS);
sess.clearAttribute(SessionData.SA_EDIT_DUES_ERROR);

if (edit.isFailed()) {
    sess.setAttribute(SessionData.SA_EDIT_DUES_DEFAULTS, edit.getCustomDuesBean());
    sess.setAttribute(SessionData.SA_EDIT_DUES_ERROR, edit.getErrorMessage());
    response.sendRedirect(edit.getFailTarget());
} else {
    response.sendRedirect(edit.getSuccessTarget());
}
%>
