<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
void buildCellList (List<Cell> cs, Cell c) {
    if (c.isCategory()) {
        for (Cell cell:c.getChildren())
            buildCellList(cs, cell);
    } else {
        cs.add(c);
    }
}
void buildUserList (List<User> users, Collection<User> source, UserState state) {
    for (User u:source)
        if (u.getState() == state)
            users.add(u);
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canViewUsers())
    return;

boolean canEdit = sess.getUser().getRole().canEditCells();

List<Cell> cells = new ArrayList<Cell>();
buildCellList(cells, Cell.findRoot());

String this_url = Util.html(java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));

String back_url = request.getParameter("next");
if (back_url == null)
    back_url = "home.jsp";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<link rel="icon" href="favicon.ico">
<style type="text/css">
#maindiv { 
    width:75%;
    margin-left:auto;
    margin-right:auto;
}
#leftside {
    white-space: nowrap; 
    vertical-align:top;
    border-right:1px solid #804000;
    padding-right: 1ex;
    margin-right: 1ex;
}
#rightside { 
    vertical-align:top;
    width:100%;
    padding-left: 1ex;
}
.celldetails {
    display:none;
    vertical-align:top;
    margin:0;
    padding:0;
}
.leftdetail {
    white-space: nowrap; 
    vertical-align:top;
    border-right:1px solid #402000;
    padding-right: 1ex;
    margin-right: 1ex;
}
.rightdetail {
    white-space: nowrap; 
    vertical-align:top;
    width: 100%;
    padding-left: 1ex;
}
#instructions {
    vertical-align:top;
    margin:0;
    padding:0;
}
table.volunteers td {
    text-align:left;
    white-space:nowrap;
    border-bottom:1px solid #603000;
}
table.volunteers th {
    text-align:left;
    white-space:nowrap;
    background:#603000;
}
.indicator {
    font-family:"Consolas", "Monaco", "Courier New", monospace;
    font-size:90%;
    color:#cccccc;
}
td.indicator {
    text-align:center;
    padding-right:2px;
}
.indicator-letter {
    color:#00ccff;
}
.indicator-empty {
    color:red;
}
.indicator-low {
    color:yellow !important;
}
.indicator-full {
    color:#00ff00 !important;
}
.cell-mandatory {
    background:#800000;
}
.cell-hidden {
    color: #bb6000;
}
td.cell-hidden a:link {
    color: #bb6000;
}
td.cell-hidden a:visited {
    color: #bb6000;
}
td.cell-hidden a:active {
    color: #bb6000;
}
.history {
}
.history h1 {
    font-size: medium;
    color: #ff8000;
}
.history .who {
    font-size: small;
    color: #ffeedd;
}
.history .desc {
    font-size: small;
    color: #ffeedd;
    padding-left: 4ex;
}
</style>
<script language="JavaScript" type="text/javascript">
function showDetails (id) {
	var divid = 'details_' + id;
	var divs = document.getElementsByTagName('div');
	var gotone = false;
	for (var i = 0; i < divs.length; ++ i) {
		var obj = divs[i];
		if (obj.className != 'celldetails')
			continue;
		if (obj.id == divid) {
			obj.style.display = 'block';
			gotone = true;
		} else {
			obj.style.display = 'none';
	    }
	}
    window.location.hash = '#' + id;
	if (gotone) {
	    document.getElementById('instructions').style.display = 'none';
	} else {
        document.getElementById('instructions').style.display = 'block';
	}
}
function displayAnchor () {
	var anchor = window.location.hash.substring(1);
	if (anchor.length > 0)
		showDetails(anchor);
}
</script>
</head>
<body onLoad="displayAnchor();">
<dis:header/>
<div class="nav">
  <a href="<%= Util.html(back_url) %>">Go Back</a> | <a href="javascript:showDetails('help');">Help</a>
</div>
<!-- ----------------------------------------------------------------- -->

<div id="maindiv">

<table width="100%">

<tr><td id="leftside">

  <table border="0" cellspacing="0" cellpadding="0">
<% for (Cell cell:cells) { 
    int max = cell.getPeople();
    int tot = cell.getUsers().size();
    String excls = "";
    String nexcls = "";
    if (cell.isFull()) excls += " indicator-full";
    if (cell.getUsers().isEmpty()) 
        excls += " indicator-empty";
    else if (cell.getPeople() > 0 && cell.getUsers().size() <= cell.getPeople() / 2) 
        excls += " indicator-low";
    if (cell.isMandatory()) nexcls += " cell-mandatory";
    if (cell.isReallyHidden()) nexcls += " cell-hidden";
%>
    <tr>
      <td class="indicator indicator-letter"><%= cell.isHidden() ? "H" : (cell.isHideWhenFull() ? "A" : "") %>
      <td class="indicator<%= excls %>"><%= ((max > 0) ? String.format("%2d/%2d", tot, max) : String.format("%2d   ", tot)).replaceAll(" ", "&nbsp;") %>
      <td class="<%= nexcls %>" style="padding-left:4px"><a href="javascript:showDetails('<%=cell.getCellId() %>');"><%=Util.html(cell.getFullName()) %></a><br>
<% } %>
  </table>

