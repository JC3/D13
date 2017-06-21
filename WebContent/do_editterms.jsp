<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
EditTerms edit = new EditTerms(pageContext, sess);

sess.clearAttribute(SessionData.SA_EDIT_TERMS_ERROR);
sess.clearAttribute(SessionData.SA_EDIT_TERMS_TITLE);
sess.clearAttribute(SessionData.SA_EDIT_TERMS_TEXT);

if (edit.isFailed()) {
    sess.setAttribute(SessionData.SA_EDIT_TERMS_ERROR, edit.getErrorMessage());
    sess.setAttribute(SessionData.SA_EDIT_TERMS_TITLE, edit.getSubmittedTitle());
    sess.setAttribute(SessionData.SA_EDIT_TERMS_TEXT, edit.getSubmittedText());
    response.sendRedirect(edit.getFailTarget());
} else {
    response.sendRedirect(edit.getSuccessTarget());
}
%>
