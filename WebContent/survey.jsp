<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);

User editor = null;
User editee = null;
SurveyBean defaults = (SurveyBean)sess.getAndClearAttribute(SessionData.SA_SURVEY_DEFAULTS);

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
if (defaults == null)
    defaults = new SurveyBean(editee);

String error = (String)sess.getAndClearAttribute(SessionData.SA_SURVEY_ERROR);
String error_html = (error == null ? null : Util.html(error));

// at this point:
//   editor      The current user
//   editee      The user being edited
//   defaults    The values that should be displayed in the form.

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="disorient.css">
<title>Disorient</title>
<script language="JavaScript" type="text/javascript">
function setVisible (id, visible) {
    document.getElementById(id).style.display = (visible ? 'block' : 'none');
}
function isChecked (id) {
    return document.getElementById(id).checked;
}
function updateVisibility () {
    var b = isChecked("tixWanted_1");
    setVisible("numWanted", b);
    b = isChecked("tixForSale_1");
    setVisible("numForSale", b);
}
</script>
</head>
<body onload="updateVisibility();">

<dis:header/>
<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<div class="form">

<p>Hello fellow Pornj Star! We are very excited to share our camp with you this year but we
need a little more information. Please fill out this form so that we can gather all the necessary
information for a smooth and seamless Disorient. We will be sending out emails periodically to
keep camp members updated.</p>
 
<p>We try our best to provide all tent campers with shade to live under, however on the playa, 
the more shade, the better.  Campers should try and bring extra fabric, tarps, etc. to extend
shade and build walls as necessary.  If you are planning to purchase tarps for your personal 
use on playa, please consider getting 10'x10' or 10'x20' orange tarps (<a href="http://www.tents-canopy.com/orange-tarp-10x10.html">http://www.tents-canopy.com/orange-tarp-10x10.html</a>)
as those will work well with the current camp shade structure.</p>
 
<form action="do_editsurvey.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">

<%
List<Question> qs = ApprovalQuestions.getQuestions();
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