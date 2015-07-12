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
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect(this.getServletContext().getContextPath() + "/index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
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
th { font-weight: bold; text-align: left; }
.name { white-space: nowrap; }
th.hide { white-space: nowrap; }
</style>
</head>
<body>

<table border="1">
<tr>
    <th class="name">Name</th>
    <th class="people">People</th>
    <th class="hide">Hide when full?</th>
    <th class="desc">Description</th>
</tr>
<% for (Cell c : cells) { %>
<tr>
    <td class="name"><%=Util.html(c.getFullName()) %></td>
    <td class="people"><%=c.getPeople() %></td>
    <td class="hide"><%=c.isHideWhenFull() ? "Yes" : "No" %></td>
    <td class="desc"><%=Util.html(c.getDescription()) %></td>
</tr>
<% } %>
</table>

</body>
</html>