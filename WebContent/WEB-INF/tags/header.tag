<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="blank" required="false" %>
<%@ tag import="d13.web.*" %>
<%@ tag import="d13.dao.*" %>
<%@ tag import="d13.util.*" %>
<%
User user = null;

if (!"1".equals(blank)) {
	SessionData sess = new SessionData(session);
	user = sess.isLoggedIn() ? sess.getUser() : null;
}

String email_html = null;
String realname_html = null;
String role_html = null;

if (user != null) {
    email_html = Util.html(user.getEmail());
    realname_html = Util.html(user.getRealName());
    role_html = Util.html(user.getRoleDisplay());
    if (role_html.isEmpty()) role_html = null;
}
%>
<table class="header" width="100%" cellspacing="0" cellpadding="0">
<tr>
  <td width="100%">
      <img src="media/logo.gif" alt="Disorient Camp Admin">
  </td>
  <td>
      <div class="you">
<% if (user != null) { %>
      You are logged in as:<br>
      Email: <%=email_html %><br>
      Name: <%=realname_html %><br>
      <% if (role_html != null) { %>
      <span class="rolenotify">Role: <%=role_html %></span><br>
      <% } %>
      <a href="do_logout.jsp">Log Out</a>
<% } %>
      </div>
  </td>
</tr>
</table>
<hr>
<div class="contact">If you have any questions, <a href="mailto:camp@disorient.info?subject=Disorent 2013 Registration">contact us</a>.</div>
<br>
