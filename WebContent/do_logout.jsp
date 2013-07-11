<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
sess.logout();
response.sendRedirect("index.jsp?loggedout");
%>
