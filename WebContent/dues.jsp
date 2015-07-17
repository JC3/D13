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

if (user.isInviteCodeNeeded()) {
    response.sendRedirect("invite.jsp");
    return;
}

StringBuilder personalSelectOptions = new StringBuilder();
personalSelectOptions.append("<option value=\"\">-- Select Another Camper --</option>");
for (User u:BillingManager.getUsersWithOpenPersonalDues())
    if (u.getUserId() != user.getUserId()) // dont display self in list
        personalSelectOptions.append("<option value=\"" + u.getUserId() + "\">").append(Util.html(u.getRealName())).append("</option>");
String personalSelectOptionsStr = personalSelectOptions.toString();

StringBuilder rvSelectOptions = new StringBuilder();
rvSelectOptions.append("<option value=\"\">-- Select Another Camper --</option>");
for (User u:BillingManager.getUsersWithOpenRVDues())
    if (u.getUserId() != user.getUserId()) // dont display self in list
        rvSelectOptions.append("<option value=\"" + u.getUserId() + "\">").append(Util.html(u.getRealName())).append("</option>");
String rvSelectOptionsStr = rvSelectOptions.toString();

List<BillingManager.PaymentItem> billable = BillingManager.getOpenItems(user);

String error = (String)sess.getAndClearAttribute(SessionData.SA_DUES_ERROR);
String error_html = (error == null ? null : Util.html(error));

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
    white-space: nowrap;
}
td.middle {
    text-align: right;
}
td.right {
    text-align: right;
    padding-right: 4ex;
}
</style>
<script language="JavaScript" type="text/javascript">
function checkForm () {
<% for (BillingManager.PaymentItem item:billable) { %>
    var <%=item.field%>_custom = document.getElementById("<%=item.field%>_custom").selected;
    if (<%=item.field%>_custom) {
        var amount = parseFloat(document.getElementById("<%=item.field%>_other").value);
        if (isNaN(amount) || (amount * 100 < <%=item.minimumAmount%>)) {
            alert("Other amount for <%=item.description%> must be specified and greater than <%=Util.intAmountToString(item.minimumAmount)%>.");
            document.getElementById("<%=item.field%>_other").focus();
            return false;
        }
    }
<% } %>
    return true;
}
function hideShowOther (basename) {
    var selitem = document.getElementById(basename + "_custom");
    var textbox = document.getElementById(basename + "_other");
    textbox.style.visibility = selitem.selected ? 'visible' : 'hidden';
    textbox.disabled = !selitem.selected;
}
</script>
</head>
<body>
<dis:header/>
<div class="nav"><a href="home.jsp">Go Back</a></div>

<% if (error != null) { %>
<div class="error">Error: <%=error_html%></div>
<% } %>

<div class="form">

<p>Please review your due payment information below. Our camp is paid for by donations and dues from
our campers, if you would like to make an additional financial contribution to camp, you may pay
for higher tiers or enter custom amounts below. You may pay other campers' dues as well. If you are
paying for another camper, select their name(s) below.</p>

<p>On the next page, you will be able to review and confirm your payment details and pay 
with PayPal.</p>

<form action="dueconfirm.jsp" method="post">
<input type="hidden" name="personal_count" value="5">
<input type="hidden" name="rv_count" value="5">
<input type="hidden" name="action" value="create">

<table width="100%">
<tr>
  <td class="section" colspan="3">Your Dues
<% if (user.isPaid()) { %>
<tr>
  <td class="item" colspan="3">All of your dues are paid!
<% } else if (billable.isEmpty()) { %>
<tr>
  <td class="item" colspan="3">Your due payments are currently being processed.
<% } else { %>
  <% for (BillingManager.PaymentItem item:billable) { %>
<tr>
  <td class="item"><%=Util.html(item.description)%>
  <td class="middle">
    <select class="dselect" name="<%=Util.html(item.field)%>_amount" onchange="hideShowOther('<%=Util.html(item.field)%>');">
    <% for (BillingManager.PaymentChoice choice:item.choices) { %>
      <option value="<%=choice.amount%>"><%=Util.html(choice.description)%> (<%=Util.intAmountToString(choice.amount)%>)</option>
    <% } %>
      <option value="-1" id="<%=Util.html(item.field)%>_custom">Other (Please Specify):</option>
    </select>
  <td class="right">
    <input class="dtext" type="text" name="<%=Util.html(item.field)%>_other" id="<%=Util.html(item.field)%>_other" disabled style="visibility:hidden;">
  <% } %>
<% } %> 
  
    
<tr>
  <td class="section" colspan="3">Other Campers' Dues
<tr>
  <td class="item" colspan="3"><select class="dselect" name="personal_0"><%=personalSelectOptionsStr %></select>
<tr>
  <td class="item" colspan="3"><select class="dselect" name="personal_1"><%=personalSelectOptionsStr %></select>
<tr>
  <td class="item" colspan="3"><select class="dselect" name="personal_2"><%=personalSelectOptionsStr %></select>
<tr>
  <td class="item" colspan="3"><select class="dselect" name="personal_3"><%=personalSelectOptionsStr %></select>
<tr>
  <td class="item" colspan="3"><select class="dselect" name="personal_4"><%=personalSelectOptionsStr %></select>

<tr>
  <td class="section" colspan="3">Other Campers' RV Fees
<tr>
  <td class="item" colspan="3"><select class="dselect" name="rv_0"><%=rvSelectOptionsStr %></select>
<tr>
  <td class="item" colspan="3"><select class="dselect" name="rv_1"><%=rvSelectOptionsStr %></select>
<tr>
  <td class="item" colspan="3"><select class="dselect" name="rv_2"><%=rvSelectOptionsStr %></select>
<tr>
  <td class="item" colspan="3"><select class="dselect" name="rv_3"><%=rvSelectOptionsStr %></select>
<tr>
  <td class="item" colspan="3"><select class="dselect" name="rv_4"><%=rvSelectOptionsStr %></select>
<tr>
  <td colspan="3" style="text-align:center;padding-top:1ex;"><input type="submit" class="dbutton" value="Next" onclick="return checkForm();">
</table>  

</form>

</div>

<div class="nav"><a href="home.jsp">Go Back</a></div>

<dis:footer/>
</body>
</html>