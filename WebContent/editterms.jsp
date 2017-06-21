<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);

if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canEditTerms())
    return;

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
String save_target = "terms.jsp";
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

String root = pageContext.getServletContext().getContextPath();
String error = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_TERMS_ERROR);
String error_html = (error == null ? null : Util.html(error));

String terms_title = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_TERMS_TITLE);
String terms_text = (String)sess.getAndClearAttribute(SessionData.SA_EDIT_TERMS_TEXT);
boolean initial_modified = (terms_title != null || terms_text != null);
terms_title = terms_title == null ? RuntimeOptions.Global.getTermsTitle() : terms_title;
terms_text = terms_text == null ? RuntimeOptions.Global.getTermsText() : terms_text;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<link rel="icon" href="favicon.ico">
<style type="text/css">
.edit-key {
    white-space: nowrap;
    vertical-align: top;
}
</style>
<dis:jquery/>
<script type="text/javascript">
$(document).ready(function () {
	if (<%=initial_modified%>) {
        window.onbeforeunload = () => true;
	}
	function enableSubmit (enable) {
		let submit = $('input[type="submit"]');
		if (enable)
			submit.prop('disabled', false).removeClass('disabled').attr('title', null);
		else
			submit.prop('disabled', true).addClass('disabled').attr('title', 'You must view the preview before saving.');
	}
	enableSubmit(false);
	$('[name="terms_text"], [name="terms_title"]').keypress(function () {
		enableSubmit(false);
		window.onbeforeunload = () => true;
	});
    $('#preview').click(function () {
    	$.post('<%=root%>/ajax/render_markdown.jsp', { 
    		markdown: $('[name="terms_text"]').val()
    	}).then(function (r) {
    		$('#preview_html')
    		    .empty()
    		    .append($('<h1 class="title"/>').text($('[name="terms_title"]').val()))
    		    .append($('<div/>').html(r));
    	}).fail(function (e) {
    		$('#preview_html').text(`Error rendering markdown: ${JSON.stringify(e)}`);
    	}).always(function () {
    		enableSubmit(true);
    	});
        return false;
    });
    $('[type="submit"]').click(function () {
    	let doit = confirm('Are you sure you want to save the changes? Remember: Editing the terms ' +
    			           'after registration has opened can lead to many unhappy campers.');
    	if (doit)
    		window.onbeforeunload = null;
    	return doit;
    });
});
</script>
</head>
<body>
<dis:header/>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>
<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<form action="do_editterms.jsp" method="post">
<input type="hidden" name="fail_target" value="<%= Util.html(fail_target) %>">
<input type="hidden" name="success_target" value="<%= Util.html(save_target) %>">
<table class="form">

<tr>
    <td colspan="2">Here you can edit the registration terms, which you must format as <a href="http://commonmark.org/help/">Markdown</a>.
        Please be very careful with this, as editing the terms after registration has opened can lead to many unhappy campers.</td>
<tr>
    <td style="white-space:nowrap;vertical-align:top;">Title:
    <td><input type="text" class="dtext" name="terms_title" value="<%=Util.html(terms_title)%>">
<tr>
    <td colspan="2"><textarea name="terms_text" class="dtextarea" style="width:100%;height:40ex;"><%=Util.html(terms_text)%></textarea>
<tr>
    <td colspan="2" style="text-align:center;padding-top:2ex;"><button id="preview" class="dbutton">Preview</button>&nbsp;<input class="dbutton" type="submit" value="Save">
<tr>
    <td colspan="2"><div id="preview_html" class="termstext" style="width:100ex"></div>
</table>
</form>

<div class="nav"><a href="<%=Util.html(success_target) %>">Go Back</a></div>

<dis:footer/>
</body>
</html>