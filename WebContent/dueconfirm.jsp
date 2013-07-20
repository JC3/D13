<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.questions.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    response.sendRedirect("dues.jsp");
    return;
}

User user = sess.getUser();

Invoice invoice = null;
String action = request.getParameter("action");
if ("create".equalsIgnoreCase(action)) {
    
    try {

        BillingManager.NewInvoiceFormInput input = new BillingManager.NewInvoiceFormInput();
    
        if (request.getParameter("personal_amount") != null) {
	        input.personalAmount = Integer.parseInt(request.getParameter("personal_amount"));
	        if (input.personalAmount == -1)
	            input.personalAmount = (int)(100.0f * Float.parseFloat(request.getParameter("personal_other")) + 0.5f);
        }

        if (request.getParameter("rv_amount") != null) {
	        input.rvAmount = Integer.parseInt(request.getParameter("rv_amount"));
	        if (input.rvAmount == -1)
	            input.rvAmount = (int)(100.0f * Float.parseFloat(request.getParameter("rv_other")) + 0.5f);
        }
        
        int count = Integer.parseInt(request.getParameter("personal_count"));
        for (int n = 0; n < count; ++ n) {
            Long id = Util.getParameterLong(request, "personal_" + n);
            if (id != null) input.personalUsers.add(id);
        }
            
        count = Integer.parseInt(request.getParameter("rv_count"));
        for (int n = 0; n < count; ++ n) {
            Long id = Util.getParameterLong(request, "rv_" + n);
            if (id != null) input.rvUsers.add(id);
        }
        
        invoice = BillingManager.createInvoiceFromInput(sess.getUser(), input);
        sess.setAttribute(SessionData.SA_DUES_INVOICE_ID, invoice.getInvoiceId());
        response.sendRedirect(Util.getCompleteUrl(request));
        return;
        
    } catch (Throwable t) {
        
        t.printStackTrace();

        sess.setAttribute(SessionData.SA_DUES_ERROR, t.getMessage());
        response.sendRedirect("dues.jsp");
        return;
        
    }
    
} else {
    
    if (sess.getAttribute(SessionData.SA_DUES_INVOICE_ID) == null) {
        response.sendRedirect("dues.jsp");
        return;
    }

    try {
        invoice = Invoice.findById((Long)sess.getAttribute(SessionData.SA_DUES_INVOICE_ID));
    } catch (Throwable t) {
        sess.setAttribute(SessionData.SA_DUES_ERROR, t.getMessage());
        response.sendRedirect("dues.jsp");
        return;
    }

}

String paypal_email = RuntimeOptions.getOption("dues.paypal_email", "dues@disorient.info");
String paypal_site = RuntimeOptions.getOption("dues.paypal_site", "https://www.paypal.com/cgi-bin/webscr");
String paypal_notify = RuntimeOptions.getOption("dues.paypal_notify", "http://camp.disorient.info/pay/notify.jsp");
String paypal_cancel = RuntimeOptions.getOption("dues.paypal_cancel", "http://camp.disorient.info/pay/cancel.jsp");
String paypal_return = RuntimeOptions.getOption("dues.paypal_return", "http://camp.disorient.info/pay/success.jsp");
String invoice_total = String.format("%.2f", (float)invoice.getInvoiceAmount() / 100.0f);
String invoice_id = Long.toString(invoice.getInvoiceId());

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Disorient</title>
<link rel="stylesheet" type="text/css" href="disorient.css">
<style type="text/css">
table.invoice {
    border: 1px solid #303030;
    background: black;
}
th {
    color: #ff8000;
    font-weight: bold;
}
th.item {
    padding-left: 4ex;
    text-align: left;
}
th.cost {
    text-align: right;
    padding-right: 4ex;
}
td.item {
    padding-left: 4ex;
    word-wrap: false;
    border-top: 1px solid #202020;
}
td.cost {
    text-align: right;
    padding-right: 4ex;
    vertical-align: top;
    border-top: 1px solid #202020;
}
td.total {
    font-weight: bold;
    color: #ff8080;
}
td div {
    border: 0;
    margin: 0;
    paddin: 0;
}
div.note {
    font-size: smaller;
    padding-left: 2ex;
}
</style>
</head>
<body>
<dis:header/>

<div class="form">

<table width="100%" class="invoice">
<tr>
  <th class="item">Item 
  <th class="cost">Cost
<% for (InvoiceItem item:invoice.getItems()) { %>
<tr>
  <td class="item"><div class="desc"><%=Util.html(item.getDescription()) %></div>
  <% if (item.getDue().getUser().getUserId() != user.getUserId()) { %>
    <div class="note">For <%=Util.html(item.getDue().getUser().getRealName()) %></div>
  <% } %>
  <td class="cost"><%=Util.intAmountToString(item.getAmount()) %>
<% } %>
<tr>
  <td class="item total"><div class="desc">Total:</div>
  <td class="cost total"><%=Util.intAmountToString(invoice.getInvoiceAmount()) %>
</table>

<p>To go back and edit your payment items, please use the back button on your browser.</p>

<p>To pay, please click here:
<div style="text-align:center;">
<form name="_xclick" action="<%=Util.html(paypal_site)%>" method="post" target="_top">
<input type="hidden" name="business" value="<%=Util.html(paypal_email)%>">
<input type="hidden" name="cmd" value="_xclick">
<input type="hidden" name="amount" value="<%=invoice_total%>">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="item_name" value="Disorient Camp Dues">
<input type="hidden" name="item_number" value="<%=invoice_id%>">
<input type="hidden" name="notify_url" value="<%=Util.html(paypal_notify)%>">
<!--<input type="hidden" name="bn" value="Disorient_BuyNow_WPS_US">-->
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="return" value="<%=Util.html(paypal_return)%>">
<!--<input type="hidden" name="rm" value="2">-->
<input type="hidden" name="cancel_return" value="<%=Util.html(paypal_cancel)%>">
<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_buynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
</form>
</div>
</p>

</div>

<dis:footer/>
</body>
</html>