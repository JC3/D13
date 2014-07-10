<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="blank" required="false" %>
<%@ attribute name="nav" required="false" %>
<%@ tag import="d13.web.*" %>
<%@ tag import="d13.dao.*" %>
<%@ tag import="d13.util.*" %>
<%@ tag import="java.util.List" %>
<%@ tag import="java.util.ArrayList" %>
<%!
private static class NavButton {
    String text;
    String url;
    NavButton (String text, String url) {
        this.text = text;
        this.url = url;
    }
}
%>
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

PageContext pageContext = (PageContext)getJspContext();
String root = pageContext.getServletContext().getContextPath();

/*List<NavButton> buttons = new ArrayList<NavButton>();

if (nav == null || nav.isEmpty()) {
} else if ("user".equalsIgnoreCase(nav)) {
} else if ("admin".equalsIgnoreCase(nav)) {
}*/

%>
<div id="container">
<div id="dheader">
<table class="header" width="100%" cellspacing="0" cellpadding="0">
<tr>
  <td width="100%">
      <img src="<%=root%>/media/logo.gif" alt="Disorient Registration">
  </td>
  <td>
      <div class="you">
<% if (user != null && !"true".equalsIgnoreCase(blank)) { %>
      You are logged in as:<br>
      Email: <%=email_html %><br>
      Name: <%=realname_html %><br>
      <% if (role_html != null) { %>
      <span class="rolenotify">Role: <%=role_html %></span><br>
      <% } %>
      <a href="<%=root%>/do_logout.jsp">Log Out</a>
<% } %>
      </div>
  </td>
</tr>
</table>
<hr>
<% /*
<!-- 
<div id="navbar">
  <div class="navitem">One</div>
  <div class="navitem">Two</div>
  <div class="navitem">Three</div>
  <div class="navitem">Four</div>
</div>
-->
*/ %>
</div>
<div id="dbody">