<td id="rightside">
<div id="instructions">
<p>Click a cell on the left to view details. The numbers and letters to the left of the cell names mean:</p>
<ul>
<li>An <span class="indicator indicator-letter">H</span> means the cell is set to always be hidden.
<li>An <span class="indicator indicator-letter">A</span> means the cell is set to auto hide when full.
<li>If the cell name is <span class="cell-hidden">dark orange</span> then it is currently hidden as per above rules.
<li>The number of people in the cell and total number of people needed is also shown.
<li>If the number is <span class="indicator-empty">red</span> then the cell is empty.
<li>If the number is <span class="indicator-low">yellow</span> then the cell is &le; half full.
<li>If the number is <span class="indicator-full">green</span> then the cell is full.
<li>If the cell name is <span class="cell-mandatory">highlighted</span> then the cell is mandatory.
</ul>
<p>Cells that are <span class="cell-mandatory">mandatory</span> but also <span class="cell-hidden">hidden</span> are weird and should be avoided.
They won't cause problems for users but do take up space adding warnings to the server logs and are probably a general sign that the cell's rules and
purpose should be reconsidered.</p>
<% if (canEdit) { %>
<p>You can edit cell descriptions and such by clicking a cell name then clicking the edit link in the details.</p>
<% } %>
</div>
<% for (Cell cell:cells) { 
List<User> approved = new ArrayList<User>();
List<User> pending = new ArrayList<User>();
buildUserList(approved, cell.getUsers(), UserState.APPROVED);
java.util.Collections.sort(approved, new User.RealNameComparator());
buildUserList(approved, cell.getUsers(), UserState.APPROVE_PENDING);
buildUserList(pending, cell.getUsers(), UserState.REGISTERED);
java.util.Collections.sort(pending, new User.RealNameComparator());


%>
<div id="details_<%=cell.getCellId()%>" class="celldetails">

<b>Cell:</b> <%= Util.html(cell.getFullName()) %><br>
<b>Volunteers:</b> <%= cell.getUsers().size() + (cell.getPeople() > 0 ? " of " + cell.getPeople() : "") %><br>
<b>Description:</b> <%= Util.html(cell.getDescription()) %><br>
<hr>

<b style="color: #ffddaa;">APPROVAL FINALIZED</b><br><br>

<table width="100%">
<tr><td class="leftdetail">
<b>Email Addresses:</b><br>
  <textarea style="dtextarea">
  <% {
  boolean first = true;
  for (User u:approved) {
      if (first)
          first = false;
      else
          out.print(",");
      out.print(Util.html(u.getEmail())); 
  }
  } %>
  </textarea><br><br>
<td class="rightdetail">

<table width="100%" class="volunteers">
<tr><th>Name<th>Email<th>Arrival<th>Departure
<% for (User u:approved) { %>
<tr><td><a href="details.jsp?u=<%=u.getUserId()%>&next=<%=this_url%>%23<%=cell.getCellId()%>"><%=Util.html(u.getRealName()) %></a>
    <td><a href="mailto:<%=Util.html(u.getEmail())%>"><%=Util.html(u.getEmail()) %></a>
    <% if (u.isRegistrationComplete()) { %>
      <td><%=Util.html(u.getRegistration().getArrivalDate()) %>
      <td><%=Util.html(u.getRegistration().getDepartureDate() + " " + u.getRegistration().getDepartureTime()) %>
    <% } else { %>
      <td><td>
    <% } %>
<% } %>
</table>


</table>

<hr>
<b style="color: #ffddaa;">APPROVED / REGISTERED</b><br><br>

<table width="100%">
<tr><td class="leftdetail">
<b>Email Addresses:</b><br>
  <textarea style="dtextarea">
  <% {
  boolean first = true;
  for (User u:pending) {
      if (first)
          first = false;
      else
          out.print(",");
      out.print(Util.html(u.getEmail())); 
  }
  } %>
  </textarea><br><br>
  
<td class="rightdetail">
<table width="100%" class="volunteers">
<tr><th>Name<th>Email<th>Arrival<th>Departure
<% for (User u:pending) { %>
<tr><td><a href="details.jsp?u=<%=u.getUserId()%>&next=<%=this_url%>%23<%=cell.getCellId()%>"><%=Util.html(u.getRealName()) %></a>
    <td><a href="mailto:<%=Util.html(u.getEmail())%>"><%=Util.html(u.getEmail()) %></a>
    <% if (u.isRegistrationComplete()) { %>
      <td><%=Util.html(u.getRegistration().getArrivalDate()) %>
      <td><%=Util.html(u.getRegistration().getDepartureDate() + " " + u.getRegistration().getDepartureTime()) %>
    <% } else { %>
      <td><td>
    <% } %>
<% } %>
</table>


</table>

<% if (canEdit) {
    List<CellActivityLogEntry> logs = cell.getActivityLog();
    %>
<hr>
<p><a href="editcell.jsp?c=<%= cell.getCellId() %>&next=<%= this_url %>%23<%= cell.getCellId() %>">Edit Cell Details</a></p>
  <% if (logs != null && !logs.isEmpty()) { %>
  <div class="history">
  <h1>Edit History:</h1>
    <%
       DefaultDataConverter ddc = new DefaultDataConverter();
       for (CellActivityLogEntry e:logs) { 
          String datestr = Util.html(ddc.asString(e.getTime()));
          String whostr = Util.html(e.getByWho().getEmail());
          String descstr = Util.html(e.getDescription()); 
    %>
          <div class="who"><%=datestr%>, <a href="mailto:<%=whostr%>"><%=whostr%></a>:</div>
          <div class="desc"><%=descstr%></div>
    <% } %>
  </div>
  <% } %>
<% } %>

</div>
<% } /* end loop over each cell */ %>
</table>

</div>

<!-- ----------------------------------------------------------------- -->
<br>
<div class="nav">
  <a href="<%= Util.html(back_url) %>">Go Back</a> | <a href="javascript:showDetails('help');">Help</a>
</div>
<dis:footer/>
</body>
</html>