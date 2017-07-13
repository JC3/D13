<%@ tag language="java" pageEncoding="ISO-8859-1" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag import="d13.web.*" %>
<%@ tag import="d13.util.Util" %>
<%@ tag import="java.util.List" %>
<table class="report">
<% 
    ReportController.View rview = (ReportController.View)request.getAttribute("rview");
    List<DataViewer.Column> rcols = rview.getMyReportColumns();

    boolean first = true;
    for (ReportController.View.Section s : rview.getMyReportSections()) {
        if (first) {
            first = false;
        } else {
            %><tr class="r-spacer"><td colspan="<%=rcols.size()%>"><div style="margin:0;padding:0;">&nbsp;</div><%
        }
        if (s.title != null) {
            %><tr class="r-section"><td colspan="<%=rcols.size()%>"><%= Util.html(s.title) %> (<%= s.rows.size() %>)<%
        }
        %><tr class="r-header"><%
        for (DataViewer.Column col : rcols) {
            String cls = (col.shortClass == null) ? "" : (" class=\"" + col.shortClass + "\"");
            %><th<%= cls %>><%= Util.html(col.name) %><%
        }
        for (DataViewer.Row row : s.rows) {
            %><tr class="r-user"><%  
            for (int n = 0; n < row.values.size(); ++ n) {
                String cls = rcols.get(n).shortClass;
                cls = (cls == null) ? "" : (" class=\"" + cls + "\"");
                %><td<%= cls %>><div><%= Util.html(row.values.get(n), true) %></div><%
            }
        }
    } 
%>
</table>
