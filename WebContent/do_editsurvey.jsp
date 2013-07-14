<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
EditSurvey edit = new EditSurvey(pageContext, sess);

sess.clearAttribute(SessionData.SA_SURVEY_DEFAULTS);
sess.clearAttribute(SessionData.SA_SURVEY_ERROR);

if (edit.isFailed()) {
    sess.setAttribute(SessionData.SA_SURVEY_DEFAULTS, edit.getSurveyBean());
    sess.setAttribute(SessionData.SA_SURVEY_ERROR, edit.getErrorMessage());
    response.sendRedirect(edit.getFailTarget());
} else {
    response.sendRedirect(edit.getSuccessTarget());
}
%>
