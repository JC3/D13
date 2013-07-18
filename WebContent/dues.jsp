<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);

String fail_target = Util.getCompleteUrl(request); // on error come back to this page
String success_target = request.getParameter("next");
if (success_target == null || success_target.trim().isEmpty()) success_target = "home.jsp";

if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "Please log in first.");
    response.sendRedirect("index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

User user = sess.getUser();
if (user.getState() != UserState.APPROVED) {
    response.sendRedirect("home.jsp"); // user shouldn't be here
    return;
}

/*
DueItem personalDue = user.getPersonalDueItem();
DueItem rvDue = user.getRvDueItem();

DueCalculator.Amount personalAmount = (personalDue == null ? null : personalDue.calculateAmountOwed());
DueCalculator.Amount rvAmount = (rvDue == null ? null : rvDue.calculateAmountOwed());

StringBuilder personalSelectOptions = new StringBuilder();
personalSelectOptions.append("<option value=\"\">-- Select Another Camper --</option>");
for (User u:User.findUnpaidPersonalDues())
    if (u.getUserId() != user.getUserId()) // dont display self in list
        personalSelectOptions.append("<option value=\"" + u.getUserId() + "\">").append(Util.html(u.getRealName())).append("</option>");
String personalSelectOptionsStr = personalSelectOptions.toString();

StringBuilder rvSelectOptions = new StringBuilder();
rvSelectOptions.append("<option value=\"\">-- Select Another Camper --</option>");
for (User u:User.findUnpaidRVDues())
    if (u.getUserId() != user.getUserId()) // dont display self in list
        rvSelectOptions.append("<option value=\"" + u.getUserId() + "\">").append(Util.html(u.getRealName())).append("</option>");
String rvSelectOptionsStr = rvSelectOptions.toString();
*/

DueCalculator.Amount personalAmount = null, rvAmount = null;
String personalSelectOptionsStr = "";
String rvSelectOptionsStr = "";

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
td.section {
    color: #ff8000;
    font-weight: bold;
}
td.item {
    padding-left: 4ex;
}
td.total {
    text-align: right;
    padding-right: 4ex;
}
</style>
</head>
<body>
<dis:header/>

<div class="form">
<p>Our dues system is currently under development. It will be open at some point on July 18th. We will let you know when it opens.
Please check back soon and have no fear, <a href="terms.jsp#dues">you will not miss your due tier!</a></p>

<!-- 
<p>Please review your due payment information below. On the next page, you will be able to
confirm your payment details and pay with PayPal.</p>
<p>You may pay other campers' dues below as well. If you choose to do so, select their names
below and you will be able to review and confirm your total on the next page.</p>

<form action="dueconfirm.jsp" method="post">
<input type="hidden" name="personal_count" value="6">
<input type="hidden" name="rv_count" value="6">

<table width="100%">
<tr>
  <td class="section" colspan="2">Your Total
<% int total = 0; %>
<% if (personalAmount != null) { 
   total += personalAmount.getAmount(); %>
<tr>
  <td class="item">Personal: <%=Util.html(personalAmount.getTierName()) %>
  <td class="total"><%=Util.intAmountToString(personalAmount.getAmount()) %>
  <input type="hidden" name="personal_0" value="<%=user.getUserId() %>">
<% } %>
<% if (rvAmount != null) { 
   total += rvAmount.getAmount(); %>
<tr>
  <td class="item">RV: <%=Util.html(rvAmount.getTierName()) %>
  <td class="total"><%=Util.intAmountToString(rvAmount.getAmount()) %>
  <input type="hidden" name="rv_0" value="<%=user.getUserId() %>">
<% } %>
<% if (personalAmount == null && rvAmount == null) { %>
<tr>
  <td class="item" colspan="2">You have no outstanding dues!
<% } else {%>
<tr>
  <td class="item">Total:
  <td class="total"><%=Util.intAmountToString(total) %>
<% } %>
  
<tr>
  <td class="section" colspan="2">Other Campers' Dues
<tr>
  <td class="item"><select class="dselect" name="personal_1"><%=personalSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td class="item"><select class="dselect" name="personal_2"><%=personalSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td class="item"><select class="dselect" name="personal_3"><%=personalSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td class="item"><select class="dselect" name="personal_4"><%=personalSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td class="item"><select class="dselect" name="personal_5"><%=personalSelectOptionsStr %></select>
  <td class="total">

<tr>
  <td class="section" colspan="2">Other Campers' RV Fees
<tr>
  <td class="item"><select class="dselect" name="rv_1"><%=rvSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td class="item"><select class="dselect" name="rv_2"><%=rvSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td class="item"><select class="dselect" name="rv_3"><%=rvSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td class="item"><select class="dselect" name="rv_4"><%=rvSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td class="item"><select class="dselect" name="rv_5"><%=rvSelectOptionsStr %></select>
  <td class="total">
<tr>
  <td colspan="2" style="text-align:center;padding-top:1ex;"><input type="submit" class="dbutton" value="Next">
</table>  

</form>
-->

</div>

<div class="nav"><a href="home.jsp">Go Back</a></div>

<dis:footer/>
</body>
</html>