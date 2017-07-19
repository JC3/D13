<%@ page language="java" contentType="application/json; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
ReportController.Save control = new ReportController.Save(pageContext, sess);
%>
<%= control.getJSONResponse() %>