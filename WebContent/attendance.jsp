<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis"%>
<%@ page import="d13.util.*" %>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

AttendanceReport view = new AttendanceReport(pageContext, sess);
if (view.isFailed())
    return;
%>
<!DOCTYPE html>
<html>
<head>
<dis:common require="jquery" title="Attendance"/>
<style type="text/css">
.center{display:flex;flex-direction:column;align-items:center;border:0;margin:0;padding:0}
.frame{background:#101010;border:1px solid #303030;margin:0;padding:2ex}
.summary{border-collapse:collapse}
.summary th{white-space:nowrap;margin:0;padding:2px .5ex;border:1px solid #202020;background:#202020;min-width:5ex}
.summary td{white-space:nowrap;margin:0;padding:2px .5ex;border-left:1px solid #202020;border-right:1px solid #202020;border-top:1px solid #303030;border-bottom:1px solid #303030}
#attendance{font-size:90%}
#attendance td{text-align:right}
#attendance tr:nth-child(2) td{font-weight:700}
.blurb{max-width:60ex;text-align:left}
.inithide{display:none}
.p-festival-open{border-left:2px solid #b70!important}
.p-festival-end{border-right:2px solid #b70!important}
/*td.p-festival-open,td.p-festival,td.p-festival-end{background:#201000}*/
td.empty{color:red}
@media print {
body,.frame,#dbody{background:#fff;border:0;margin:0;padding:0}
.center{display:block}
th{color:#fff}
td{color:#000;border:1px solid #888!important;background:initial!important}
.p-festival-open{border-left:3px solid black!important}
.p-festival-end{border-right:3px solid black!important}
tr:nth-child(odd){background:#ddd}
#dheader,#dfooter,.noprint{display:none!important}
}
</style>
<script type="text/javascript">
var curhash = null;
function buildTable (dataset, noselect) { // 0=all, 1=approved, 2=finalized
	var data = <%= view.getEmbeddedData().toString() %>;
    var table = $('#attendance').empty();
    var row = $('<tr/>').appendTo(table);
    const dsnames = [ 'All Users', 'Approval Pending/Finalized', 'Approval Finalized' ];
    const dshash = [ '#all', '#approved', '#finalized' ];
    $('<th/>').text(dsnames[dataset]).appendTo(row);
    for (let c in data.columns1)
    	$('<th/>').html(data.columns1[c] + '<br/>' + data.columns2[c]).addClass('p-' + data.period[c]).appendTo(row);
    for (let r of data.rows) {
        row = $('<tr/>').appendTo(table);
    	$('<td/>').text(r.name).appendTo(row);
    	for (let n in r.data[dataset]) {
    		$('<td/>').text(r.data[dataset][n])
    		          .addClass('p-' + data.period[n])
    		          .addClass(r.data[dataset][n] === 0 ? 'empty' : '')
    		          .attr('title', data.columns1[n] + ' ' + data.columns2[n] + ' ' + r.name)
    		          .appendTo(row);
    	}
    }
    curhash = dshash[dataset];
    window.location.hash = dshash[dataset];
    if (!noselect)
        $('#select-dataset').val(dataset);
    $('.inithide').css('display', 'initial');
}
function buildFromHash () {
	if (window.location.hash === curhash)
		return;
	else if (window.location.hash === '#all')
		buildTable(0);
	else if (window.location.hash === '#approved')
		buildTable(1);
	else if (window.location.hash === '#finalized')
		buildTable(2);
	else
		buildTable(2);
}
window.onhashchange = buildFromHash;
$(document).ready(function () {
	$('#select-dataset').change(function () {
		buildTable(Number($('#select-dataset').val()), true);
	});
});
</script>
</head>
<body>
<dis:header/>
<div class="noprint nav"><a href="home.jsp">Home</a></div>
<div class="center">
  <div class="noprint blurb">These numbers are estimates based on registration forms. 
  The counts <i>include</i> the users' arrival and departure dates. 
  This page is fully printable and the print preview should look OK although you may have 
  to turn on background graphics in your print settings to get row colors.</div>
  <div class="noprint inithide" style="white-space:nowrap">Users: <select id="select-dataset" class="dselect">
  <option value="2">Approval Finalized
  <option value="1">Approval Pending/Finalized
  <option value="0">All Registered Users
  </select></div>
  <div class="frame inithide"><table id="attendance" class="summary"></table><script type="text/javascript">buildFromHash();</script></div>
</div>
<div class="noprint nav" style="padding-top:1ex"><a href="home.jsp">Home</a></div>
<dis:footer/>
</body>
</html>