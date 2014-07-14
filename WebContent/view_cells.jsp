<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
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
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
h1 {
    font-size: larger;
}
div.cells {
    border: 0;
    margin: 0;
    padding: 0;
}
form.td {
    text-align: top;
    white-space: nowrap;
}
</style>
</head>
<body>
<dis:header/>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<table class="form" style="width:90ex;">

<% for (Cell c:cells) { %>

<tr>
  <td colspan="3" style="border-top:1px solid #202020"><h1><%= Util.html(c.getFullName()) %></h1>

<tr>
  <td>Volunteers Wanted:
  <td><%= c.getPeople() > 0 ? c.getPeople() : "Unspecified" %>
  <td rowspan="3">
    Volunteer List:<br>
    <% for (User u:c.getUsers()) { %>
      <%= Util.html(u.getRealName()) %>, <a href="mailto:<%= Util.html(u.getEmail()) %>?subject=Disorient: <%= Util.html(c.getFullName()) %>"><%= Util.html(u.getEmail()) %></a><br>
    <% } %>
    
<tr>
  <td>Volunteers Signed Up:
  <td><%= c.getUsers().size() %>
<tr>
  <td colspan="2">Email Addresses:<br>
  <textarea style="dtextarea">
<% 
boolean first = true;
for (User u:c.getUsers()) {
    if (first)
        first = false;
    else
        out.print(",");
    out.print(Util.html(u.getEmail())); 
}
%>
  </textarea>
    
<% } %>

</table>

<br>
<div class="nav">
  <a href="home.jsp">Home</a>
</div>


<dis:footer/>
</body>
</html>