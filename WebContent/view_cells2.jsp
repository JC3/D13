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

List<Cell> cells = new ArrayList<Cell>();
buildCellList(cells, Cell.findRoot());

String this_url = Util.html(java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
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
	if (gotone) {
	    window.location.hash = '#' + id;
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
  <a href="home.jsp">Home</a>
</div>
<!-- ----------------------------------------------------------------- -->

<div id="maindiv">

<table width="100%">

<tr><td id="leftside">
<% for (Cell cell:cells) { %>
<a href="javascript:showDetails('<%=cell.getCellId() %>');"><%=Util.html(cell.getFullName()) %></a><br>
<% } %>

<td id="rightside">
<div id="instructions">
Click a cell on the left to view details.
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

</div>
<% } %>
</table>

</div>

<!-- ----------------------------------------------------------------- -->
<br>
<div class="nav">
  <a href="home.jsp">Home</a>
</div>
<dis:footer/>
</body>
</html>