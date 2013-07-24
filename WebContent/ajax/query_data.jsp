<%@ page language="java" contentType="application/json; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="d13.dao.*" %>
<%@ page import="d13.web.*" %>
<%@ page import="d13.util.Util" %>
<%@ page import="java.util.List" %>
<%@ page import="com.thoughtworks.xstream.*" %>
<%@ page import="com.thoughtworks.xstream.annotations.*" %>
<%@ page import="com.thoughtworks.xstream.io.json.*" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="dis" %>
<%!
static class Cell {
    String   t;
    String   h;
}
static class Row {
    long     u;     // user id
    Cell[]   v;     // values
}
@XStreamAlias("result")
static class Result {
    boolean  f;     // failed
    String   e;     // errors
    String[] c;     // columns
    String[] l;     // column classes
    Row[]    r;     // rows
    void fail (String message) { f = true; e = message; c = null; l = null; r = null; }
}
%>
<%
Result result = new Result();

SessionData sess = new SessionData(session);
if (!sess.isLoggedIn())
    result.fail("You must be logged in.");

DataViewer view = null;
if (!result.f) {
	view = new DataViewer(pageContext, sess);
	if (view.isFailed())
	    result.fail("Query error.");
}

if (!result.f) {
	
    List<String> cols = view.getColumns();
	List<String> cls = view.getColumnClasses();
	List<DataViewer.Row> rows = view.getRows();

	result.c = cols.toArray(new String[cols.size()]);
	result.l = cls.toArray(new String[cls.size()]);
	result.r = new Row[rows.size()];
	
	for (int n = 0; n < rows.size(); ++ n) {
	    DataViewer.Row r = rows.get(n);
	    result.r[n] = new Row();
	    result.r[n].u = r.user.getUserId();
	    result.r[n].v = new Cell[r.values.size()];
	    for (int j = 0; j < r.values.size(); ++ j) {
	        result.r[n].v[j] = new Cell();
	        result.r[n].v[j].t = r.values.get(j);
	        result.r[n].v[j].h = r.hrefs.get(j);
	    }
	}
	
}

XStream xs = new XStream(new JsonHierarchicalStreamDriver());
xs.processAnnotations(Result.class);
xs.processAnnotations(Row.class);
xs.setMode(XStream.NO_REFERENCES);
xs.toXML(result, out);
%>
