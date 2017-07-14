<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
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
RegistrationBean defaults = (RegistrationBean)sess.getAndClearAttribute(SessionData.SA_REG_DEFAULTS);

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "cells.jsp";

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
if (defaults == null)
    defaults = new RegistrationBean(editee);

if (editor.isInviteCodeNeeded()) {
    response.sendRedirect("invite.jsp");
    return;
}
    
String error = (String)sess.getAndClearAttribute(SessionData.SA_REG_ERROR);
String error_html = (error == null ? null : Util.html(error));

// at this point:
//   editor      The current user
//   editee      The user being edited
//   defaults    The values that should be displayed in the form.

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dis:common/>
<style type="text/css">
#arrivalDate select, #departureDate select { width: 100% !important; }
</style>
<script language="JavaScript" type="text/javascript">
function setVisible (id, visible) {
    document.getElementById(id).style.display = (visible ? 'block' : 'none');
}
function isChecked (id) {
	return document.getElementById(id).checked;
}
function setChecked (id, state) {
	document.getElementById(id).checked = state;
}
function updateVisibility () {
    var b = isChecked("disorientVirgin_1");
    setVisible("bmVirgin", b);
    setVisible("sponsor", b);
    setVisible("sponsorFor", !b);
    if (!b) {
        setChecked("bmVirgin_1", false);
        setChecked("bmVirgin_0", true);
    }
    b = isChecked("driving_1");
    setVisible("rideSpaceTo", b);
    setVisible("rideSpaceFrom", b);
    setVisible("parkAtCamp", b);
    setVisible("vehicleComments", b);
    setVisible("haveVehiclePass", b);
    b = isChecked("tixWanted_1");
    setVisible("numWanted", b);
    b = isChecked("tixForSale_1");
    setVisible("numForSale", b);
}
</script>
</head>
<body onload="updateVisibility()">

<dis:header/>
<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<div class="form">

<form action="do_editreg.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">

<%
List<Question> qs = RegistrationQuestions.getQuestions();
QuestionForm.writeQuestions(out, qs, defaults, true);
%>
<% if (editee.isEditableBy2(editor)) { %>
<div style="text-align:center"><input class="dbutton" type="submit" value="Continue"></div>
<% } %>
</form>

</div>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>
<dis:footer/>

</body>
</html>
