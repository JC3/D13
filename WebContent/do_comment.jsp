<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
PostComment post = new PostComment(pageContext, sess);

if (post.isFailed()) {
    System.err.println("ERROR: " + post.getErrorMessage());
}

response.sendRedirect(post.getTarget());
%>