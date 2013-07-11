<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
EditCells edit = new EditCells(pageContext, sess);

sess.clearAttribute(SessionData.SA_CELL_ERROR);

if (edit.isFailed()) {
    sess.setAttribute(SessionData.SA_CELL_ERROR, edit.getErrorMessage());
    response.sendRedirect(edit.getFailTarget());
} else {
    response.sendRedirect(edit.getSuccessTarget());
}
%>
