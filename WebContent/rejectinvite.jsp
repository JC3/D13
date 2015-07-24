<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
Invite invite = null;
try {
    invite = Invite.findByInviteCode(request.getParameter("code"));
    invite.reject(null);
} catch (Exception x) {
    x.printStackTrace();
    invite = null;
}
%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
</head>
<body>
<dis:header blank="true"/>
<br><br><br><br>

<div class="content">
<% if (invite != null) { %>The invite has been turned down.<% } %> Thanks for responding promptly! Sorry to hear you won't be joining us this year. If you go to the playa, please stop by and visit! Have a good year!
</div>

<dis:footer/>
</body>
</html>