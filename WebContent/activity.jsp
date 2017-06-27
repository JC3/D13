<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.*" %>
<%@ page import="org.joda.time.*" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="dis"%>
<%!
static String getTypeString (Note.Type t) {
    switch (t) {
    case ACTIVITY: return "Review";
    case COMMENT: return "Comment";
    case CELL: return "Cell Edit";
    case PERSONAL_DUE: return "Dues";
    case RV_DUE: return "Dues";
    case REGISTRATION: return "Registered";
    case INVITE: return "Invite";
    case INVITE_CANCEL: return "Invite";
    case TIER_END: return "Tier End";
    case DATA_EDIT: return "Edited";
    case ADMIN_EDIT: return "Admin";
    case DELETE: return "Delete";
    }
    return "?";
}

static String getTargetUrl (Note n, String next) {
    try {
	    User u = n.getTargetUser();
	    Cell c = n.getTargetCell();
	    if (u != null) {
	        return "details.jsp?u=" + u.getUserId() + "&next=" + URLEncoder.encode(next, "us-ascii");
	    } else if (c != null) {
	        return "view_cells2.jsp?next=" + URLEncoder.encode(next, "us-ascii") + "#" + c.getCellId();
	    } else {
	        return null;
	    }
    } catch (Throwable t) {
        return null; // shouldn't happen
    }
}

static String getAuthorUrl (Note n) {
    User u = n.getAuthor();
    if (u != null)
        return "mailto:" + u.getEmail();
    else
        return null;
}

static String makeLink (String text, String url) {
    
    if (text == null)
        return "";
    else if (url == null)
        return Util.html(text);
    else
        return String.format("<a href=\"%s\">%s</a>", Util.html(url), Util.html(text));
    
}

