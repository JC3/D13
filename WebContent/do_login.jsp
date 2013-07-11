<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
Login login = new Login(pageContext, sess);

sess.clearAttribute(SessionData.SA_LOGIN_ERROR);
sess.clearAttribute(SessionData.SA_LOGIN_EMAIL);
sess.clearAttribute(SessionData.SA_LOGIN_EXISTING);

if (login.isFailed()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, login.getErrorMessage());
    sess.setAttribute(SessionData.SA_LOGIN_EMAIL, login.getEmail());
    sess.setAttribute(SessionData.SA_LOGIN_EXISTING, login.isExisting());
    response.sendRedirect("index.jsp");
    return;
} else if (login.isLoggedIn()) {
    response.sendRedirect("home.jsp");
} else {
    sess.setAttribute(SessionData.SA_LOGIN_EMAIL, login.getEmail());
    response.sendRedirect("personal.jsp?newuser");
}
%>
