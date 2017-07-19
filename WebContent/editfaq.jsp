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
    width: 900px;
}
#faq-navigator {
    display: flex;
}
#categories {
    padding-right: 1ex;
    padding-bottom: 1ex;
    margin-right: 1ex;
    border-right: 1px dotted #666;
    flex-basis: 38%;
}
#questions {
    padding-bottom: 1ex;
    flex-basis: 62%;
}
#editor {
    border-top: 1px dotted #666 !important;
    padding-top: 1ex !important;
}
/*#editor table {
    width: 100%;
}
#editor td {
    vertical-align: top;
    white-space: nowrap;
}*/
.panel, .list-items {
    display: flex;
    flex-direction: column;
}
.panel-list {
}
.panel, .panel > div, .list-items > div, #faq-form > div, .input-row, .input-row > div {
    border: 0;
    margin: 0;
    padding: 0;
}
.list-items > div {
    display: flex;
    align-items: center;
    padding: 2px 4px;
    margin: 1px 0 0 0;
    border: 1px solid #666;
    background: #333;
    cursor: pointer;
    /*z-index: 1;*/
}
.list-items > div:hover {
    border: 1px solid #844;
    background: #422;
    color: white;
}
div.title {
    padding-bottom: 0.5ex;
    font-weight: bold;
}
div.actions {
    padding-top: 0.5ex;
}
.btn {
    display: inline-block;
    width: 16px;
    height: 16px;
    border: 0;
    margin: 0 2px 0 0;
    padding: 0;
    cursor: pointer;
    /*z-index: 3 !important;*/
}
/*.btn:hover::after {
    display: block;
    content: '';
    width: 16px;
    height: 16px;
    background: #404040;
    z-index: 2;
    position: absolute;
}*/
.btn:hover {
    filter: brightness(1.5);
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
.input-row {
    display: flex;
    padding-bottom: 4px !important;
}
.input-row > div:first-child {
    flex-basis: 3ex;
    padding-top: 2px;
}
.input-row > div:last-child {
    flex-grow: 1;
    display: flex;
}
.input-row textarea {
    width: 100% !important;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	$('.btn').click(function () {
		/*
		$(this).parent()
		 .animate({opacity:0})
		 .animate({height:0,'margin-top':0,'margin-bottom':0,'padding-top':0,'padding-bottom':0}, function () { $(this).remove(); });
		*/
	    return true;
	});
	$('.btn-delete').attr('title', 'delete item');
	$('.btn-up').attr('title', 'move item up');
	$('.btn-down').attr('title', 'move item down');
});
function newCategory () {
	
}
function newQuestion () {
	
}
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
              <div class="actions"><a href="javascript:newCategory()">New Category...</a></div>
            </div>
            
            <div id="questions" class="panel panel-list">
              <div class="title">Questions:</div>
              <div class="list-items">
                  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>First Question</div>
                  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Second Question</div>
                  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Third Question</div>
                  <div><div class="btn btn-delete"></div><div class="btn btn-up"></div><div class="btn btn-down"></div>Fourth Question</div>
              </div>
              <div class="actions"><a href="javascript:newQuestion()">New Question...</a></div>
            </div>
        
        </div>
        
        <div id="editor" class="panel">
          <div class="title">Edit Question:</div>
          <div class="input-row">
            <div>Q:</div>
            <div><input placeholder="Question" type="text" class="dtext"></div>
          </div>
          <div class="input-row">
            <div>A:</div>
            <div><textarea placeholder="Answer. Markdown formatting is allowed." class="dtextarea"></textarea></div>
          </div>
        </div>
        
    </div>
</div>

<div class="nav"><a href="${pageContext.request.contextPath}/home.jsp">Home</a></div>
<dis:footer/>
</body>
</html>