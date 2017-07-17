<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis"%>
<%@ page import="d13.web.*" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.util.*" %>
<%@ page import="java.util.*" %>
<jsp:useBean id="params" scope="request" class="d13.web.servlets.ReportParameters"/>
<%
SessionData sess = new SessionData(session);
ReportController.View rview;
try {
    rview = new ReportController.View(params, pageContext, sess);
} catch (Exception x) {
    x.printStackTrace(System.err);
    return;
}

if (rview.getMyReportColumns() == null)
    return;

boolean showpage = rview.getMyReportSections().size() > 1;

request.setAttribute("rview", rview); // for report.tag
%>
<!DOCTYPE html>
<html>
<head>
<% if (showpage) { %>
<dis:common require="jquery" raw="true"/>
<% } else { %>
<dis:common raw="true"/>
<% } %>
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
  .paged tr.r-spacer, .paged tr.r-spacer * { border:0; margin: 0; padding: 0; }
  #print-options {
    display: flex;
    justify-content: center;
  }
  #print-options > div {
    border: 1px solid red;
    border-radius: 4px;
    background: #ffaaaa;
    color: #400000;
    display: flex;
    flex-direction: column;
    margin: 2ex;
    padding: 2ex;
    font-size: 95%;
    -webkit-box-shadow: 0px 0px 4px 0px rgba(255,0,0,0.5);
	-moz-box-shadow: 0px 0px 4px 0px rgba(255,0,0,0.5);
	box-shadow: 0px 0px 4px 0px rgba(255,0,0,0.5);
  }
  #print-options label {
    display: flex;
    align-items: center;
  }
  @media print { #print-options { display: none !important; } }
</style>
<% if (showpage) { %>
<script type="text/javascript">
function setPaged (paged) {
	$('tr.r-spacer td').each(function(_,e){
		if (paged)
			$(e).empty().html('<div></div><div style="page-break-before:always"></div>');
		else
			$(e).empty().html('<div>&nbsp;</div>');
	});
	if (paged)
	    $('body').addClass('paged');
	else
		$('body').removeClass('paged');
    //window.location.hash = (paged ? 'paged' : '');
    window.location.replace(paged ? '#paged' : '#');
}
$(document).ready(function () {
	if (window.location.hash === '#paged') {
		$('input[name="_page"][value="yes"]').prop('checked', true);
		setPaged(true);
	} else {
		setPaged(false);
	}
	$('input[name="_page"]').change(function () {
		setPaged($('input[name="_page"]:checked').val() === 'yes');
	});
});
</script>
<% } %>
</head>
<body>
<div id="print-options">
  <div>
<% if (showpage) { %>
	  <span style="margin-bottom:1ex">Select a choice here before printing:</span>
	  <label><input type="radio" name="_page" value="no" checked>Print all sections with no page breaks.</label>
	  <label style="margin-bottom:1ex"><input type="radio" name="_page" value="yes">Start each section on a new page (doesn't work on Firefox).</label>
<% } %>
	  <div>Printing pro-tips:
	  <ul style="margin:1ex 0">
	    <li>In Chrome you can shrink size by changing scale on the print page.
	    <li>Don't forget landscape mode if the report is wide.
	  </ul>
	  </div>
      <a href="<%= rview.getURL(request, null) %>" style="align-self:center">Back to Reports Page</a>
  </div>
</div>
<dis:report/>
</body>
</html>