static String dateString (long millis) {
    if (millis == 0)
        return "beginning of time";
    else
        return Util.html(DefaultDataConverter.objectAsString(new DateTime(millis)));
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

User viewer = sess.getUser();
if (!viewer.getRole().isSpecial())
    return;

String success_target = request.getParameter("next");
String back_link_name = "Go Back";
String this_page = Util.getCompleteUrl(request);
if (success_target == null || success_target.trim().isEmpty()) {
    success_target = "home.jsp";
    back_link_name = "Home";
}

List<Note> notes = Note.getFullLog(viewer, Note.DESCENDING);

DefaultDataConverter ddc = new DefaultDataConverter(true);

String this_url = Util.getCompleteUrl(request);

long timestamp_login = sess.getAttributeLong(SessionData.SA_GLOBAL_PREVIOUS_LOGIN);
long timestamp_24 = DateTime.now().minusHours(24).getMillis();
long timestamp_action = 0;

//easiest way to get last action time is from notes at this point
for (Note n : notes) {
    if (n.getAuthor() != null && n.getAuthor().getUserId() == sess.getUserId() && n.getTime() != null) {
        timestamp_action = n.getTime().getMillis();
        break;
    }
}
%>
<!DOCTYPE html>
<html>
<head>
<dis:common require="jquery"/>
<style type="text/css">
table.notes {
    /*border-collapse: collapse;*/
    border-spacing: 0;
    width: 90% !important; 
    max-width: 90% !important;
}
table.notes th {
    white-space: nowrap;
    text-align: left;
    font-size: 90%;
}
table.notes th.header {
    text-align: center;
}
table.notes td {
    vertical-align: top;
    border-top: 1px solid #505050;
    border-right: 1px solid #202020;
    /*
    border-bottom: 1px solid #505050;
    border-left: 1px solid #202020;
    */
    padding-left: 0.5ex;
    padding-right: 0.5ex;
    margin-left: 0;
    margin-right: 0;
    font-size: 90%;
}
table.notes .time {
    white-space: nowrap;
}
table.notes .author {
    white-space: nowrap;
}
table.notes .target {
}
table.notes .target-user {
    white-space: nowrap;
}
table.notes .target-cell {
    white-space: nowrap;
}
table.notes .text {
    border-right: 0;
}
table.notes tr.type-activity td,
table.notes tr.type-activity a:link,
table.notes tr.type-activity a:hover,
table.notes tr.type-activity a:visited,
table.notes tr.type-activity a:active {
    color: #ffff50; /*rgb(161,185,0); /*#ffff50;*/
}
table.notes tr.type-registration td,
table.notes tr.type-registration a:link,
table.notes tr.type-registration a:hover,
table.notes tr.type-registration a:visited,
table.notes tr.type-registration a:active {
    color: #ff9950; /* rgb(255,65,157); /*#ff9950;*/
}
table.notes tr.type-comment td,
table.notes tr.type-comment a:link,
table.notes tr.type-comment a:hover,
table.notes tr.type-comment a:visited,
table.notes tr.type-comment a:active {
    color: #50ffff; /*rgb(51,195,184); /*#50ffff;*/
}
table.notes tr.type-cell td,
table.notes tr.type-cell a:link,
table.notes tr.type-cell a:hover,
table.notes tr.type-cell a:visited,
table.notes tr.type-cell a:active {
    color: #ff5050; /*rgb(46,168,254); /*#ff5050;*/
}
table.notes tr.type-edit td,
table.notes tr.type-edit a:link,
table.notes tr.type-edit a:hover,
table.notes tr.type-edit a:visited,
table.notes tr.type-edit a:active {
    color: #50ff50; /*rgb(131,137,237); /*#50ff50;*/
}
table.notes tr.type-personaldue,
table.notes tr.type-personaldue a:link,
table.notes tr.type-personaldue a:hover,
table.notes tr.type-personaldue a:visited,
table.notes tr.type-personaldue a:active,
tr.type-rvdue td,
table.notes tr.type-rvdue a:link,
table.notes tr.type-rvdue a:hover,
table.notes tr.type-rvdue a:visited,
table.notes tr.type-rvdue a:active {
    color: #ffffff; /*rgb(219,166,0); /*#ffffff;*/
}
table.notes tr.type-invite td,
table.notes tr.type-invite a:link,
table.notes tr.type-invite a:hover,
table.notes tr.type-invite a:visited,
table.notes tr.type-invite a:active {
    color: #ff50ff; /*rgb(245,91,27); /*#ff50ff;*/
}
table.notes tr.type-invitecancel td,
table.notes tr.type-invitecancel a:link,
table.notes tr.type-invitecancel a:hover,
table.notes tr.type-invitecancel a:visited,
table.notes tr.type-invitecancel a:active {
    color: #ff50ff; 
    opacity: 0.9;
}
table.notes tr.type-invitecancel .type {
    text-decoration: line-through;
}
table.notes tr.type-tier td,
table.notes tr.type-tier a:link,
table.notes tr.type-tier a:hover,
table.notes tr.type-tier a:visited,
table.notes tr.type-tier a:active {
    color: white;
    background: #800000;
}
table.notes tr.type-adminedit td,
table.notes tr.type-adminedit a:link,
table.notes tr.type-adminedit a:hover,
table.notes tr.type-adminedit a:visited,
table.notes tr.type-adminedit a:active {
    color: white;
    background: #000080;
}
table.notes tr.type-delete td,
table.notes tr.type-delete a:link,
table.notes tr.type-delete a:hover,
table.notes tr.type-delete a:visited,
table.notes tr.type-delete a:active {
    color: yellow;
    background: #800000;
}
.inithide {
    display: none;
}
</style>
<script type="text/javascript">
function showAfter (link, timestamp, datestr) {
    $('tr.noterow').filter(function() { 
        return $(this).data('timestamp') >= timestamp; 
    }).toggle(true);
    $('tr.noterow').filter(function() { 
        return $(this).data('timestamp') < timestamp; 
    }).toggle(false);
    $('#header').text(datestr);
    window.location.hash = $(link).data('show');
}
$(document).ready(function() {
	var attr = '[data-show="all"]';
	if (window.location.hash.includes('login'))
		attr = '[data-show="login"]';
	else if (window.location.hash.includes('day'))
		attr = '[data-show="day"]';
	else if (window.location.hash.includes('last'))
		attr = '[data-show="last"]';
	$(attr).click();
	$('.inithide').toggle(true);
});
</script>
</head>
<body class="inithide">

<dis:header/>
<div class="nav"><a href="<%=Util.html(success_target) %>"><%= back_link_name %></a></div>

<div style="margin-left:auto;margin-right:auto;text-align:center;white-space:nowrap;">
  <div style="text-align:left;display:inline-block;">
  <input type="radio" name="displaytime" data-show="all" onclick="showAfter(this, 0, '<%= dateString(0) %>');" checked>Show all activity.<br>
  <input type="radio" name="displaytime" data-show="login" onclick="showAfter(this, <%= timestamp_login %>, '<%= dateString(timestamp_login) %>');">Show activity since last log in.<br>
  <input type="radio" name="displaytime" data-show="day" onclick="showAfter(this, <%= timestamp_24 %>, '<%= dateString(timestamp_24) %>');">Show activity in last 24 hours.<br>
  <input type="radio" name="displaytime" data-show="last" onclick="showAfter(this, <%= timestamp_action %>, '<%= dateString(timestamp_action) %>');">Show activity since your last action.<br>
  </div>
  <!-- maybe later, need to make it not conflict with displaytime
  <div style="text-align:left;display:inline-block;">
  <% for (Note.Type t : Note.Type.values()) { %>
  <input class="dcheckbox viewcheck" type="checkbox" onclick="updateView()" data-what="type-<%= t.getName() %>" checked><%= Util.html(getTypeString(t)) %><br>
  <% } %>
  </div>  
  -->
</div>

<table class="form notes">
<tr>
  <th class="header" colspan="5">Activity since <span id="header"></span>:
<tr>
  <th class="time">When
  <th class="target">Who
  <th class="author">By Who
  <th class="type">What
  <th class="text">Details
<% for (Note note : notes) {
    // small hack since we're also displaying invites
    if (note.getType() == Note.Type.COMMENT && note.getText().startsWith("[Invite Comment]"))
        continue;
    String authorName = (note.getAuthor() != null ? note.getAuthor().getRealName() : "");
    String targetName = note.getTargetName();
    String text = note.getText();
    String typestr = getTypeString(note.getType()); 
    long timestamp = note.getTime().getMillis();
    // hack cause i like to keep "by who" admins only, i think it's cleaner looking, so blank
    // out the column for edit activity where a user edited themself.
    if (note.getType() == Note.Type.DATA_EDIT && note.getAuthor().getUserId() == note.getTargetUser().getUserId())
        authorName = "";
    if (note.getType() == Note.Type.DELETE && note.getAuthor().getUserId() == note.getTargetUser().getUserId())
        targetName = "";
    %>
<tr class="noterow type-<%= note.getType().getName() %>" data-timestamp="<%= timestamp %>">
  <td class="time"><%= Util.html(ddc.asString(note.getTime())) %>
  <td class="target <%= note.isCell() ? "target-cell" : "target-user" %>"><%= makeLink(targetName, getTargetUrl(note, this_url)) %>
  <td class="author"><%= makeLink(authorName, getAuthorUrl(note)) %>
  <td class="type"><%= Util.html(typestr) %>
  <td class="text"><%= Util.html(text) %>
<% } %>
</table>

<div class="nav"><a href="<%=Util.html(success_target) %>"><%= back_link_name %></a></div>
<dis:footer/>

</body>
</html>