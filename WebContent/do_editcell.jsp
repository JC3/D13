<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="d13.web.*" %>
<%
SessionData sess = new SessionData(session);
EditCellDetails edit = new EditCellDetails(pageContext, sess);

sess.clearAttribute(SessionData.SA_EDIT_CELL_ERROR);
sess.clearAttribute(SessionData.SA_EDIT_CELL_MESSAGE);

if (edit.isFailed()) {
    sess.setAttribute(SessionData.SA_EDIT_CELL_ERROR, edit.getErrorMessage());
    response.sendRedirect(edit.getFailTarget());
} else {
    sess.setAttribute(SessionData.SA_EDIT_CELL_MESSAGE, edit.getSuccessMessage());
    response.sendRedirect(edit.getSuccessTarget());
}
%>
