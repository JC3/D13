<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="org.markdown4j.Markdown4jProcessor" %>
<%@ page import="d13.web.SessionData" %>
<%@ page import="d13.util.Util" %>
<%!
    private static final ThreadLocal<Markdown4jProcessor> mdparser = new ThreadLocal<Markdown4jProcessor>() {
        @Override protected Markdown4jProcessor initialValue () {
            return new Markdown4jProcessor();
        }
    };
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn())
    return; // not logged in

String markdown = request.getParameter("markdown");
%>
<%= mdparser.get().process(markdown == null ? "" : markdown) %>