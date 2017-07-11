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
    
String this_url = Util.html(java.net.URLEncoder.encode(Util.getCompleteUri(request), "us-ascii"));
String root = pageContext.getServletContext().getContextPath();

List<DataViewer.Column> cols = view.getColumns();
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
<!DOCTYPE html>
<html>
<head>
<dis:common require="jquery tooltipster"/>
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
    white-space: nowrap;
}
.summary td div {
    border: 0;
    margin: 0;
    padding: 0;
}
.w {
    white-space: inherit !important;
}
th.w {
    white-space: nowrap !important;
}
.w div {
    width: 30ex;
}
hr.sub {
    border-top: 1px solid #303030;
}
#stats {
    font-size: smaller;
}
#summary-table, #summary-loading {
    margin-top: 1ex;
    margin-bottom: 1ex;
}
#summary-loading {
    color: #ffeedd;
    opacity: 0.8;
}
#summary-table {
    font-size: 90%;
}
#options > label {
    display: flex;
    align-items: center;
    font-size: 90%;
}
.hider {
    margin: 0;
    padding: 0;
}
.long-content { display: block; }
.long-hidden > a > div { 
    text-overflow: ellipsis;
    white-space: nowrap; 
    overflow: hidden; 
    width: calc(30ex - 4px);
    opacity: 0.9;
}
.long-hidden { display: none; }
.long-hidden > a:hover, .long-hidden > a:visited, .long-hidden > a:active, .long-hidden > a:link {
    /*color: #ffeedd;*/
}
.hide-long .long-content { display: none !important; }
.hide-long .long-hidden { display: block !important; }
.hide-cells .cell-column { display: none !important; }
</style>
<script type="text/javascript">
var wideIndices = <%= view.getWideColumnIndices().toString() %>;
var cellIndices = <%= view.getCellColumnIndices().toString() %>;
$(document).ready(function() {
    updateHideLong(sessionStorage.getItem('jsp.view_data.opt-hidelong') === '1');
    updateHideCells(sessionStorage.getItem('jsp.view_data.opt-hidecells') === '1');
    (function () {
        for (var i in wideIndices) {
            var d = wideIndices[i] + 1 + <%=show_comments?6:5%>;
            $('#summary-table td:nth-child('+d+')').each(function (_,td) {
            	var content = $(td).html();
            	if (content.trim() !== '')
	            	$(td)
	            	   .empty()
	            	   .append($('<div class="hider long-content"/>').html(content))
	            	   .append($('<div class="hider long-hidden"/>')
	            		    .append($('<a href="#" class="show-content"/>').html(content)));
            });
        }
        for (var i in cellIndices) {
            var h = cellIndices[i] + 2;
            var d = cellIndices[i] + 1 + <%=show_comments?6:5%>;
            $('#summary-table td:nth-child('+d+'), #summary-table th:nth-child('+h+')').each(function (_,t) {
            	$(t).addClass('cell-column');
            });
        }
    })();
    $('.show-content').click(function () {
    	var content = $(this).closest('td').find('.long-content > div').html();
    	console.log(content);
        return false;
    });
    $('#summary-loading').toggle(false);
    $('#summary-table').attr('style', null);
    $('.tooltip').tooltipster({
        content: 'Loading...',
        contentAsHTML: true,
        maxWidth: 400,
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
    $('#opt-hidelong').click(function () {
    	var checked = $(this).prop('checked');
    	sessionStorage.setItem('jsp.view_data.opt-hidelong', checked ? '1' : '0');
    	updateHideLong(checked);
    }).prop('checked', sessionStorage.getItem('jsp.view_data.opt-hidelong') === '1');
    $('#opt-hidecells').click(function () {
        var checked = $(this).prop('checked');
        sessionStorage.setItem('jsp.view_data.opt-hidecells', checked ? '1' : '0');    	
        updateHideCells(checked);
    }).prop('checked', sessionStorage.getItem('jsp.view_data.opt-hidecells') === '1');
    function updateHideLong (hide) {
    	if (hide)
    		$('#summary-table').addClass('hide-long');
    	else
    		$('#summary-table').removeClass('hide-long');
    }
    function updateHideCells (hide) {
        if (hide)
            $('#summary-table').addClass('hide-cells');
        else
            $('#summary-table').removeClass('hide-cells');
    }
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
<table class="summary" id="stats">
<tr>
  <th>
  <th>Total
  <th>Return
  <th>New
  <th>Virgins
<%!
  void statsRow (JspWriter out, String name, UserStatistics.UserCount s) throws java.io.IOException {
     out.print(String.format("<tr><td>%s", Util.html(name)));
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
  <li><a href="view_data.jsp<%=makeQuery("11", sortby, null)%>">All registered users.</a>
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
  <!-- <li><a href="view_groups.jsp">View Camper Groups</a>  (removed for 2016 when group leader went away)  -->
  <li><a href="view_finance.jsp">View Dues Reports</a>
</ul>
</div>

<div style="border:0;margin:0;padding:0;padding-left:4ex;float:left;width:30ex;">
<div style="margin:0;padding:0;">
<h1>Email:</h1>
<textarea class="dtextarea" style="width:100%;" readonly>
<% 
boolean first = true;
for (DataViewer.Row row:rows) {
    if (first)
        first = false;
    else
        out.println(", ");
    out.print(Util.html(row.user.getEmail()));
} 
%>
</textarea>
<p style="font-size:smaller;">
This is a list of all email addresses displayed 
in table below, which you can copy and paste 
into your email client.</p>
</div>
<div style="margin:0;padding:0;" id="options">
<h1>Options:</h1>
<label><input type="checkbox" class="dcheckbox" id="opt-hidelong">Hide long data fields.</label>
<label><input type="checkbox" class="dcheckbox" id="opt-hidecells">Hide cell columns.</label>
</div>
</div>

<div style="border:0;margin:0;padding:0;clear:both;"></div>

</div>

<hr class="sub">
<h1>Users (<%= rows.size() %> found):</h1>

<div style="width:100%;white-space:nowrap;border:0;margin:0;padding:0;">
<form action="view_data.jsp" method="get">
<% if (sortby != null && sortby != "") { %>
<input type="hidden" name="sortby" value="<%=Util.html(sortby)%>">
<% } %>
Find: <input type="text" name="search" class="dtext" style="width:20ex;" value="<%=Util.html(search)%>"> <input type="submit" value="Search" class="dbutton" style="width:10ex;">
</form></div>

<span id="summary-loading">Loading...</span>

<table class="summary" id="summary-table" style="display:none">
<tr>

    <th colspan="<%=show_comments?6:5%>">Actions<%
  for (int n = 0; n < cols.size(); ++ n) { 
      String colClass = cols.get(n).shortClass;
      String colName = cols.get(n).name;
    %><th<% if (colClass != null) { %> class="<%=colClass%>"<%}%>><a href="<%=sortLink(qf, n, search)%>"><%=Util.html(colName) %></a><%
  } 
    %><th colspan="5">Actions

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
<tr><td><%if (personal) {%><div><a href="personal.jsp?<%=query%>">Profile</a></div><%}
%><td><%if (registration) {%><div><a href="registration.jsp?<%=query%>">Registration</a></div><%}
%><td><%if (cells) {%><div><a href="cells.jsp?<%=query%>">Cells</a></div><%}
%><td><%if (dues) {%><div><a href="editdues.jsp?<%=query%>">Dues</a></div><%}
%><td><div><a href="details.jsp?<%=query%>"><%=review?"Review":"Details"%></a></div><%
   if (show_comments) { 
%><td><div><%if (comments) {%><a href="details.jsp?<%=query%>#comments"><img src="<%=root %>/media/comment.png" class="tooltip" data-uid="<%=row.user.getUserId()%>"></a><%}%></div><%
   }
   for (int n = 0; n < row.values.size(); ++ n) { 
     String str = row.values.get(n);
     String cls = cols.get(n).shortClass;
     if (str.isEmpty()) {
        %><td><%
     } else if (row.hrefs.get(n) != null) {
        %><td<% if (cls != null) { %> class="<%=cls%>"<%}%>><div><a href="<%=Util.html(row.hrefs.get(n))%>"><%=Util.html(str) %></a></div><%
     } else {
        %><td<% if (cls != null) { %> class="<%=cls%>"<%}%>><div><%=Util.html(str) %></div><%
     }
   } 
%><td><%if (personal) {%><div><a href="personal.jsp?<%=query%>">Profile</a></div><%}
%><td><%if (registration) {%><div><a href="registration.jsp?<%=query%>">Registration</a></div><%}
%><td><%if (cells) {%><div><a href="cells.jsp?<%=query%>">Cells</a></div><%}
%><td><%if (dues) {%><div><a href="editdues.jsp?<%=query%>">Dues</a></div><%}
%><td><div><a href="details.jsp?<%=query%>"><%=review?"Review":"Details"%></a></div><%
} %>

</table>

<div class="nav">
  <a href="home.jsp">Home</a> | 
  <a href="<%=Util.html(csv_link) %>">Download as CSV</a>
</div>
<dis:footer/>

</body>
</html>
