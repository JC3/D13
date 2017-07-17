<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%

// registration.jsp?u=123   Display form for user 123.
// registration.jsp         Display form for current user.
// Parameter 'next' specifies URL to redirect to on success.

SessionData sess = new SessionData(session);

User editor = null;
User editee = null;
//RegistrationBean defaults = (RegistrationBean)sess.getAndClearAttribute(SessionData.SA_REG_DEFAULTS);

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}
Long user_id = Util.getParameterLong(request, "u");
editor = sess.getUser();
try {
    editee = (user_id == null ? editor : User.findById(user_id));
} catch (Throwable t) {
    // no such user. do nothing.
    return;
}
if (!editee.isEditableBy2(editor) && !editee.isViewableBy2(editor))
    return; // permission denied. do nothing.

if (editor.isInviteCodeNeeded()) {
    response.sendRedirect("invite.jsp");
    return;
}

String error = (String)sess.getAndClearAttribute(SessionData.SA_CELL_ERROR);
String error_html = (error == null ? null : Util.html(error));

Cell root = Cell.findRoot();

// at this point:
//   editor      The current user
//   editee      The user being edited
//   defaults    The values that should be displayed in the form.

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dis:common title="Cells"/>
<style type="text/css">
table.form td { vertical-align: top; border-bottom: 1px solid #202020; padding: 0; margin: 0; }
table.form td.bottom { border: 0; padding-top: 1ex; }
.cellname { font-weight: bold; }
.cellvol { font-weight: bold; font-size: smaller; color: #ff8000; }
.celllink a { text-decoration: none; }
.celldesc { display: none; }
.error { color: red; }
td.cellcheck { white-space: nowrap; }
td.cellcheck label { display: inline-flex; align-items: center; }
table.form a:link { color: #00a0ff; }
table.form a:visited { color: #00a0ff; }
table.form a:active { color: #00a0ff; }
.mandatory-cell-left { border-left: 4px solid red; padding-left: 4px !important; position: relative; left: -8px; }
.mandatory-cell-title { background: red; }
</style>
<script language="JavaScript" type="text/javascript">
function setVisible (id, visible) {
    document.getElementById(id).style.display = (visible ? 'block' : 'none');
}
function showMore (id) {
    setVisible('celllink_' + id, false);
    setVisible('celldesc_' + id, true);
    return false;
}
</script>
</head>
<body>

<dis:header/>
<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>


<form action="do_editcells.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">
<table class="form">
<% CellList.writeCellTree(out, root, editee); %>
<% if (editee.isEditableBy2(editor)) { %>
<tr class="section">
    <td colspan="2" style="text-align:center" class="bottom"><input class="dbutton" type="submit" value="Continue">
<% } %>
</table>
</form>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>
<dis:footer/>

</body>
</html>
