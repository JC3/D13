<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn())
    return; // not logged in

long uid;
try {
    uid = Long.parseLong(request.getParameter("u"));
} catch (Throwable t) {
    return; // invalid parameter
}

User viewee;

try {
    viewee = User.findById(uid);
} catch (Throwable t) {
    return; // no such user
}

if (!viewee.hasViewableComments(sess.getUser())) {
    return; // no comments or permission denied
}

List<Comment> comments = viewee.getComments();

for (Comment c : comments) { %>
<div style="margin:0;padding:0;font-size:smaller;font-style:italic;"><%= Util.html(c.getAuthor().getEmail()) %>, <%= DefaultDataConverter.objectAsString(c.getTime()) %>:</div>
<div style="margin:0;padding:0;font-size:smaller;padding-left:2ex;"><%= Util.html(c.getComment()) %></div>
<% } %>