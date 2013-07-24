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
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<script language="JavaScript" type="text/javascript">
function escapeHTML (v) {
	
	return v;
	
}

function displayData (result) {
	
	var table = document.getElementById("data");
	table.innerHTML = "";
	
    var tr = table.insertRow(-1);	
	for (n = 0; n < result.c.length; ++ n) {
		var td = tr.insertCell(-1);
		td.innerHTML = escapeHTML(result.c[n]);
	}
	
	for (n = 0; n < result.r.length; ++ n) {
		tr = table.insertRow(-1);
		for (j = 0; j < result.r[n].v.length; ++ j) {
			td = tr.insertCell(-1);
			td.innerHTML = escapeHTML(result.r[n].v[j].t);
		}
	}
	
}

function updateData () {
	
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function () {
		if (xhr.readyState == 4 && xhr.status == 200) {
			eval("var result = " + xhr.responseText);	
			displayData(result.result);
		}
	};
	xhr.open("GET", "ajax/query_data.jsp", false);
	xhr.send();
	
}
</script>
</head>
<body onload="setTimeout('updateData()', 250)">
<dis:header/>

<table id="data">
</table>

<dis:footer/>
</body>
</html>