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
.centerer {
    display: flex;
    flex-direction: column;
    align-items: center;
}
#faq-form {
    display: inline-flex;
    flex-direction: column;
}
#faq-navigator {
    display: flex;
}
#categories {
}
#questions {
}
#editor {
}
#editor table {
    width: 100%;
}
#editor td {
    vertical-align: top;
    white-space: nowrap;
}
.panel, .list-items {
    display: flex;
    flex-direction: column;
}
.panel-list {
}
.panel, .panel > div, .list-items > div, #faq-form > div {
    border: 0;
    margin: 0;
    padding: 0;
}
.panel-list .actions {
    padding-left: calc(3 * (16px + 2px) + 4px); /* btns + margins */
}
.list-items > div {
    display: flex;
    align-items: center;
}
.title {
}
.btn {
    display: inline-block;
    width: 16px;
    height: 16px;
    border: 0;
    margin: 0 2px 0 0;
    padding: 0;
    cursor: pointer;
}
.btn:hover::after {
    display: block;
    content: '';
    width: 16px;
    height: 16px;
    background: #404040;
    z-index: -1;
    position: absolute;
}
.btn-delete {
    background-image: url(${pageContext.request.contextPath}/media/delete.png);
}
.btn-up {
    background-image: url(${pageContext.request.contextPath}/media/moveup.png);
}
.btn-down {
    background-image: url(${pageContext.request.contextPath}/media/movedown.png);
    margin-right: 6px;
}
.list-items > div:first-child .btn-up,
.list-items > div:last-child .btn-down {
    visibility: hidden;
}
</style>
<script type="text/javascript">
</script>
</head>
<body>
<dis:header/>
<div class="nav"><a href="${pageContext.request.contextPath}/home.jsp">Home</a></div>

<div class="centerer">
	<div id="faq-form">
	
		<div id="faq-navigator">
		
			<div id="categories" class="panel panel-list">
			  <div class="title">Categories:</div>
			  <div class="list-items">
				  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>First Category</div>
				  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Second Category</div>
				  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Third Category</div>
				  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Fourth Category</div>
			  </div>
			  <div class="actions">New Category...</div>
			</div>
			
			<div id="questions" class="panel panel-list">
			  <div class="title">Questions:</div>
			  <div class="list-items">
				  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>First Question</div>
				  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Second Question</div>
				  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Third Question</div>
				  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Fourth Question</div>
			  </div>
			  <div class="actions">New Question...</div>
			</div>
		
		</div>
		
		<div id="editor" class="panel">
		  <div class="title">Edit Question:</div>
		  <table>
		  <tr><td>Q:<td><input type="text" class="dtext">
		  <tr><td>A:<td><textarea class="dtextarea"></textarea>
		  </table>
		</div>
		
	</div>
</div>

<script type="text/javascript">
$('.btn').click(function () {
	return true;
});
$('.btn-delete').attr('title', 'delete item');
$('.btn-up').attr('title', 'move item up');
$('.btn-down').attr('title', 'move item down');
</script>

<div class="nav"><a href="${pageContext.request.contextPath}/home.jsp">Home</a></div>
<dis:footer/>
</body>
</html>