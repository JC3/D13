<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.*" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
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
    case TIER_END: return "Tier End";
    }
    return "?";
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
String this_page = Util.getCompleteUrl(request);
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

List<Note> notes = new ArrayList<Note>();
notes.addAll(Note.allUsers(viewer, User.findAll()));
notes.addAll(Note.allCells(viewer, Cell.findAll()));
notes.addAll(Note.allInvites(viewer, Invite.findAll()));
notes.addAll(Note.allTiers());
Collections.sort(notes, Note.DESCENDING);

DefaultDataConverter ddc = new DefaultDataConverter(true);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="disorient.css">
<link rel="icon" href="favicon.ico">
<title>Disorient</title>
<style type="text/css">
table.notes {
    border-collapse: collapse;
    width: 90% !important; 
}
table.notes th {
    white-space: nowrap;
    text-align: left;
    font-size: 90%;
}
table.notes td {
    vertical-align: top;
    border-top: 1px solid #505050;
    border-bottom: 1px solid #505050;
    border-left: 1px solid #202020;
    border-right: 1px solid #202020;
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
}
table.notes .text {
}
table.notes tr.type-activity td {
    color: #ffff00;
}
table.notes tr.type-registration td {
    color: #ff8000;
}
table.notes tr.type-comment td {
    color: #00ffff;
}
table.notes tr.type-cell td {
    color: #ff0000;
}
table.notes tr.type-personaldue,tr.type-rvdue td {
    color: #00ff00;
}
table.notes tr.type-invite td {
    color: #ff00ff;
}
table.notes tr.type-tier td {
    color: white;
    background: #800000;
}
</style>
</head>
<body>

<dis:header/>
<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<div>THIS PAGE IS STILL UNDER CONSTRUCTION. DON'T COMPLAIN ABOUT IT.</div>

<table class="form notes">
<tr>
  <th class="time">Time
  <th class="author">By
  <th class="target">To
  <th class="type">Type
  <th class="text">Description
<% for (Note note : notes) {
    // small hack since we're also displaying invites
    if (note.isComment() && note.getText().startsWith("[Invite Comment]"))
        continue;
    String authorName = (note.getAuthor() != null ? note.getAuthor().getRealName() : "");
    String targetName = note.getTargetName();
    String text = note.getText();
    String typestr = getTypeString(note.getType()); %>
<tr class="type-<%= note.getType().getName() %>">
  <td class="time"><%= Util.html(ddc.asString(note.getTime())) %>
  <td class="author"><%= Util.html(authorName) %>
  <td class="target <%= note.isCell() ? "target-cell" : "target-user" %>"><%= Util.html(targetName == null ? "" : targetName) %>
  <td class="type"><%= Util.html(typestr) %>
  <td class="text"><%= Util.html(text) %>
<% } %>
</table>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>
<dis:footer/>

</body>
</html>