<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);

if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canEditAnnouncements())
    return;

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

String error = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_ANNOUNCE_ERROR);
String error_html = (error == null ? null : Util.html(error));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dis:common title="Edit Announcement"/>
<style type="text/css">
.edit-key {
    white-space: nowrap;
    vertical-align: top;
}
</style>
</head>
<body>
<dis:header/>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>
<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<form action="do_editannounce.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<table class="form">

<tr>
    <td colspan="2">Here you can set a message that will be displayed on the top of every page to all logged in users. To display no
    message at all, just leave the text field blank. Please don't write stupid things here. Use it to draw attention to important 
    needs, etc.
<tr>
    <td class="edit-key">Announcement:
    <td><input type="text" class="dtext" name="message" value="<%=Util.html(RuntimeOptions.Global.getLoggedInAnnouncement())%>">
<tr>
    <td colspan="2">Note: The maintenance mode announcement (which people can see even when not logged in) can only be edited by the system administrator.
    Also, the color of this announcement is configurable but Jason did not add a GUI for it, ask him if you want to change the color (maybe a todo for later).
<tr>
    <td colspan="2" style="text-align:center;padding-top:2ex;"><input class="dbutton" type="submit" value="Save">
</table>
</form>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<dis:footer/>
</body>
</html>