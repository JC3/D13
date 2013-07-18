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
<%!
List<DueItem> getItems (List<DueItem> items, ServletRequest request, String prefix, boolean rv) {

    Set<Long> retrieved = new HashSet<Long>(0);
    int count = Integer.parseInt(request.getParameter(prefix + "_count"));
   
    for (int n = 0; n < count; ++ n) {
        Long userid = Util.getParameterLong(request, prefix + "_" + Integer.toString(n));
        if (userid == null)
            continue;
        if (retrieved.contains(userid))
            continue; // silently ignore duplicate ids
        else
            retrieved.add(userid);
        User user = User.findById(userid);
        DueItem item;
        if (rv)
            item = user.getRvDueItem();
        else
            item = user.getPersonalDueItem();
        if (item != null && item.isOutstanding())
            items.add(item);
    }
    
    return items;
    
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    response.sendRedirect("index.jsp");
    return;
}

List<DueItem> items = new ArrayList<DueItem>();
try {
	getItems(items, request, "personal", false);
	getItems(items, request, "rv", true);
} catch (Exception x) {
    return; // user shouldn't be here.
}
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
<% for (DueItem item:items) { %>
<tr>
  <td class="item"><%= item.calculateAmountOwed() %>
  <td class="total">
<% } %>
</div>

<dis:footer/>
</body>
</html>