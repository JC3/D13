<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%

// personal.jsp?newuser Display form for new user.
// personal.jsp?u=123   Display form for user 123.
// personal.jsp         Display form for current user.
// Parameter 'next' specifies URL to redirect to on success.

SessionData sess = new SessionData(session);

User editor = null;
User editee = null;
boolean newuser = request.getParameterMap().containsKey("newuser");
boolean nologin = request.getParameterMap().containsKey("nologin");
UserProfileBean defaults = (UserProfileBean)sess.getAndClearAttribute(SessionData.SA_USER_PROFILE_DEFAULTS);

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

boolean editLogin;

if (newuser) {
    if (RuntimeOptions.Global.isRegistrationClosed())
        return; // shouldn't be here
    editor = null;
    editee = null;
    editLogin = true;
    if (defaults == null) {
        defaults = new UserProfileBean();
        defaults.setEmail((String)sess.getAndClearAttribute(SessionData.SA_LOGIN_EMAIL));
    }
} else {
    if (!sess.isLoggedIn()) {
        sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
        response.sendRedirect("index.jsp");
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
        defaults = new UserProfileBean(editee);
    editLogin = editee.isLoginEditableBy(editor);
}

sess.clearAttribute(SessionData.SA_LOGIN_EMAIL);

String error = (String)sess.getAndClearAttribute(SessionData.SA_USER_PROFILE_ERROR);
String error_html = (error == null ? null : Util.html(error));

// at this point:
//   newuser     True if creating new user, false if editing existing user.
//   nologin     True if new user should not be logged in after creation.
//   editor      The current user, or null if creating new user.
//   editee      The user being edited, or null if creating new user.
//   defaults    The values that should be displayed in the form.
//   locations   List of locations to display in form.
// also:
//   - if editing user, current user is logged in and has edit access to existing user.
//   - SA_USER_PROFILE_DEFAULTS has been cleared.
//   - SA_USER_PROFILE_ERROR has been cleared.
//   - SA_LOGIN_EMAIL has been cleared.

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="disorient.css">
<title>Disorient</title>
</head>
<body>

<dis:header/>
<% if (!newuser) { %><div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div><% } %>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<div class="form">

<form action="do_edituser.jsp" method="post">
<input type="hidden" name="action" value="<%= newuser ? "create" : "update" %>">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(success_target) %>">
<% if (!newuser) { %>
<input type="hidden" name="user_id" value="<%= editee.getUserId() %>">
<% } else if (nologin) { %>
<input type="hidden" name="nologin" value="1">
<% } %>

<%
List<Question> qs = ProfileQuestions.getQuestions();
QuestionForm.writeQuestions(out, qs, defaults, false,  !editLogin);
%>
<% if (editee == null || editee.isEditableBy2(editor)) { %>
<div style="text-align:center"><input class="dbutton" type="submit" value="Continue"></div>
<% } %>
</form>

</div>

<% if (!newuser) { %><div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div><% } %>
<dis:footer/>

</body>
</html>
