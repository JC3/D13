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

if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canEditDues())
    return;

User editee;
Long user_id = Util.getParameterLong(request, "u");
try {
    editee = User.findById(user_id);
} catch (Throwable t) {
    // no such user. do nothing.
    return;
}

CustomDuesBean defaults = (CustomDuesBean)sess.getAndClearAttribute(SessionData.SA_EDIT_DUES_DEFAULTS);
if (defaults == null) defaults = new CustomDuesBean(editee);

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

String error = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_DUES_ERROR);
String error_html = (error == null ? null : Util.html(error));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
</head>
<body>
<dis:header/>
<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<form action="do_editdues.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">
<table class="form">
<tr>
    <td colspan="2" style="padding-bottom:2ex;">To set special rates, check the box and enter the rate. To restore to normal
    rates, uncheck the box. To waive dues, set a special rate of 0.</td>
<tr>
    <td>Real Name:
    <td><%=Util.html(editee.getRealName())%>
<tr>
    <td style="padding-bottom:2ex;">Email:
    <td style="padding-bottom:2ex;"><a href="mailto:<%=Util.html(editee.getEmail())%>?subject=Disorient 2013 Dues"><%=Util.html(editee.getEmail())%></a>
<tr>
    <td>Special Camp Rate?
    <td><input type="checkbox" class="dcheckbox" name="personalCustom" value="1" <%="1".equals(defaults.getPersonalCustom())?"checked":""%>>
<tr>
    <td>Special Camp Rate:
    <td><input type="text" class="dtext" name="personalAmount" value="<%=Util.html(defaults.getPersonalAmount())%>">
<tr>
    <td>Special RV Rate?
    <td><input type="checkbox" class="dcheckbox" name="rvCustom" value="1" <%="1".equals(defaults.getRvCustom())?"checked":""%>>
<tr>
    <td>Special RV Rate:
    <td><input type="text" class="dtext" name="rvAmount" value="<%=Util.html(defaults.getRvAmount())%>">
<tr>
    <td>Explanation:
    <td><textarea class="dtextarea" name="explain"><%=Util.html(defaults.getExplain()) %></textarea>
<tr>
    <td colspan="2" style="text-align:center;padding-top:2ex;"><input class="dbutton" type="submit" value="Save">
</table>
</form>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>
<dis:footer/>
</body>
</html>