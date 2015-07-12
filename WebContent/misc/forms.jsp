<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.IOException"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
void writeRow (JspWriter out, Question q) throws IOException {
    
    if (q.isForLogin())
        return;
 
    StringBuilder sb = new StringBuilder("<tr>");
    
    String typestr;
    switch (q.getType()) {
    case Question.TYPE_DROPLIST: typestr = "Drop List"; break;
    case Question.TYPE_LONG_TEXT: typestr = "Long Text"; break;
    case Question.TYPE_MULTI_CHOICE: typestr = "Multi Choice"; break;
    case Question.TYPE_PASSWORD: typestr = "Password"; break;
    case Question.TYPE_SHORT_TEXT: typestr = "Short Text"; break;
    case Question.TYPE_SINGLE_CHOICE: typestr = "Single Choice"; break;
    case Question.TYPE_USER_DROPLIST: typestr = "User List"; break;
    default: typestr = Integer.toString(q.getType()); break;
    }
    
    List<Question.Choice> choices = q.getChoices();
    
    sb.append("<td class=\"type\">")
      .append(typestr)
      .append("</td>");
    sb.append("<td class=\"brief\">")
      .append(Util.html(q.getBrief()))
      .append("</td>");
    sb.append("<td class=\"detail\">")
      .append(Util.html(q.getDetail()))
      .append("</td>");
    sb.append("<td class=\"choices\">");
    if (choices != null) {
        for (Question.Choice c : choices) {
            sb.append("&bull; ");
            sb.append(c.isOther() ? "<i>Other</i>" : Util.html(c.getText()));
            sb.append("<br>");
        }
    }
    sb.append("</td>");
   
    sb.append("<tr>");
    out.write(sb.toString());
    
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect(this.getServletContext().getContextPath() + "/index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

if (!sess.getUser().getRole().canViewUsers())
    return;

List<Question> rqs = RegistrationQuestions.getQuestions();
List<Question> aqs = ApprovalQuestions.getQuestions();
List<Question> pqs = ProfileQuestions.getQuestions();

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
th { font-weight: bold; text-align: left; white-space: nowrap; }
td.choices { white-space: nowrap; }
</style>
</head>
<body>

<h1>Profile Questions</h1>
<table border="1">
<tr>
    <th class="type">Type</th>
    <th class="brief">Brief</th>
    <th class="detail">Detail</th>
    <th class="choices">Choices</th>
</tr>
<% for (Question q : pqs) { writeRow(out, q); } %>
</table>

<hr>
<h1>Registration Questions</h1>
<table border="1">
<tr>
    <th class="type">Type</th>
    <th class="brief">Brief</th>
    <th class="detail">Detail</th>
    <th class="choices">Choices</th>
</tr>
<% for (Question q : rqs) { writeRow(out, q); } %>
</table>

<hr>
<h1>Approval Survey Questions</h1>
<table border="1">
<tr>
    <th class="type">Type</th>
    <th class="brief">Brief</th>
    <th class="detail">Detail</th>
    <th class="choices">Choices</th>
</tr>
<% for (Question q : aqs) { writeRow(out, q); } %>
</table>

</body>
</html>