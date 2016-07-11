<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.beans.*" %>
<%@ page import="java.lang.reflect.*" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
String getPrivilegeName (String s) {
    return Introspector.decapitalize(s.replaceFirst("[a-z]*", ""));
}
void writeRoleHeaders (JspWriter out, List<Method> privs) throws IOException {
    for (Method priv : privs) {
        out.append("  <th class=\"rotate\"><div><span>")
           .append(Util.html(getPrivilegeName(priv.getName())))
           .append("</span></div>\n");
    }
}
void writeRoleCells (JspWriter out, List<Method> privs, Role r) throws IOException {
    for (Method priv : privs) {
        String value;
        try {
            value = priv.invoke(r).toString();
            if ("true".equalsIgnoreCase(value))
                value = "X";
            else
                value = "";
        } catch (Exception x) {
            value = "?";
        }
        out.append("  <td>")
           .append(Util.html(value))
           .append("\n");
    }
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canViewAdminData()) {
    return; // permission denied.
}

List<User> users = User.findSpecial();
List<Role> roles = Role.findAll();
List<Method> privs = new ArrayList<Method>();

for (Method m : Role.class.getDeclaredMethods())
    if (m.isAnnotationPresent(Privilege.class))
        privs.add(m);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<link rel="icon" href="favicon.ico">
<style type="text/css">
.summary {
    background: #101010;
    border: 1px solid #303030;
    padding: 0;
    border-top: 0;
    border-right: 0;
    width: 60ex;
    margin: 4ex;
    margin-left: auto;
    margin-right: auto;
}
.summary th {
    white-space: nowrap;
    text-align: left;
    border: 0;
    margin: 0;
    padding: 2px 0.5ex 2px 0.5ex;
    background: #202020;
}
.summary td {
    vertical-align: top;
    border: 0;
    margin: 0;
    padding: 2px 0.5ex 2px 0.5ex;
    border-right: 1px solid #202020;
    border-top: 1px solid #303030;
    white-space: nowrap;
}
th.rotate {
  /* Something you can count on */
  height: 140px;
  white-space: nowrap;
}

th.rotate > div {
  transform: 
    /* Magic Numbers */
    translate(0px, 60px)
    /* 45 is really 360 - 45 */
    rotate(270deg);
  width: 30px;
}
th.rotate > div > span {
  border-bottom: 1px solid #bbb;
  padding: 5px 10px;
}</style>
</head>
<body>
<dis:header/>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<p>This page is hard to read and I don't care.</p>

<table class="summary" cellspacing="0">
<tr>
  <th>ID
  <th>Name
  <th>Role
  <th>Email
  <th>Last Login
<% writeRoleHeaders(out, privs); %>
<% for (User u : users) { %>
<tr>
  <td><%= u.getUserId() %>
  <td><%= Util.html(u.getRealName()) %>
  <td><%= Util.html(u.getRoleDisplay()) %>
  <td><a href="mailto:<%= Util.html(u.getEmail()) %>"><%= Util.html(u.getEmail()) %></a>
  <td><%= Util.html(DefaultDataConverter.objectAsString(u.getLastLogin())) %>
<% writeRoleCells(out, privs, u.getRole()); %>
<% } %>
</table>

<table class="summary" cellspacing="0">
<tr>
  <th>ID
  <th>Name
  <th>Level
<% writeRoleHeaders(out, privs); %>
<% for (Role r : roles) { %>
<tr>
  <td><%= r.getRoleId() %>
  <td><%= Util.html(r.getName()) %>
  <td><%= r.getLevel() %>
<% writeRoleCells(out, privs, r); %>
<% } %>
</table>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<dis:footer/>
</body>
</html>