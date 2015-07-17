<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

// todo: move code out of this jsp

if (!sess.getUser().getRole().isSpecial()) {
    return; // permission denied. todo: special privilege for viewing options?
}

List<RuntimeOptions.RuntimeOption> options = new ArrayList<RuntimeOptions.RuntimeOption>(RuntimeOptions.getOptions());
options.add(new RuntimeOptions.RuntimeOption("$CAMP_YEAR", Integer.toString(ThisYear.CAMP_YEAR)));
options.add(new RuntimeOptions.RuntimeOption("$SYSTEM_VERSION", ThisYear.SYSTEM_VERSION));          
            
Collections.sort(options, new Comparator<RuntimeOptions.RuntimeOption>() {
    @Override public int compare (RuntimeOptions.RuntimeOption a, RuntimeOptions.RuntimeOption b) {
        boolean aglobal = (a.getName().indexOf('.') < 0);
        boolean bglobal = (b.getName().indexOf('.') < 0);
        if (aglobal == bglobal)
            return a.getName().compareToIgnoreCase(b.getName());
        else
            return aglobal ? -1 : 1;
    }
});
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
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
.secure {
    color: #505050; /*#bfb2a5;*/
    /*font-style: italic;*/
}
</style>
</head>
<body>
<dis:header/>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<table class="summary" cellspacing="0">
<tr>
  <th>Name
  <th>Value
<% for (RuntimeOptions.RuntimeOption o : options) { 
     String value_html = o.isSecure() ? "<span class=\"secure\">Hidden</span>" : Util.html(o.getValue());
%>
<tr>
  <td><%= Util.html(o.getName()) %>
  <td><%= value_html %>
<% } %>
</table>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<dis:footer/>
</body>
</html>