<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

DataViewer view = new DataViewer(pageContext, sess);
if (view.isFailed())
    return; // user should not be here

String this_url = Util.html(java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));

List<String> cols = view.getColumns();
List<String> cls = view.getColumnClasses();
List<DataViewer.Row> rows = view.getRows();

if (view.isDownloadCSV()) {
    response.resetBuffer();
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment;filename=registration.csv");
    view.writeCSV(out);
    return;
}

String csv_link = Util.getCompleteUrl(request);
if (!csv_link.contains("download")) { // hack
    if (csv_link.indexOf('?') == -1)
        csv_link += "?download";
    else
        csv_link += "&download";
}

%>
<%!
String sortLink (int index) {
    return "view_data.jsp?sortby=" + index;
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
/*td { border-bottom: 1px solid black; border-right: 1px solid #e0e0e0; white-space: nowrap; vertical-align: top; }*/
/*th { border-bottom: 1px solid black; border-right: 1px solid #e0e0e0; font-weight: bold; white-space: nowrap; }*/
.summary {
    background: #101010;
    border: 1px solid #303030;
    padding: 2ex;
    width: 60ex;    
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
}
.standard {
    white-space: nowrap;
}
.standard div {
    border: 0;
    margin: 0;
    padding: 0;
}
.wide {
    vertical-align: top;
}
.wide div {
    width: 30ex;
    border: 0;
    margin: 0;
    padding: 0;
}
</style>
</head>
<body>

<dis:header/>
<div class="nav">
  <a href="home.jsp">Home</a> | 
  <a href="<%=Util.html(csv_link) %>">Download as CSV</a>
</div>


<!-- 
<form action="<%=Util.getCompleteUrl(request) %>" method="get">
<table border=1>
<tr><td colspan="3">Filters:
<tr>
<td>User State:<br>
  <input type="checkbox">New User<br>
  <input type="checkbox">Needs Review<br>
  <input type="checkbox">Approved<br>
  <input type="checkbox">Rejected<br>
  <input type="checkbox" checked>ALL<br>
<td>Forms:<br>
  <input type="checkbox">Registration Complete<br>
  <input type="checkbox">Approval Survey Complete<br>
  <input type="checkbox" checked>ALL<br>
<td>Cells:<br>
  <input type="radio" name="fc">No Cells<br>
  <input type="radio" name="fc">Any Cells<br> 
<tr><td colspan="3"><input type="submit" value="Apply">
<tr><td colspan="3"><a href="<%=Util.html(csv_link) %>">Download as CSV</a>
</table>
</form>
-->

<table cellspacing="0" class="summary">
<tr>

    <th class="standard" colspan="5">Actions
<% for (int n = 0; n < cols.size(); ++ n) { %>
    <th class="<%=cls.get(n)%>"><a href="<%=sortLink(n)%>"><%=Util.html(cols.get(n)) %></a>
<% } %>
    <th class="standard" colspan="5">Actions

<% for (DataViewer.Row row:rows) { 

    boolean personal = row.user.isEditableBy2(sess.getUser()) || row.user.isViewableBy2(sess.getUser());
    boolean registration = row.user.isEditableBy2(sess.getUser()) || row.user.isViewableBy2(sess.getUser());
    boolean cells = row.user.isEditableBy2(sess.getUser()) || row.user.isViewableBy2(sess.getUser());
    boolean review =
            (row.user.getState() == UserState.NEEDS_REVIEW && row.user.isReviewableBy2(sess.getUser())) ||
            (row.user.getState() == UserState.REGISTERED && row.user.isApprovableBy2(sess.getUser()));
    boolean dues = row.user.getState() == UserState.APPROVED && sess.getUser().getRole().canEditDues();
    
    String query = ("u=" + row.user.getUserId() + "&next=" + this_url);
    
    /* "details" will be labelled:
        - review: if user is NEEDS_REVIEW and current user can review
        - review: if user is REGISTERED and current user can admit
        - details: in all other cases
     */
%>
<tr>

    <td class="standard"><div><%if (personal) {%><a href="personal.jsp?<%=query%>">Profile</a><%}%></div>
    <td class="standard"><div><%if (registration) {%><a href="registration.jsp?<%=query%>">Registration</a><%}%></div>
    <td class="standard"><div><%if (cells) {%><a href="cells.jsp?<%=query%>">Cells</a><%}%></div>
    <td class="standard"><div><%if (dues) {%><a href="editdues.jsp?<%=query%>">Dues</a><%}%></div>
    <td class="standard"><div><a href="details.jsp?<%=query%>"><%=review?"Review":"Details"%></a></div>
    
    <% for (int n = 0; n < row.values.size(); ++ n) { String str=row.values.get(n); %>
    <td class="<%=cls.get(n)%>"><div>
        <% if (row.hrefs.get(n) != null) { %>
            <a href="<%=Util.html(row.hrefs.get(n))%>"><%=Util.html(str) %></a>
        <% } else { %>
            <%=Util.html(str) %>
        <% } %>
    </div>
    <% } %>
    
    <td class="standard"><div><%if (personal) {%><a href="personal.jsp?<%=query%>">Profile</a><%}%></div>
    <td class="standard"><div><%if (registration) {%><a href="registration.jsp?<%=query%>">Registration</a><%}%></div>
    <td class="standard"><div><%if (cells) {%><a href="cells.jsp?<%=query%>">Cells</a><%}%></div>
    <td class="standard"><div><%if (dues) {%><a href="editdues.jsp?<%=query%>">Dues</a><%}%></div>
    <td class="standard"><div><a href="details.jsp?<%=query%>"><%=review?"Review":"Details"%></a></div>

<% } %>

</table>

<div class="nav">
  <a href="home.jsp">Home</a> | 
  <a href="<%=Util.html(csv_link) %>">Download as CSV</a>
</div>
<dis:footer/>

</body>
</html>
