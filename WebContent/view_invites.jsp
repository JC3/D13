<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

ManageInvites manager = new ManageInvites(pageContext, sess);
if (manager.isFailed()) {
    return;
}

String error = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_ERROR);
String error_html = (error == null ? null : Util.html(error));
String emails = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_EMAILS);
String emails_html = Util.html(emails);
String expires = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_EXPIRES);
String expires_html = Util.html(expires);
String warning = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_WARNING);
String warning_html = warning; //(warning == null ? null : Util.html(warning)); // it's already html
boolean show_actions = sess.getUser().getRole().canInviteUsers();
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
#emailtext {
    width: 40ex;
    height: 24.72ex;
}
.summary {
    background: #101010;
    border: 1px solid #303030;
    padding: 2ex;
    width: 60ex;    
}
.summary th {
    white-space: nowrap;
    text-align: left;
    border: 0;
    margin: 0;
    padding: 2px 0.5ex 2px 0.5ex;
    background: #202020;
}
.summary td {
    vertical-align: top;
    border: 0;
    margin: 0;
    padding: 2px 0.5ex 2px 0.5ex;
    border-right: 1px solid #202020;
    border-top: 1px solid #303030;
}
.standard {
    white-space: nowrap;
}
.standard div {
    border: 0;
    margin: 0;
    padding: 0;
}
table.form td {
    white-space:nowrap;
}
pre {
    border: 0;
    margin: 0;
    padding: 0;
}
</style>
<script type="text/javascript">
function confirmCancel (name) {
	return confirm('Really cancel the invite for ' + name + '? The user has already received an invite email so beware of possible confusion.');
}
</script>
</head>
<body>
<dis:header/>
<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<div style="text-align:center;margin-left:auto;margin-right:auto;">This page is new and rough. Send bug reports and suggestions to Jason.</div>

<% if (error_html != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<% if (warning_html != null) { %>
<div class="notice">Error: <%=warning_html%></div>
<% } %>

<% if (show_actions) { %>
<form action="do_invites.jsp" method="post">
<input type="hidden" name="action" value="invite">
<table class="form" style="margin-bottom:4ex;">
<tr>
    <td>Email Addresses:
    <td><textarea class="dtextarea" id="emailtext" name="emails" placeholder="<%=Util.html("Please enter one or more email addresses, either separated by commas or one per line. Email addresses may have the form \"Full Name <emailaddress>\".") %>"><%= emails_html %></textarea>
<tr>
    <td>Expires In (Days):
    <td><input type="text" class="dtext" name="expires" placeholder="Leave blank for none." value="<%= expires_html %>">
<tr>
    <td colspan="2" style="text-align:center;padding-top:2ex;"><input class="dbutton" type="submit" value="Send Invites">
</table>
</form>
<% } %>

<table cellspacing="0" class="summary" style="margin-bottom:4ex;margin-left:auto;margin-right:auto;">
    <tr>
        <th class="standard">Invitee Email</th>
        <th class="standard">Invitee Name</th>
        <th class="standard">Code</th>
        <th class="standard">Issued</th>
        <th class="standard">Issued By</th>
        <th class="standard">Expires</th>
        <th class="standard">Status</th>
        <th class="standard">Status Changed</th>
        <% if (show_actions) { %>
        <th class="standard">Actions</th>
        <% } %>
    </tr>
    <% for (Invite i : manager.getInvites()) { %>
    <tr>
        <td class="standard"><a href="mailto:<%=Util.html(i.getInviteeEmail())%>"><%=Util.html(i.getInviteeEmail()) %></a></td>
        <td class="standard"><%=Util.html(i.getInviteeName()) %></td>
        <td class="standard"><pre><%=Util.html(i.getInviteCode()) %></pre></td>
        <td class="standard" style="text-align:right"><%=Util.html(DefaultDataConverter.objectAsString(i.getCreatedOn())) %></td>
        <td class="standard"><%=Util.html(DefaultDataConverter.objectAsString(i.getCreatedBy())) %></td>
        <td class="standard" style="text-align:right"><%=Util.html(DefaultDataConverter.objectAsString(i.getExpiresOn())) %></td>
        <td class="standard"><%=ManageInvites.getStatusHtml(i) %></td>
        <td class="standard" style="text-align:right"><%=Util.html(DefaultDataConverter.objectAsString(i.getStatusChanged())) %></td>
        <% if (show_actions) { %>
        <%   if (i.isCancellable(sess.getUser())) { %>
          <td class="standard"><a href="do_invites.jsp?action=cancel&invite=<%= i.getInviteId() %>" onclick="return confirmCancel('<%= Util.html(i.getInviteeEmail()) %>')">Cancel</a></td>
        <%   } else { %>
          <td class="standard"></td>
        <%   } %>
        <% } %>
    </tr>
    <% } %>
</table>

<br>
<div class="nav">
  <a href="home.jsp">Home</a>
</div>
<dis:footer/>
</body>
</html>