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
if ("new".equals(request.getParameter("c"))) {
    if (!role.canCreateCells())
        return;
    cell = null;
} else {
	Long cell_id = Util.getParameterLong(request, "c");
	try {
	    cell = Cell.findById(cell_id);
	} catch (Throwable t) {
	    // no such cell. do nothing.
	    return;
	}
	if (cell.getParent() == null) {
	    // root cell, can't edit.
	    return;
	}
}
// cell == null means creating a new cell

boolean isnew = (cell == null);
boolean iscat = !isnew && cell.isCategory();
boolean nodelete = isnew || (iscat && cell.getNonCategoryChildCount() > 0);

StringBuilder catopts_sb = new StringBuilder();
StringBuilder parentopts_sb = new StringBuilder();
for (Cell cat : Cell.findCategories(true)) {
    if (!isnew && (cat.getCellId() == cell.getCellId() || (iscat && cat.isChildOf(cell))))
        continue; // when we're editing categories, don't list itself or any of its children
    if (cat.getParent() != null) {
        boolean ismine = !isnew && (cell.getParent().getCellId() == cat.getCellId());
        catopts_sb.append(String.format("<option value=\"%s\"%s>", cat.getCellId(), ismine ? " selected" : ""));
	    catopts_sb.append(Util.html(cat.getFullName()));
	    catopts_sb.append("</option>");
	    parentopts_sb.append(String.format("<option value=\"%s\">", cat.getCellId()));
	    parentopts_sb.append(Util.html(cat.getFullName()));
	    parentopts_sb.append("</option>");
    } else {
        if (iscat) {
            // when we're editing categories, top level is an option.
            boolean ismine = !isnew && (cell.getParent().getCellId() == cat.getCellId());
            catopts_sb.append(String.format("<option value=\"%s\"%s>", cat.getCellId(), ismine ? " selected" : ""));
            catopts_sb.append("&lt;None&gt;");
            catopts_sb.append("</option>");
        }
        parentopts_sb.append(String.format("<option value=\"%s\" selected>", cat.getCellId()));
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
<dis:common require="jquery"/>
<style type="text/css">
.editcell-key {
    white-space: nowrap;
    vertical-align: top;
}
.edit-note {
    padding-left: 1ex;
    font-size: smaller;
    opacity: 0.8;
}
.centerer {
    display: flex;
    align-items: center;
}
<% if (iscat) { %>
.notcat {
    display: none;
}
.editcell-key {
    min-width: 10ex;
}
<% } %>
</style>
<script type="text/javascript">
$(document).ready(function () {	
	
<% if (role.canCreateCells()) { %>

<%   if (nodelete) { %>
    $('#delete-cell').prop('disabled', true);
    $('#delete-cell').click(() => false);
<%   } else { %>
    $('#delete-cell').click(function () {
<%     if (iscat || cell.getUsers().isEmpty()) { %>
        var doit = confirm('Are you sure you want to permanently delete this <%=iscat?"category":"cell"%>?');
<%     } else { %>
        var code = prompt('This cell contains <%=cell.getUsers().size()%> user(s). Consequences of deleting it are:\n\n' +
        		          '- Users will be removed from it (without knowing).\n' +
        		          '- Some users may be put under their minimum cell requirements.\n' +
        		          '- You CANNOT undo this.\n\n' +
        		          'If you are sure you want to delete this cell, type "i accidentally" in the box below.');
        var doit;
        if (!code) {
        	doit = false;
        } else if (code.trim() === 'i accidentally') {
        	doit = true;
        } else {
        	doit = false;
        	alert('Incorrect phrase entered, cell not deleted.');
        }
<%     } %>
        if (doit) {
	    	$('[name="delete_cell"]').val('1');
	    	$('#theform').submit();
	    	return true;
        } else {
        	return false;
        }
    });
<%   } %>

    $('[name="newcatName"]').outerWidth($('[name="parent"]').outerWidth());
    $('[name="newcatParent"]').outerWidth($('[name="parent"]').outerWidth());

    $('[name="parent"]').change(function () {
        $('.newcat').toggle(Number($(this).val()) === -1);
	});
    
    $('[name="parent"]').trigger('change'); // if cell list is empty we need to initially show new category inputs.
<% } %>
	
	$('[type="submit"]').click(function () {
<% if (role.canCreateCells()) { %>
		let cat = Number($('[name="parent"]').val());
		let catname = $('[name="newcatName"]').val().trim();
		if (cat === -1 && !catname) {
			alert('Either enter a name for the new category, or select an existing category.');
			return false;
		}
<% } %>
        if (!$('[name="name"]').val().trim()) {
        	alert('Please enter a name for the cell.');
        	return false;
        }
		return true;
	});
	
});
</script>
</head>
<body>
<dis:header/>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>
<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<form action="do_editcell.jsp" method="post" id="theform">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="cell_id" value="<%= isnew ? "new" : cell.getCellId() %>">
<% if (role.canCreateCells()) { %>
<input type="hidden" name="delete_cell" value="0">
<% } %>
<table class="form">

<tr>
    <td class="editcell-key">Name:
    <td><input type="text" class="dtext" name="name" style="width:80ex" value="<%=isnew ? "" : Util.html(cell.getName())%>">
<% if (role.canCreateCells()) { %>
<tr>
    <td class="editcell-key">Category:
    <td><span class="centerer"><select class="dselect" name="parent"><%= catopts %></select><span class="edit-note"><% if (!isnew) { %>Current: <%=Util.html(cell.getParent().getParent() == null ? "<None>" : cell.getParent().getFullName()) %><% } %></span></span>
<tr class="newcat" style="display:none">
    <td class="editcell-key">Cat. Name:
    <td><input type="text" class="dtext" name="newcatName">
<tr class="newcat" style="display:none">
    <td class="editcell-key">Cat. Parent:
    <td><select class="dselect" name="newcatParent"><%= parentopts %></select>
<% } %>
<tr class="notcat">
    <td class="editcell-key">Description:
    <td><div style="margin:0;padding:0;display:flex"><textarea class="dtextarea" name="desc" style="width:calc(100% - 8px);height:15ex;"><%=isnew ? "" : Util.html(cell.getDescription()) %></textarea></div>
<tr class="notcat">
    <td class="editcell-key">People Needed:
    <td><span class="centerer"><input type="text" class="dtext" name="people" value="<%=isnew ? 0 : cell.getPeople() %>" style="max-width:10ex"><span class="edit-note">(0 for unlimited)</span></span>
<tr class="notcat">
    <td class="editcell-key">Hide When Full?
    <td><input type="checkbox" name="hideFull" value="1" <%=(!isnew && cell.isHideWhenFull())?"checked":""%>>
<tr class="notcat">
    <td class="editcell-key">Mandatory?
    <td><input type="checkbox" name="mandatory" value="1" <%=(!isnew && cell.isMandatory())?"checked":""%>>
<tr class="notcat">
    <td class="editcell-key">Hidden?
    <td><input type="checkbox" name="hidden" value="1" <%=(!isnew && cell.isHidden())?"checked":""%>>

<tr>
    <td colspan="2" style="text-align:center;padding-top:2ex;"><input class="dbutton" type="submit" value="Save">
<% if (!isnew && role.canCreateCells()) { %>
    <button class="dbutton<%=nodelete?" disabled":""%>" id="delete-cell" <%=nodelete?"title=\"You must delete all cells in this category first.\"":"" %>>Delete <%= iscat ? "Category" : "Cell" %></button>
<% } %>
</table>
</form>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<dis:footer/>
</body>
</html>