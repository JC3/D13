<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis"%>
<%@ page import="d13.web.*" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.util.*" %>
<%@ page import="java.util.*" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

ReportController.View rview;
try {
    rview = new ReportController.View(pageContext, sess);
} catch (Exception x) {
    x.printStackTrace(System.err);
    return;
}

boolean print = false;
if (rview.isMyReportFormat("p")) {
    print = true;
} else if (rview.isMyReportFormat("c")) {
    // ...
    out.println("Sorry, CSV reports aren't finished yet.");
    return;
}

boolean paged = true;

Map<String,List<DataViewer.Column>> reportCols = rview.getReportColumns();

request.setAttribute("rview", rview); // for report.tag
%>
<!DOCTYPE html>
<html>
<% if (print) { %>
<!-- PRINT -->
<head>
<dis:common require="jquery" raw="true"/>
<title>Disorient<%= rview.getMyReportTitle() == null ? "" : (" - " + Util.html(rview.getMyReportTitle())) %></title>
<style type="text/css">
* { font-family: Tahoma, Verdana, sans-serif; }
html, body { margin: 0; padding: 0; }
.report { width: 100%; border-collapse: collapse; }
th { text-align: left; }
td, th { white-space: nowrap; font-size: 90%; padding: 1px 0.5ex; vertical-align: top; }
td.w { white-space: inherit; }
tr.r-section td { background: black; color: white; font-weight: bold; font-size: 110%; padding: 0.5ex; }
th, tr.r-user td { border-bottom: 1px solid #ccc; }
/* todo: these hand-wavey styles still don't always solve pb's after .r-header on chrome at least... */
tr.r-section, tr.r-section *, tr.r-header, tr.r-header * { page-break-after: avoid; }
tr, th, td { page-break-inside: avoid; }
<%   if (paged) { %>
tr.r-spacer, tr.r-spacer * { border:0; margin: 0; padding: 0; }
<%   } %>
</style>
</head>
<body>
<%   if (rview.getMyReportColumns() == null) { %>
Invalid report ID.
<%   } else { %>
<dis:report/>
<%     if (paged) { %>
<script type="text/javascript">
$('tr.r-spacer td').each(function(_,e){
	$(e).empty().html('<div></div><div style="page-break-before:always"></div>');
});
</script>
<%     } %>
<%   } %>
</body>
<!-- END PRINT -->
<% } else { /* !print */ %>
<head>
<dis:common require="jquery tooltipster" nocbhack="true"/>
<style type="text/css">
#report-options {
    align-items: center;
}
#report-options, #report-options div {
    margin: 0;
    padding: 0;
    display: flex;
}
#report-options label {
    display: inline-flex;
    align-items: center;
    white-space: nowrap;
}
#report-options ul, #report-options ul li {
    list-style: none inside none;
    padding: 0;
    margin: 0;
}
#report-run {
    white-space: nowrap;
    width: auto;
}
.vert, .categories > div {
    flex-direction: column;
}
.categories > div, .bottom > div {
    padding: 1ex !important;
}
.categories h1 {
    font-size: 100%;
    margin: 0;
    margin-bottom: 0.5ex;
}
#report-run, #report-opt-filter {
    font-size: 100%;
}
#report-opt-filter {
    max-width: 40ex;
}
.categories label, .report-control-table td {
    font-size: 90%;
}
.cat-profile {
    background: #302020;
}
.cat-registration {
    background: #302820;
}
.cat-survey {
    background: #202830;
}
.misc-opts {
    background: #203028;
}
.control-panel {
    background: #282828;
    flex-grow: 1;
    align-items: center;
    justify-content: center;
}
.report {
    /*background: #101010;
    border: 1px solid #303030;
    padding: 2ex;*/
    font-size: 90%;
    margin: 0 auto;
}
.report td:first-child {
    border-left: 1px solid #202020;
}
.report td {
    vertical-align: top;
    border: 0;
    margin: 0;
    padding: 2px 0.5ex 2px 0.5ex;
    border-right: 1px solid #202020;
    border-bottom: 1px solid #303030;
    white-space: nowrap;
    background: #101010;
}
.report div {
    border: 0;
    margin: 0;
    padding: 0;
}
.report tr.r-spacer {
    visibility: hidden;
}
.report tr.r-section td {
    background: #004080;
    border-top: 1px solid #303030;
    font-weight: bold;
}
.report th {
    white-space: nowrap;
    text-align: left;
    border: 0;
    margin: 0;
    padding: 2px 0.5ex 2px 0.5ex;
    background: #202020;
}
.report tr.r-user td.w {
    white-space: inherit;
}
.report tr.r-user td.w div {
    width: 30ex;
    text-overflow: ellipsis;
    overflow-x: hidden;
    white-space: nowrap;
}
#report-wrapper {
    /*display: flex;
    flex-direction: column;
    align-items: center;*/
}
.report {
    width: 1px;
}
.tooltipster-content {
    font-size: 90%;
}
</style>
<script type="text/javascript">
function getReportOptions () {    
    var options = {
        columns: [],
        filter: $('#report-opt-filter').val(),
        cells: $('input[name="_cells"]:checked').val()
    };
    $('input[data-column-sid]:checked').each(function (_,input) {
        options.columns.push($(input).data('column-sid'));
    });
    return options;
}
$(document).ready(function () {
    $('#report-run').click(function () {
        $.post('${pageContext.request.contextPath}/ajax/save_report.jsp', getReportOptions(), 'json').then(function (r) {
            if (r.error)
                alert('Error: ' + r.error_message);
            else
                window.location = '${pageContext.request.contextPath}/reports.jsp?r=' + r.report;
        }).fail(function (r) {
            alert('A server error occurred: ' + e.status + ' ' + e.statusText);         
        });
    });
    $('td.w div').each(function (_,e) {
        $(e)
           .tooltipster({
               contentAsHTML: true,
               content: $(e).html(),
               arrow: true,
               animationDuration: 200,
               maxWidth: 400
           })
    });
});
</script>
</head>
<body>
<dis:header/>

