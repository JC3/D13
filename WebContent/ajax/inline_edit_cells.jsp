<%@ page language="java" contentType="application/json; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.web.*" %>
<%@ page import="org.codehaus.jettison.json.*" %>
<%

EditCellDetails.Multiple editor;
JSONObject result;

try {
    editor = new EditCellDetails.Multiple(pageContext, new SessionData(session));
    result = new JSONObject();
    result.put("error", false);
    result.put("warnings", editor.getWarnings());
    for (EditCellDetails.Multiple.ResultState rs : editor.getResults()) {
        JSONObject r = new JSONObject();
        r.put("cell", rs.cell);
        r.put("autohide", rs.autohide);
        r.put("mandatory", rs.mandatory);
        r.put("hidden", rs.hidden);
        result.append("results", r);
    }
} catch (SecurityException x) {
    return;
} catch (Throwable t) {
    result = new JSONObject();
    result.put("error", true);
    result.put("error_message", t.getMessage());
}

out.print(result.toString());
return;

%>