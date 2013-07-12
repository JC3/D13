<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

DataViewer view = new DataViewer(pageContext, sess, true);
if (view.isFailed())
    return; // user should not be here
    
String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

// there will be only one row in result. cols and row.values will be the same size.
List<String> cols = view.getColumns();
DataViewer.Row row = view.getRows().get(0);
    
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
td { vertical-align: top; }
td.key { white-space: nowrap; font-weight: bold;}
td.value { }
td.left { text-align: right; }
td.wide { text-align: center; }
</style>
<script language="JavaScript" type="text/javascript">
function checkc () {
	var action = document.getElementById("action");
	if (action.options[action.selectedIndex].value == "") {
		alert("Please select 'Approve' or 'Reject'.");
		return false;
	}
	if (!document.getElementById("c1").checked ||
	    !document.getElementById("c2").checked ||
	    !document.getElementById("c3").checked) {
		alert("You must read and check the checkboxes.");
		return false;
	}
	return true;
}
</script>
<title>Disorient</title>
</head>
<body>

<% if (sess.getUser().isAdmin() && view.getSingleUser().getState() == UserState.NEEDS_REVIEW) { %>
<span style="color: red;">This user has just signed up. Please review the form below and click "Reviewed" at the bottom if
the application is in order. If there are issues, you may contact the user to clear them up first. Until this application is
marked as "Reviewed", this user cannot be accepted or rejected!</span>
<% } %>

<table>
<% for (int n = 0; n < cols.size(); ++ n) { %>
<tr><td class="key"><%=Util.html(cols.get(n)) %><td class="value"><%=Util.html(row.values.get(n)) %>
<% } %>
</table>

<% if (sess.getUser().isAdmin() && view.getSingleUser().getState() == UserState.NEEDS_REVIEW) { %>
<form action="do_approval.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= row.userId %>">
<input type="hidden" name="action" value="review">
<input type="submit" value="Reviewed!">
</form>
<% } else if (row.approvable) { %>
<hr>
<b>Approve / Reject User:</b>
<form action="do_approval.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= row.userId %>">
<table>
<tr><td class="left"><input type="checkbox" name="c1" id="c1" value="1"><td>I have thoroughly reviewed the above application and if I approve it, I believe that <%=Util.html(view.getSingleUser().getRealName()) %> is a DOer who will positively contribute to our camp.
<tr><td class="left"><input type="checkbox" name="c2" id="c2" value="1"><td>I understand that I have been trusted with the responsibility to enforce our maximum camper limit so that we can provide <%=Util.html(view.getSingleUser().getRealName()) %> with food, water, power, and a place to live for a week.
<tr><td class="left"><input type="checkbox" name="c3" id="c3" value="1"><td>I understand that once I approve/reject a user, the user will be notified immediately and only an administrator may change their status.
<% if (!view.getSingleUser().isInCells()) { %>
<tr><td><td><span style="color: red;">Warning: This user has not signed up for any volunteer cells!</span>
<% } %>
<tr><td class="left">Action:<td><select name="action" id="action">
    <option value="">--- Select Action ---</option>
    <option value="approve">Approve</option>
    <option value="reject">Reject</option>
</select>
<tr><td class="wide" colspan="2"><input type="submit" value="Apply" onclick="return checkc();">
</table>
</form>
<% } %>

<a href="<%=Util.html(success_target)%>">Go Back</a>

</body>
</html>