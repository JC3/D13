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

if (!sess.getUser().getRole().canEditCells())
    return;

Cell cell;
Long cell_id = Util.getParameterLong(request, "c");
try {
    cell = Cell.findById(cell_id);
} catch (Throwable t) {
    // no such user. do nothing.
    return;
}

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

String error = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_CELL_ERROR);
String error_html = (error == null ? null : Util.html(error));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<link rel="icon" href="favicon.ico">
<style type="text/css">
.editcell-key {
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

<form action="do_editcell.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="cell_id" value="<%= cell.getCellId() %>">
<table class="form">

<tr>
    <td class="editcell-key">Name:
    <td><input type="text" class="dtext" name="name" value="<%=Util.html(cell.getName())%>">
<tr>
    <td class="editcell-key">Description:
    <td><textarea class="dtextarea" name="desc" style="width:80ex;height:15ex;"><%=Util.html(cell.getDescription()) %></textarea>
<tr>
    <td class="editcell-key">People Needed:
    <td><input type="text" class="dtext" name="people" value="<%=cell.getPeople() %>" style="max-width:10ex"> (0 for unlimited)
<tr>
    <td class="editcell-key">Hide When Full?
    <td><input type="checkbox" class="dcheckbox" name="hideFull" value="1" <%=cell.isHideWhenFull()?"checked":""%>>
<tr>
    <td class="editcell-key">Mandatory?
    <td><input type="checkbox" class="dcheckbox" name="mandatory" value="1" <%=cell.isMandatory()?"checked":""%>>
<tr>
    <td class="editcell-key">Hidden?
    <td><input type="checkbox" class="dcheckbox" name="hidden" value="1" <%=cell.isHidden()?"checked":""%>>


<tr>
    <td colspan="2" style="text-align:center;padding-top:2ex;"><input class="dbutton" type="submit" value="Save">
</table>
</form>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<dis:footer/>
</body>
</html>