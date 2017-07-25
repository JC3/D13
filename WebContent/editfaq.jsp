<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.*" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);

if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canEditFAQ())
    return;
%>
<!DOCTYPE html>
<html>
<head>
<dis:common require="jquery" title="Edit FAQ"/>
<style type="text/css">
</style>
<script type="text/javascript">
(function () {
    window.Disorient = window.Disorient || {};
    window.Disorient.FAQ = {
        data: function () { return faqEntries; }
    };
})();
</script>
</head>
<body>
<dis:header/>
<div class="nav"><a href="${pageContext.request.contextPath}/home.jsp">Home</a></div>
<dis:footer/>
</body>
</html>