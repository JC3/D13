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

String this_url = Util.html(java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
String root = pageContext.getServletContext().getContextPath();

String error = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_ERROR);
String error_html = (error == null ? null : Util.html(error));
String emails = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_EMAILS);
String emails_html = Util.html(emails);
String expires = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_EXPIRES);
String expires_html = Util.html(expires);
String warning = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_WARNING);
String warning_html = warning; //(warning == null ? null : Util.html(warning)); // it's already html
String comment = (String)sess.getAndClearAttribute(SessionData.SA_MANAGE_INVITES_COMMENT);
String comment_html = Util.html(comment);
boolean show_actions = sess.getUser().getRole().canInviteUsers();
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="<%=root %>/ext/tooltipster.css" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.0.min.js"></script>
<script type="text/javascript" src="<%=root %>/ext/jquery.tooltipster.min.js"></script>
<link rel="stylesheet" type="text/css" href="disorient.css">
<link rel="icon" href="favicon.ico">
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
<% if (show_actions) { %>
<script type="text/javascript">
function confirmCancel (name) {
    return confirm('Really cancel the invite for ' + name + '? The user has already received an invite email so beware of possible confusion.');
}
function updateView () {
    $('.invact').toggle($('#vact').is(':checked'));  
    $('.invacc').toggle($('#vacc').is(':checked'));  
    $('.invrej').toggle($('#vrej').is(':checked'));  
    $('.invexp').toggle($('#vexp').is(':checked'));  
    $('.invcan').toggle($('#vcan').is(':checked'));
    var addrs = '';
    $('.inv:visible > .invemail').each(function() {
    	addrs = addrs + $(this).text() + ', ';
    });
    $('#addrlist').text(addrs);
}
</script>
<% } %>
<script>
$(document).ready(function() {
    $('.tooltip').tooltipster();
    $('#nact').text($('.invact').length);
    $('#nacc').text($('.invacc').length);
    $('#nrej').text($('.invrej').length);
    $('#nexp').text($('.invexp').length);
    $('#ncan').text($('.invcan').length);
    $('#ninv').text($('.inv').length);
    updateView();
});
</script>
</head>
<body>
<dis:header/>
<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<% if (error_html != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<% if (warning_html != null) { %>
<div class="notice">Error: <%=warning_html%></div>
<% } %>

<% if (show_actions) { %>
<table style="margin-left:auto;margin-right:auto;"><tr><td>

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
    <td>Comment:
    <td><input type="text" class="dtext" name="comment" placeholder="Optional comment." value="<%= comment_html %>">
<tr>
    <td colspan="2" style="text-align:center;padding-top:2ex;"><input class="dbutton" type="submit" value="Send Invites">
</table>
</form>

</td><td>
<% } %>

<table cellspacing="0" class="summary" style="margin-bottom:4ex;margin-left:auto;margin-right:auto;">
    <tr><th class="standard">View?</th><th class="standard">Status</th><th class="standard">Count</th></tr>
    <tr><td class="standard"><input class="dcheckbox" type="checkbox" id="vact" onclick="updateView()" checked></td><td class="standard">Active</td>   <td class="standard" id="nact"></td></tr>
    <tr><td class="standard"><input class="dcheckbox" type="checkbox" id="vacc" onclick="updateView()" checked></td><td class="standard">Accepted</td> <td class="standard" id="nacc"></td></tr>
    <tr><td class="standard"><input class="dcheckbox" type="checkbox" id="vrej" onclick="updateView()"></td><td class="standard">Rejected</td> <td class="standard" id="nrej"></td></tr>
    <tr><td class="standard"><input class="dcheckbox" type="checkbox" id="vexp" onclick="updateView()"></td><td class="standard">Expired</td>  <td class="standard" id="nexp"></td></tr>
    <tr><td class="standard"><input class="dcheckbox" type="checkbox" id="vcan" onclick="updateView()"></td><td class="standard">Cancelled</td><td class="standard" id="ncan"></td></tr>
    <tr><td class="standard"></td><td class="standard">Total</td><td class="standard" id="ninv"></td></tr>
    <tr><td class="standard">Emails:</td><td class="standard" colspan="2"><textarea class="dtextarea" id="addrlist"></textarea></td></tr>
</table>

<% if (show_actions) { %>
</td></tr></table>
<% } %>

<table cellspacing="0" class="summary" style="margin-bottom:4ex;margin-left:auto;margin-right:auto;">
    <tr>
        <th class="standard"></th>
        <th class="standard">Invitee Email</th>
        <th class="standard">Invitee Name</th>
        <th class="standard">Code</th>
        <th class="standard">Issued</th>
        <th class="standard">Issued By</th>
        <th class="standard">Expires</th>
        <th class="standard">Status</th>
        <th class="standard">Status Changed</th>
        <th class="standard">User State</th>
        <% if (show_actions) { %>
        <th class="standard">Actions</th>
        <% } %>
    </tr>
    <% for (Invite i : manager.getInvites()) { 
       String icomment = Util.html(i.getComment()).trim();
    %>
    <tr class="inv <%=ManageInvites.getStatusStyleClass(i)%>">
        <td class="standard"><% if (!icomment.isEmpty()) { %><img src="<%=root %>/media/comment.png" class="tooltip" title="<%=Util.html(i.getComment())%>"><% } %></td>
        <td class="standard invemail"><a href="mailto:<%=Util.html(i.getInviteeEmail())%>"><%=Util.html(i.getInviteeEmail()) %></a></td>
        <td class="standard"><%=Util.html(i.getInviteeName()) %></td>
        <td class="standard"><pre><%=Util.html(i.getInviteCode()) %></pre></td>
        <td class="standard" style="text-align:right"><%=Util.html(DefaultDataConverter.objectAsString(i.getCreatedOn())) %></td>
        <td class="standard"><%=Util.html(DefaultDataConverter.objectAsString(i.getCreatedBy())) %></td>
        <td class="standard" style="text-align:right"><%=Util.html(DefaultDataConverter.objectAsString(i.getExpiresOn())) %></td>
        <td class="standard"><%=ManageInvites.getStatusHtml(i, this_url) %></td>
        <td class="standard" style="text-align:right"><%=Util.html(DefaultDataConverter.objectAsString(i.getStatusChanged())) %></td>
        <td class="standard" style="text-align:right"><%=ManageInvites.getUserStatusHtml(i) %></td>
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