<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="d13.notify.BackgroundNotificationManager" %>
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

if (!sess.getUser().getRole().canViewAdminData()) {
    return; // permission denied.
}

List<RuntimeOptions.RuntimeOption> options = new ArrayList<RuntimeOptions.RuntimeOption>(RuntimeOptions.getOptions());
options.add(new RuntimeOptions.RuntimeOption("$CAMP_YEAR", Integer.toString(ThisYear.CAMP_YEAR)));
options.add(new RuntimeOptions.RuntimeOption("$CAMP_NAME", ThisYear.CAMP_NAME));
options.add(new RuntimeOptions.RuntimeOption("$SYSTEM_VERSION", ThisYear.SYSTEM_VERSION));
options.add(new RuntimeOptions.RuntimeOption("$GRACE_PERIOD_DAYS", Integer.toString(ThisYear.GRACE_PERIOD_DAYS)));
options.add(new RuntimeOptions.RuntimeOption("$BNM_POLL_INTERVAL", Integer.toString(BackgroundNotificationManager.POLL_INTERVAL)));
            
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

List<DueCalculator.Tier> pdues = new ArrayList<DueCalculator.Tier>(), rdues = new ArrayList<DueCalculator.Tier>();
ThisYear.setupPersonalTiers(pdues);
ThisYear.setupRVTiers(rdues);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<dis:common require="jquery"/>
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
.long div {
    display: none;
}
#mask {
    position: fixed;
    z-index: 999;
    width: 100%;
    height: 100%;
    border: 0;
    margin: 0;
    padding: 0;
    background: rgba(0,0,0,0.7);
}
#overlay {
    position: fixed;
    z-index: 1000;
    width: 100ex;
    height: 90vh;
    top: 5vh;
    left: calc((100vw - 100ex) / 2);
    padding: 0;
    margin: 0;
    border: 1px solid #909090;
    background: #101010;
    display: flex;
    flex-direction: column;
    font-family: monospace;
}
#overlay div {
    margin: 0;
    padding: 1ex;
}
#overlay-header {
    padding-top: 0.5ex !important;
    padding-bottom: 0.5ex !important;
    border-bottom: 1px dotted #909090;
    background: #505050;
    font-weight: bold;
}
#overlay-content {
    white-space: pre-wrap;
    word-wrap: break-word;
    overflow-x: hidden;
    overflow-y: scroll;
    flex-grow: 1;
}
#overlay-name {
    float: left;
}
#overlay-close {
    float: right;
}
</style>
<script type="text/javascript">
function showOption (name, link) {
	
	let parent = $(link).closest('span');
	let content = parent && parent.children('div');

	if (!content)
		return false;
	
	$('#overlay-name').text(name);
	$('#overlay-content').html(content.html());
	$('#mask').toggle(true);
	
	return false;
	
}

function hideOption () {
	
	$('#mask').toggle(false);
	return false;
	
}

$(window).click(function (e) {

	let t = $(e.target);
	if (t.closest('#overlay').length === 0 && !t.hasClass('no-close-option'))
		hideOption();
	
});
</script>
</head>
<body>
<div id="mask" style="display:none">
	<div id="overlay">
	    <div id="overlay-header"><span id="overlay-name">name</span><a id="overlay-close" href="#" onclick="return hideOption()">CLOSE</a></div>
	    <div id="overlay-content"></div>
	</div>
</div>
<dis:header/>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<table class="summary" cellspacing="0">
<tr>
  <th>Name
  <th>Value
<% for (RuntimeOptions.RuntimeOption o : options) { 
     String value_html;
     if (o.isSecure())
         value_html = "<span class=\"secure\">Hidden</span>";
     else if (o.getValue() != null && o.getValue().length() >= 100)
         value_html = String.format("<span class=\"long\">"
            + "<a class=\"no-close-option\" href=\"#\" onclick=\"return showOption('%s', this)\">Click to view...</a>"
            + "<div>%s</div></span>",
            o.getName(), Util.html(o.getValue()));
     else
         value_html = Util.html(o.getValue());
%>
<tr>
  <td><%= Util.html(o.getName()) %>
  <td><%= value_html %>
<% } %>
</table>

<table class="summary" cellspacing="0">
<tr>
    <th>Due Type
    <th>Name
    <th>Amount
    <th>Deadline
<% for (DueCalculator.Tier t : pdues) { 
    String name_html = Util.html(t.getName()); %>
<tr>
    <td>Personal
    <td><%= name_html %>
    <td><%= Util.intAmountToString(t.getAmount()) %>
    <td><%= DefaultDataConverter.objectAsString(t.getEnd()) %>
<% } %>
<% for (DueCalculator.Tier t : rdues) { 
    String name_html = Util.html(t.getName()); %>
<tr>
    <td>R.V.
    <td><%= name_html %>
    <td><%= Util.intAmountToString(t.getAmount()) %>
    <td><%= DefaultDataConverter.objectAsString(t.getEnd()) %>
<% } %>
</table>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<dis:footer/>
</body>
</html>