<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect(this.getServletContext().getContextPath() + "/index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

MiscDataExport view = new MiscDataExport(pageContext, sess);
if (view.isFailed())
    return; // user should not be here

if (view.isDownload()) {
    response.resetBuffer();
    response.setContentType(view.getDownloadContentType());
    response.setHeader("Content-Disposition", "attachment;filename=" + view.getDownloadFilename());
    view.toDownload(out);
    return;
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<style type="text/css">
th { font-weight: bold; text-align: left; white-space: nowrap; }
.name { white-space: nowrap; }
th.hide { white-space: nowrap; }
th.mand { white-space: nowrap; }
td.choices { white-space: nowrap; }
td.type { white-space: nowrap; }
td.brief { width: 40ex; }
</style>
</head>
<body>

<p><a href="<%= Util.getCompleteUrl(request) + "&download" %>">Download CSV</a></p>
<p><textarea rows="10" cols="80"><%= view.toDownload() %></textarea></p>
<hr>
<%= view.toHTML() %>

</body>
</html>
