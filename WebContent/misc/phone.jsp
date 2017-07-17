<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
<%@ page import="d13.web.SessionData" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat" %>
<%!
private static boolean isValidNumber (String s) {
    try {
        Util.parsePhoneNumber(s);
        return true;
    } catch (Exception x) {
        return false;
    }    
}
private static String validateNumber (String s) {
    try {
        Util.PhoneNumberInfo info = Util.parsePhoneNumber(s);
        return info == null ? null : info.formatted;
    } catch (Exception x) {
        return s;
    }
}
private static String formatNumber (String s, PhoneNumberFormat f) {
    try {
        return Util.formatPhoneNumber(s, f);
    } catch (Exception x) {
        return null;
    }
}
private static String formatNumberMobile (String s) {
    try {
        return Util.formatPhoneNumberMobile(s);
    } catch (Exception x) {
        return null;
    }
}
private static String getRegion (String s) {
    try {
        return Util.getPhoneNumberRegion(s);
    } catch (Exception x) {
        return null;
    }
}

private static void doReparse () {
    for (User user : User.findAll()) {
        try {
            user.setPhone(user.getPhone());
        } catch (Exception x) {
            System.out.printf("REPARSE: %s (%s) failed for '%s': %s\n", 
                    user.getRealName(),
                    user.getUserId(),
                    user.getPhone(),
                    x.getMessage());
        }
    }
}

private static String contactNumbers (User u) {
    List<String> ns = Util.getPhoneNumbers(u.getEmergencyContact());
    StringBuilder b = new StringBuilder();
    for (String n : ns) {
        if (b.length() > 0)
            b.append(", ");
        b.append(n);
    }
    return b.toString();
}
%>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn()) {
    sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
    response.sendRedirect(this.getServletContext().getContextPath() + "/index.jsp?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
    return;
}

User editor = sess.getUser();
if (!editor.getRole().canEditUsers())
    return;

String action = request.getParameter("do");
if ("reparse".equals(action)) {
    doReparse();
    return;
}

List<User> users = new ArrayList<User>();
for (User user : User.findAll("realName"))
    if (user.getState() != UserState.NEW_USER)
        users.add(user);

String detail = Util.getAbsoluteUrl(request, "details.jsp") + "?u=";
%>
<!DOCTYPE html>
<html>
<head>
<title>Disorient - Phone Number Tool</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$('#reparse-all').click(function () {
		if (!confirm('This could break stuff. Are you sure?'))
			return false;
		$.get('phone.jsp?do=reparse').then(function () {
			window.location.reload();
		}).fail(function () {
			alert('Something went wrong.');
		});
	});
});
</script>
<style type="text/css">
.numbers {
    border-collapse: collapse;
}
.numbers td {
    border: 1px solid #aaa;
    padding-left: 1ex;
    padding-right: 1ex;
    vertical-align: top;
    white-space: nowrap;
    text-align: right;
}
.numbers th {
    font-weight: bold;
    background: #eee;
}
.numbers td:nth-child(1) {
    text-align: left;
}
.invalid {
    background: #fdd;
}
.actions {
    padding: 1ex;
    border: 1px solid #aaa;
    margin-bottom: 1ex;
}
</style>
</head>
<body>
<div class="actions">
Actions:
<button id="reparse-all">Reparse Numbers</button>
</div>
<h1>Phone Numbers</h1>
<table class="numbers">
<tr>
  <th>Name
  <th>Number
  <th>Validated
  <th>From US (db)
  <th>Region
  <th>From US
  <th>From Mobile
  <th>E164
  <th>RFC3966
  <th>International
  <th>National
<% for (User u : users) { %>
<tr<%= isValidNumber(u.getPhone()) ? "" : " class=\"invalid\"" %>>
  <td><a href="<%= detail + u.getUserId() %>"><%= Util.html(u.getRealName()) %></a>
  <td><%= Util.html(u.getPhone()) %>
  <td><%= Util.html(validateNumber(u.getPhone())) %>
  <td><%= Util.html(u.getPhoneFromUs()) %>
  <td><%= Util.html(getRegion(u.getPhone())) %>
  <td><%= Util.html(formatNumber(u.getPhone(), null)) %>
  <td><%= Util.html(formatNumberMobile(u.getPhone())) %>
  <td><%= Util.html(formatNumber(u.getPhone(), PhoneNumberFormat.E164)) %>
  <td><%= Util.html(formatNumber(u.getPhone(), PhoneNumberFormat.RFC3966)) %>
  <td><%= Util.html(formatNumber(u.getPhone(), PhoneNumberFormat.INTERNATIONAL)) %>
  <td><%= Util.html(formatNumber(u.getPhone(), PhoneNumberFormat.NATIONAL)) %>
<% } %>
</table>
<h1>Emergency Contacts</h1>
<table class="numbers">
<tr>
  <th>Name
  <th>Contact Text
  <th>Contact Numbers
<% for (User u : users) {
    String c = contactNumbers(u); %>
<tr<%= c.isEmpty() ? " class=\"invalid\"" : "" %>>
  <td><a href="<%= detail + u.getUserId() %>"><%= Util.html(u.getRealName()) %></a>
  <td><%= Util.html(u.getEmergencyContact()) %>
  <td><%= Util.html(c) %>
<% } %>
</table>
</body>
</html>