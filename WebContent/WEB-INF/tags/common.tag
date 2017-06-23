<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="title" required="false" %>
<%@ attribute name="require" required="false" %>
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
%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="icon" href="favicon.ico">
<link rel="stylesheet" type="text/css" href="disorient.css?v=<%=ThisYear.CSS_VERSION%>">
<% if (tooltipster) { %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ext/tooltipster.css" />
<% } %>
<% if (jquery) { %>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<% } %>
<% if (tooltipster) { %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ext/jquery.tooltipster.min.js"></script>
<% } %>
<title><%= Util.html(title) %></title>
