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

UserStatistics.Statistics stats = UserStatistics.calculateStatistics();
    
String this_url = Util.html(java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
String root = pageContext.getServletContext().getContextPath();

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

boolean show_comments = sess.getUser().getRole().canViewComments();

String qf = request.getParameter("qf");
String sortby = request.getParameter("sortby");
String search = request.getParameter("search");
%>
<%!
String sortLink (String qf, int index, String search) {
    return "view_data.jsp" + makeQuery(qf, Integer.toString(index), search);
}
String makeQuery (String qf, String sortby, String search) {
    StringBuilder query = new StringBuilder("?");
    if (qf != null && qf != "")
        query.append("qf=").append(qf).append("&");
    if (sortby != null && sortby != "")
        query.append("sortby=").append(sortby).append("&");
    if (search != null && search != "")
        try { query.append("search=").append(java.net.URLEncoder.encode(search, "us-ascii")).append("&"); } catch (Exception x) { }
    query.deleteCharAt(query.length() - 1);
    return query.toString();
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="<%=root %>/ext/tooltipster.css" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.0.min.js"></script>
<script type="text/javascript" src="<%=root %>/ext/jquery.tooltipster.min.js"></script>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
/*td { border-bottom: 1px solid black; border-right: 1px solid #e0e0e0; white-space: nowrap; vertical-align: top; }*/
/*th { border-bottom: 1px solid black; border-right: 1px solid #e0e0e0; font-weight: bold; white-space: nowrap; }*/
h1 {
    font-size: larger;
}
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
hr.sub {
    border-top: 1px solid #303030;
}
#stats {
    font-size: smaller;
}
</style>
<script>
$(document).ready(function() {
	$('.tooltip').tooltipster({
	    content: 'Loading...',
	    contentAsHTML: true,
	    functionBefore: function(origin, continueTooltip) {

	        // we'll make this function asynchronous and allow the tooltip to go ahead and show the loading notification while fetching our data
	        continueTooltip();
	        
	        // next, we want to check if our data has already been cached
	        if (origin.data('ajax') !== 'cached') {
	            $.ajax({
	                type: 'POST',
	                data: { 'u': origin.attr('data-uid') },
	                url: '<%=root%>/ajax/query_comments.jsp',
	                success: function(data) {
	                    // update our tooltip content with our returned data and cache it
	                    origin.tooltipster('content', data).data('ajax', 'cached');
	                }
	            });
	        }
	    }
	});
});
</script>
</head>
<body>

<dis:header/>

<div class="nav">
  <a href="home.jsp">Home</a> | <a href="<%=Util.html(csv_link) %>">Download as CSV</a>
</div>

<hr class="sub">

<div style="border:0;margin:0;padding:0;">

<div style="border:0;margin:0;padding:0;float:left;">
<h1>Statistics:</h1>
<table cellspacing="0" class="summary" id="stats">
<tr>
  <th class="standard">
  <th class="standard">Total
  <th class="standard">Return
  <th class="standard">New
  <th class="standard">Virgins
<%!
  void statsRow (JspWriter out, String name, UserStatistics.UserCount s) throws java.io.IOException {
     out.print(String.format("<tr><td class=\"standard\">%s", Util.html(name)));
     out.print(String.format("<td>%d<td>%d<td>%d<td>%d", s.total, s.total - s.disorientVirgins, s.disorientVirgins, s.bmVirgins));
  }
%>
<% statsRow(out, "Registered (including approved)", stats.registered); %>
<% statsRow(out, "Approved", stats.approved); %>
<% statsRow(out, "RVs Registered (including approved)", stats.rvsRegistered); %>
<% statsRow(out, "RVs Approved", stats.rvsApproved); %>
<% statsRow(out, "Need registration review", stats.needReview); %>
<% statsRow(out, "Need admissions review", stats.needAdmitted); %>
<% statsRow(out, "Need admission confirmed", stats.needFinalized); %>
<% statsRow(out, "Need any action", stats.needAction); %>
</table>
</div>

<div style="border:0;margin:0;padding:0;padding-left:4ex;float:left;">
<h1>Filters:</h1>
<ul>
  <li><a href="view_data.jsp<%=makeQuery(null, sortby, null)%>">All users.</a>
  <li><a href="view_data.jsp<%=makeQuery("1", sortby, null)%>">Only users that need registration applications reviewed.</a>
  <li><a href="view_data.jsp<%=makeQuery("2", sortby, null)%>">Only users that need to be approved or rejected.</a>
  <li><a href="view_data.jsp<%=makeQuery("3", sortby, null)%>">Only users that need to be finalized.</a>
  <li><a href="view_data.jsp<%=makeQuery("4", sortby, null)%>">Only users that have been approved.</a>
  <li><a href="view_data.jsp<%=makeQuery("5", sortby, null)%>">Only users that own RVs.</a>
  <li><a href="view_data.jsp<%=makeQuery("6", sortby, null)%>">Only users that need to pay their dues.</a>
  <li><a href="view_data.jsp<%=makeQuery("7", sortby, null)%>">Only users that need to complete their approval surveys.</a>
  <li><a href="view_data.jsp<%=makeQuery("8", sortby, null)%>">All users that need to sign up for more work cells.</a>
  <li><a href="view_data.jsp<%=makeQuery("9", sortby, null)%>">Only not-yet-approved users that need to sign up for work cells.</a>
  <li><a href="view_data.jsp<%=makeQuery("10", sortby, null)%>">Only approved that need to sign up for work cells.</a>
</ul>

<h1>Other Views:</h1>
<ul>
  <li><a href="view_cells2.jsp">View Cells</a>
  <li><a href="view_groups.jsp">View Camper Groups</a>
  <li><a href="view_finance.jsp">View Dues Reports</a>
</ul>
</div>

<div style="border:0;margin:0;padding:0;padding-left:4ex;float:left;width:30ex;">
<h1>Email:</h1>
<textarea class="dtextarea" style="width:100%;" readonly>
<% 
boolean first = true;
for (DataViewer.Row row:rows) {
    if (first)
        first = false;
    else
        out.println(",");
    out.print(Util.html(row.user.getEmail()));
} 
%>
</textarea>
<p style="font-size:smaller;">
This is a list of all email addresses displayed 
in table below, which you can copy and paste 
into your email client.</p>
</div>

<div style="border:0;margin:0;padding:0;clear:both;"></div>

</div>

<hr class="sub">
<h1>Users:</h1>

<div style="width:100%;white-space:nowrap;border:0;margin:0;padding:0;">
<form action="view_data.jsp" method="get">
<% if (sortby != null && sortby != "") { %>
<input type="hidden" name="sortby" value="<%=Util.html(sortby)%>">
<% } %>
Find: <input type="text" name="search" class="dtext" style="width:20ex;" value="<%=Util.html(search)%>"> <input type="submit" value="Search" class="dbutton" style="width:10ex;">
</form></div>

<table cellspacing="0" class="summary">
<tr>

    <th class="standard" colspan="<%=show_comments?6:5%>">Actions
<% for (int n = 0; n < cols.size(); ++ n) { %>
    <th class="<%=cls.get(n)%>"><a href="<%=sortLink(qf, n, search)%>"><%=Util.html(cols.get(n)) %></a>
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
    boolean comments = row.user.hasViewableComments(sess.getUser());
    
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
<% if (show_comments) { %>
    <td class="standard"><div><%if (comments) {%><a href="details.jsp?<%=query%>#comments"><img src="<%=root %>/media/comment.png" class="tooltip" data-uid="<%=row.user.getUserId()%>"></a><%}%></div>
<% } %>
    
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
