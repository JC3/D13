<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
PasswordReset pr = new PasswordReset(pageContext, sess);

sess.clearAttribute(SessionData.SA_LOGIN_MESSAGE);
sess.clearAttribute(SessionData.SA_LOGIN_ERROR);
sess.clearAttribute(SessionData.SA_LOGIN_EMAIL);
sess.clearAttribute(SessionData.SA_LOGIN_EXISTING);
sess.clearAttribute(SessionData.SA_PWRESET_ERROR);

if (pr.getResult() == PasswordReset.RESULT_OK) {
    sess.setAttribute(SessionData.SA_LOGIN_MESSAGE, "Your password has been updated. You may now log in below.");
    sess.setAttribute(SessionData.SA_LOGIN_EMAIL, pr.getEmail());
    sess.setAttribute(SessionData.SA_LOGIN_EXISTING, true);
    response.sendRedirect("index.jsp");
} else if (pr.getResult() == PasswordReset.RESULT_FAILED) {
    sess.setAttribute(SessionData.SA_PWRESET_ERROR, pr.getFailureMessage());
    response.sendRedirect("pwreset.jsp?key=" + pr.getKey());
} else if (pr.getResult() == PasswordReset.RESULT_EXPIRED) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, PasswordReset.EXPIRATION_INDEX_MESSAGE);
    sess.setAttribute(SessionData.SA_LOGIN_EMAIL, pr.getEmail());
    sess.setAttribute(SessionData.SA_LOGIN_EXISTING, true);
    response.sendRedirect("index.jsp");
} else if (pr.getResult() == PasswordReset.RESULT_DENIED) {
    response.sendRedirect("index.jsp");
}
%>
