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
if (!editor.getRole().canEditCells())
    return; // permission denied

boolean success = false;
String message = "";

try {
    Cell cell = Cell.findById(Util.getParameterLong(request, "c"));
    if (cell.isCategory())
        throw new IllegalArgumentException("Invalid parameter.");
    else if ("u".equals(request.getParameter("d")))
        cell.moveUp();
    else if ("d".equals(request.getParameter("d")))
        cell.moveDown();
    else
        throw new IllegalArgumentException("Invalid parameter.");
    success = true;
} catch (Exception x) {    
    success = false;
    message = x.getMessage();
}
%>
{"ok":<%=success %>,"e":"<%=message.replace("\"", "\\\"") %>"}