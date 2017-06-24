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

Role role = sess.getUser().getRole();
if (!role.canEditCells())
    return;

Cell cell;
Long cell_id = Util.getParameterLong(request, "c");
try {
    cell = Cell.findById(cell_id);
} catch (Throwable t) {
    // no such cell. do nothing.
    return;
}

StringBuilder catopts_sb = new StringBuilder();
StringBuilder parentopts_sb = new StringBuilder();
for (Cell cat : Cell.findCategories(true)) {
    if (cat.getParent() != null) {
        catopts_sb.append(String.format("<option value=\"%s\"%s>", cat.getCellId(), cell.getParent().getCellId() == cat.getCellId() ? " selected" : ""));
	    catopts_sb.append(Util.html(cat.getFullName()));
	    catopts_sb.append("</option>");
	    parentopts_sb.append(String.format("<option value=\"%s\">", cat.getCellId()));
	    parentopts_sb.append(Util.html(cat.getFullName()));
	    parentopts_sb.append("</option>");
    } else {
        parentopts_sb.append(String.format("<option value=\"%s\"%s selected>", cat.getCellId(), cell.getParent().getCellId() == cat.getCellId() ? " selected" : ""));
        parentopts_sb.append("&lt;None&gt;");
        parentopts_sb.append("</option>");
    }
}
catopts_sb.append("<option value=\"-1\">&lt;New Category...&gt;</option>");
String catopts = catopts_sb.toString();
String parentopts = parentopts_sb.toString();

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

String error = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_CELL_ERROR);
String error_html = (error == null ? null : Util.html(error));
%>
<!DOCTYPE html>
<html>
<head>
<% if (role.canCreateCells()) { %>
<dis:common require="jquery"/>
<% } else { %>
<dis:common/>
<% } %>
<style type="text/css">
.editcell-key {
    white-space: nowrap;
    vertical-align: top;
}
</style>
<% if (role.canCreateCells()) { %>
<script type="text/javascript">
$(document).ready(function () {	
	
	$('[name="newcatName"]').outerWidth($('[name="parent"]').outerWidth());

	$('[name="parent"]').change(function () {
        $('.newcat').toggle(Number($(this).val()) === -1);
	});
	
	$('[type="submit"]').click(function () {
		let cat = Number($('[name="parent"]').val());
		let catname = $('[name="newcatName"]').val().trim();
		if (cat === -1 && !catname) {
			alert('Either enter a name for the new category, or select an existing category.');
			return false;
		} else {
			return true;
		}
	});
	
});
</script>
<% } %>
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
    <td><input type="text" class="dtext" name="name" style="width:80ex" value="<%=Util.html(cell.getName())%>">
<% if (role.canCreateCells()) { %>
<tr>
    <td class="editcell-key">Category:
    <td><select class="dselect" name="parent"><%= catopts %></select><span id="newcat-info"></span>
<tr class="newcat" style="display:none">
    <td class="editcell-key">Cat. Name:
    <td><input type="text" class="dtext" name="newcatName" style="width:80ex">
<tr class="newcat" style="display:none">
    <td class="editcell-key">Cat. Parent:
    <td><select class="dselect" name="newcatParent"><%= parentopts %></select>
<% } %>
<tr>
    <td class="editcell-key">Description:
    <td><textarea class="dtextarea" name="desc" style="width:calc(100% - 8px);height:15ex;"><%=Util.html(cell.getDescription()) %></textarea>
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