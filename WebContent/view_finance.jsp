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

FinanceReport fr = new FinanceReport(sess);
if (fr.isFailed())
    return;

DefaultDataConverter dd = new DefaultDataConverter();
List<FinanceReport.DuesByUser> dbu = fr.getDuesByUser();
FinanceReport.Totals tot = fr.getTotals();

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
.summary {
    background: #101010;
    border: 1px solid #303030;
    padding: 2ex;
    width: 60ex;    
}
.summary th {
    white-space: nowrap;
    text-align: left;
    border: 0;
    margin: 0;
    padding: 2px 0.5ex 2px 0.5ex;
    background: #202020;
}
.summary td {
    vertical-align: top;
    border: 0;
    margin: 0;
    padding: 2px 0.5ex 2px 0.5ex;
    border-right: 1px solid #202020;
    border-top: 1px solid #303030;
    white-space: nowrap;
}
td.r {
    text-align: right;
}
td.s {
    font-size: smaller;
}
#stats {
    font-size: smaller;
}
h1 {
    font-size: larger;
}
</style>
</head>
<body>
<dis:header/>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<h1>Summary:</h1>
<table class="summary">
<tr>
  <th>
  <th class="r">Total
  <th class="r">Paid
  <th class="r">Unpaid
<tr>
  <td>Camper Fees
  <td class="r"><%= Util.intAmountToString(tot.personalTotal) %>
  <td class="r"><%= Util.intAmountToString(tot.personalPaid) %>
  <td class="r"><%= Util.intAmountToString(tot.personalTotal - tot.personalPaid) %>
<% for (String tier:tot.personalByTier.keySet()) { %>
<tr>
  <td class="s" style="padding-left:2ex;"><%= Util.html(tier) %>
  <td class="r s"><%= Util.intAmountToString(tot.personalByTier.get(tier)) %>
  <td class="s">
  <td class="s">
<% } %>
<tr>
  <td>RV Fees
  <td class="r"><%= Util.intAmountToString(tot.rvTotal) %>
  <td class="r"><%= Util.intAmountToString(tot.rvPaid) %>
  <td class="r"><%= Util.intAmountToString(tot.rvTotal - tot.rvPaid) %>
<tr>
  <td>Total Fees
  <td class="r"><%= Util.intAmountToString(tot.personalTotal + tot.rvTotal) %>
  <td class="r"><%= Util.intAmountToString(tot.personalPaid + tot.rvPaid) %>
  <td class="r"><%= Util.intAmountToString((tot.personalTotal + tot.rvTotal) - (tot.personalPaid + tot.rvPaid)) %>
<tr>
  <td>PayPal Fees
  <td class="r">
  <td class="r">(<%= Util.intAmountToString(tot.paypalFees) %>)
  <td class="r">
<tr>
  <td>Net
  <td class="r"><%= Util.intAmountToString(tot.personalTotal + tot.rvTotal - tot.paypalFees) %>
  <td class="r"><%= Util.intAmountToString(tot.personalPaid + tot.rvPaid - tot.paypalFees) %>
  <td class="r"><%= Util.intAmountToString((tot.personalTotal + tot.rvTotal) - (tot.personalPaid + tot.rvPaid)) %>
<tr>
  <td colspan="4" class="s">Note: Net Total subtracts paid PayPal fees, does not estimate fees for unpaid dues.
</table>

<hr>

<h1>Individual Dues:</h1>
<table class="summary">
<tr>
  <th>ID
  <th>Name
  <th>Email
  <th class="r">Tier
  <th class="r">Camp Fee Owed
  <th class="r">Camp Fee Paid
  <th class="r">Camp Fee Paid On
  <th class="r">Camp Fee Paid By
  <th class="r">RV Fee Owed
  <th class="r">RV Fee Paid
  <th class="r">RV Fee Paid On
  <th class="r">RV Fee Paid By
<% for (FinanceReport.DuesByUser d:dbu) { %>
<tr>
  <td><%= d.user.getUserId() %>
  <td><%= Util.html(d.user.getRealName()) %>
  <td><%= Util.html(d.user.getEmail()) %>
  <td class="r"><%= Util.html(d.personalTierName) %>
  <td class="r"><%= Util.intAmountToString(d.personalOwedAmount) %>
  <td class="r"><%= d.personalPaid ? Util.intAmountToString(d.personalPaidAmount) : "unpaid" %>
  <td class="r"><%= d.personalPaid ? dd.asString(d.personalPaidDate) : "" %>
  <td class="r"><%= (d.personalPaid && d.personalPaidBy != null) ? Util.html(d.personalPaidBy.getRealName()) : "" %>
  <td class="r"><%= d.rvOwed ? Util.intAmountToString(d.rvOwedAmount) : "n/a" %>
  <% if (d.rvOwed) { %>
  <td class="r"><%= d.rvPaid ? Util.intAmountToString(d.rvPaidAmount) : "unpaid" %>
  <td class="r"><%= d.rvPaid ? dd.asString(d.rvPaidDate) : "" %>
  <td class="r"><%= (d.rvPaid && d.rvPaidBy != null) ? Util.html(d.rvPaidBy.getRealName()) : "" %>
  <% } else { %>
  <td class="r">
  <td class="r">
  <td class="r">
  <% } %>
<% } %>
<tr>
  <td colspan="4"><strong>TOTAL:</strong>
  <td class="r"><strong><%= Util.intAmountToString(tot.personalTotal) %></strong>
  <td class="r"><strong><%= Util.intAmountToString(tot.personalPaid) %></strong>
  <td>
  <td>
  <td class="r"><strong><%= Util.intAmountToString(tot.rvTotal) %></strong>
  <td class="r"><strong><%= Util.intAmountToString(tot.rvPaid) %></strong>
  <td>
  <td>

</table>

<hr>

<h1>Completed Transactions:</h1>

<table class="summary">
<tr>
    <th>Invoice ID
    <th>Invoice Date
    <th>User Email
    <th>User Name
    <th class="r">Invoice Amount
    <th class="r">PayPal Sender
    <th class="r">PayPal First Name
    <th class="r">PayPal Last Name
    <th class="r">PayPal Transaction
    <th class="r">PayPal Status
    <th class="r">PayPal Amount
    <th class="r">PayPal Fee
    <th class="r">PayPal Date
<% for (Invoice i:fr.getInvoices()) { %>
<tr>
    <td><%= i.getInvoiceId() %>
    <td><%= dd.asString(i.getCreated()) %>
    <td><%= Util.html(i.getCreator().getEmail()) %>
    <td><%= Util.html(i.getCreator().getRealName()) %>
    <td class="r"><%= Util.intAmountToString(i.getInvoiceAmount()) %>
    <td class="r"><%= Util.html(i.getPaypalSenderEmail()) %>
    <td class="r"><%= Util.html(i.getPaypalSenderFirstName()) %>
    <td class="r"><%= Util.html(i.getPaypalSenderLastName()) %>
    <td class="r"><%= Util.html(i.getPaypalTransactionId()) %>
    <td class="r"><%= Util.html(i.getPaypalTransactionStatus()) %>
    <td class="r"><%= Util.intAmountToString(i.getPaypalAmount()) %>
    <td class="r"><%= Util.intAmountToString(i.getPaypalFee()) %>
    <td class="r"><%= Util.html(i.getPaypalTimestamp()) %>
<% } %> 
</table>

<div class="nav">
  <a href="home.jsp">Home</a>
</div>

<dis:footer/>
</body>
</html>