<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="org.markdown4j.Markdown4jProcessor" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.dao.RuntimeOptions" %>
<%@ page import="d13.util.Util" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
    private static final ThreadLocal<Markdown4jProcessor> mdparser = new ThreadLocal<Markdown4jProcessor>() {
        @Override protected Markdown4jProcessor initialValue () {
            return new Markdown4jProcessor();
        }
    };
%>
<%
SessionData sess = new SessionData(session);
boolean loggedin = sess.isLoggedIn();

//2014-07-10 changed to allow anybody to view
/*if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must log in.");
    response.sendRedirect("index.jsp");
    return;
}*/

boolean cont = "1".equals(request.getParameter("continue"));
boolean accept = "1".equals(request.getParameter("accept"));

if (!loggedin) { // 2014-07-10 added to allow anybody to view
    cont = false;
    accept = false;
}

if (cont) {
    if (accept) {
        sess.getUser().setTermsAgreed(true);
        response.sendRedirect("home.jsp");
    } else {
        response.sendRedirect("sorry.jsp");
    }
    return;
}

String terms_title_html = Util.html(RuntimeOptions.Global.getTermsTitle());
String terms_markdown = RuntimeOptions.Global.getTermsText();
String terms_text_html = mdparser.get().process(terms_markdown == null ? "**An administrator needs to set the terms text!**" : terms_markdown);
String message = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_TERMS_MESSAGE);
String message_html = (message == null ? null : Util.html(message));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dis:common/>
</head>
<body>
<dis:header/>
<% if (loggedin /* 2014-07-10 added to allow anybody to view */ && sess.getUser().isTermsAgreed()) { %>
<div class="nav"><a href="home.jsp">Home</a></div>
<% } %>

<% if (message != null) { %>
<div class="message" style="margin-top:1em"><%=message_html%></div>
<% } %>

<div class="termstext">
  <h1 class="title"><%= terms_title_html %></h1>
<% if (loggedin) { %>
  <div><strong>Welcome to Disorient! Before you register, please read the following information!</strong></div>
<% } %>
  <div><%= terms_text_html %></div>
</div>

<% if (loggedin /* added to support anybody viewing */ && !sess.getUser().isTermsAgreed()) { %>
<form action="terms.jsp" method="get">
<div style="margin-top:2ex;text-align:center;" class="content">
<br>
<input type="hidden" name="continue" value="1">
<input type="checkbox" name="accept" value="1">I have read and understood the rules above!<br><br>
<input class="dbutton" type="submit" value="Continue">
</div>
</form>
<% } else { %>
    <% if (loggedin) { %>
    <div class="nav"><a href="home.jsp">Home</a></div>
    <% } else { %>
    <div class="nav"><a href="index.jsp">Register Now!</a></div>
    <% } %>
<% } %>

<dis:footer/>

</body>
</html>
