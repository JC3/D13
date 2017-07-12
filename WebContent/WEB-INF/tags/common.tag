<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="title" required="false" %>
<%@ attribute name="require" required="false" %>
<%@ attribute name="nocbhack" required="false" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag import="d13.ThisYear" %>
<%@ tag import="d13.util.Util" %>
<% 
    if (title == null || "".equals(title)) 
        title = "Disorient";
    boolean jquery = false;
    boolean tooltipster = false;
    if (require != null) {
        for (String req : require.split(" ")) {
            if ("jquery".equals(req))
                jquery = true;
            else if ("tooltipster".equals(req))
                tooltipster = true;
        }
    }
    jquery = jquery || tooltipster; // tooltipster requires jquery
    boolean nocbh = (nocbhack != null && Boolean.parseBoolean(nocbhack));
    String vpath = request.getContextPath() + "/v/" + ThisYear.CSS_VERSION;
%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="icon" href="<%=vpath%>/favicon.ico">
<link rel="stylesheet" type="text/css" href="<%=vpath%>/disorient.css">
<% if (tooltipster) { %>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/jquery.tooltipster/4.2.5/css/tooltipster.bundle.min.css">
<!-- https://github.com/iamceege/tooltipster/issues/691: -->
<style type="text/css">div.tooltipster-arrow,div.tooltipster-arrow div{margin:0}</style>
<% } %>
<% if (jquery) { %>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<% } %>
<% if (tooltipster) { %>
<script type="text/javascript" src="https://cdn.jsdelivr.net/jquery.tooltipster/4.2.5/js/tooltipster.bundle.min.js"></script>
<% } %>
<% if (!nocbh) { %>
<!-- browser style hacks -->
<script type="text/javascript">
if (window.chrome) {
	let s = document.createElement('style');
	s.type = 'text/css';
	s.textContent = 'input[type="checkbox"] { margin: 3px 3px 0px 5px; }';
	document.head.appendChild(s);
}
</script>
<% } %>
<title><%= Util.html(title) %></title>