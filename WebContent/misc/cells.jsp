<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="d13.web.MiscTable" %>
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

MiscTable table = new MiscTable();
table.setDefaultStyles("cat", "name", "people", "hide", "desc");
table.setHeader("Category", "Name", "People", "Hide when full?", "Description");

for (Cell c : cells) {
    MiscTable.Row row = table.addRow();
    row.add(c.getParentName());
    row.add(c.getName());
    row.add(Integer.toString(c.getPeople()));
    row.add(c.isHideWhenFull() ? "Yes" : "No");
    row.add(c.getDescription());
}

if (request.getParameterMap().containsKey("download")) {
    response.resetBuffer();
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment;filename=cells.csv");
    table.setShowHeader(false).toCSV(out);
    return;
}
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

<p><a href="cells.jsp?download">Download CSV</a></p>
<hr>
<%= table.setShowHeader(true).toHTMLTable() %>
<hr>
<textarea rows="10" cols="80"><%= Util.html(table.setShowHeader(false).toCSV()) %></textarea>

</body>
</html>