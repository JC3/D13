<%@ page language="java" contentType="application/json; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.ThisYear" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.notify.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%
SessionData sess = new SessionData(session);
if (!sess.isLoggedIn())
    return; // not logged in
    
User editor = sess.getUser();
if (!editor.getRole().canEditMailTemplates())
    return; // permission denied

String what = request.getParameter("what");
String subj = request.getParameter("subject");
String body = request.getParameter("body");
    
boolean success = false;
String message = "";

try {
    // I messed this up:
    if ("approve".equals(what))
        what = Email.RT_APPROVAL_CONTENT;
    else if ("reject".equals(what))
        what = Email.RT_REJECTION_CONTENT;
    else if ("invite".equals(what))
        what = Email.RT_INVITE_CONTENT;
    // End kludge.
    Email.Configuration.setContents(what, subj, body);
    editor.addGeneralLogEntry("Edited email template \"" + what + "\".", GeneralLogEntry.TYPE_EMAIL_TEMPLATE);
    success = true;
} catch (Exception x) {    
    success = false;
    message = x.getMessage();
}
%>
{"success":<%=success %>,"message":"<%=message.replace("\"", "\\\"") %>"}