<div id="report-options" class="vert">
    <div id="report-opts" class="vert" >
        <div class="categories">
<% 
int split = (reportCols.get("Registration").size() + 1) / 2; // kludge; it's the longest one.
for (String category : reportCols.keySet()) { 
    int count = 0;
%>
            <div class="cat-<%= category.toLowerCase() %>"><h1><%= Util.html(category) %></h1><ul>
<%    
    for (DataViewer.Column col : reportCols.get(category)) { 
        boolean checked = rview.isMyReportColumnSelected(col.sid);
        ++ count;
        if (count > split) {
            %></ul></div><div class="cat-<%= category.toLowerCase() %>"><h1>&nbsp;</h1><ul><%
            count = 0;
        }
%>
                    <li><label><input type="checkbox" data-column-sid="<%= Util.html(col.sid) %>"<%= checked ? " checked" : "" %>><%= Util.html(col.name) %></label>
<%
    } 
%>
            </ul></div>
<%
} 
%>
        </div>
        <div class="bottom">
            <div class="misc-opts">
                <table class="report-control-table">
                <tr><td>Who:
                    <td>
                        <select class="dselect" id="report-opt-filter">
                            <% int rf = rview.getMyReportFilter(); %>
                            <option value="0"<%= (rf==0)?" selected":"" %>>All Users</option>
                            <option value="11"<%= (rf==11)?" selected":"" %>>All registered users</option>
                            <option value="1"<%= (rf==1)?" selected":"" %>>Only users that need registration applications reviewed</option>
                            <option value="2"<%= (rf==2)?" selected":"" %>>Only users that need to be approved or rejected</option>
                            <option value="3"<%= (rf==3)?" selected":"" %>>Only users that need to be finalized</option>
                            <option value="4"<%= (rf==4)?" selected":"" %>>Only users that have been approved</option>
                            <option value="5"<%= (rf==5)?" selected":"" %>>Only users that own RVs</option>
                            <option value="6"<%= (rf==6)?" selected":"" %>>Only users that need to pay their dues</option>
                            <option value="7"<%= (rf==7)?" selected":"" %>>Only users that need to complete their approval surveys</option>
                            <option value="8"<%= (rf==8)?" selected":"" %>>All users that need to sign up for more work cells</option>
                            <option value="9"<%= (rf==9)?" selected":"" %>>Only not-yet-approved users that need to sign up for work cells</option>
                            <option value="10"<%= (rf==10)?" selected":"" %>>Only approved users that need to sign up for work cells</option>
                        </select>
                <% int cm = rview.getMyReportCells(); %>
                <tr><td>Cells:
                    <td><label title="Do not include any information about cells."><input type="radio" name="_cells" value="no"<%= cm == ReportTemplate.CELLS_NONE ? " checked" : "" %>> No</label>
                <tr><td>
                    <td><label title="Add a column that lists each cell a user is in."><input type="radio" name="_cells" value="list"<%= cm == ReportTemplate.CELLS_LIST ? " checked" : "" %>> List Cells</label>
                <tr><td>
                    <td><label title="Add a column that lists each non-mandatory cell a user is in."><input type="radio" name="_cells" value="listopt"<%= cm == ReportTemplate.CELLS_LIST_OPT ? " checked" : "" %>> List Non-Mandatory Cells</label>
                <tr><td>
                    <td><label title="Split report by cells, list users in each cell."><input type="radio" name="_cells" value="split"<%= cm == ReportTemplate.CELLS_SPLIT ? " checked" : "" %>> Split Report By Cell</label>   
                </table>
            </div>
            <div class="control-panel">
                <table class="report-control-table">
                <tr><td>Bookmark:
                    <td id="bookmark-here"><%= rview.getLinkHTML(null) %>
                <tr><td>Printable:
                    <td id="bookmark-print"><%= rview.getLinkHTML("p") %>
                <tr><td>CSV:
                    <td id="bookmark-csv"><%= rview.getLinkHTML("c") %>
                <tr><td>
                    <td><button class="dbutton" id="report-run">Generate Report...</button>
                </table>
            </div>
        </div>
    </div>
</div>

<hr>
<% 
if (rview.getMyReportColumns() != null) {
%>
<div id="report-wrapper">
<dis:report/>
</div>
<%
}
%>

<dis:footer/>
</body>
<% } /* if (print) ... else ... */ %>
</html>
