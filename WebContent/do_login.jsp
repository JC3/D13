<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
Login login = new Login(pageContext, sess);

sess.clearAttribute(SessionData.SA_LOGIN_ERROR);
sess.clearAttribute(SessionData.SA_LOGIN_EMAIL);
sess.clearAttribute(SessionData.SA_LOGIN_EXISTING);

String next = request.getParameter("next");
if (next != null && next.trim().isEmpty()) next = null;

if (login.isFailed()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, login.getErrorMessage());
    sess.setAttribute(SessionData.SA_LOGIN_EMAIL, login.getEmail());
    sess.setAttribute(SessionData.SA_LOGIN_EXISTING, login.isExisting());
    if (next != null)
        response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(next, "us-ascii"));
    else
        response.sendRedirect("index.jsp");
    return;
} else if (login.isLoggedIn()) {
    response.sendRedirect(next == null ? "home.jsp" : next);
} else {
    sess.setAttribute(SessionData.SA_LOGIN_EMAIL, login.getEmail());
    response.sendRedirect("personal.jsp?newuser");
}
%>
