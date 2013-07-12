<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
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
if (!editee.isEditableBy(editor) && !editee.isViewableBy(editor))
    return; // permission denied. do nothing.
if (defaults == null)
    defaults = new RegistrationBean(editee);

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
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
.error { color: red; }
.message { color: #008000; }
</style>
<title>Disorient</title>
</head>
<body>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<form action="do_editreg.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">

<table>
<%
List<Question> qs = RegistrationQuestions.getQuestions();
QuestionForm.writeQuestions(out, qs, defaults);
%>
<% if (editee.isEditableBy(editor)) { %>
<tr><td colspan="2"><input type="submit" value="Continue">
<% } %>
</table>
</form>

<a href="home.jsp">Home</a>

</body>
</html>
