package d13.web.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.joda.time.DateTime;

import d13.util.Util;
import d13.web.DataViewer;
import d13.web.ReportController;
import d13.web.SessionData;

/**
 * This only exists so we can use nice path names for reports. It doesn't technically
 * need to be this way but at this point this entire project is just a learning
 * experience for me so I'm trying a few new things.
 */
@WebServlet("/report/*")
public class ReportDispatchServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
       
    @Override
	protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        SessionData sess;
        try {
            sess = new SessionData(request.getSession());
            if (!sess.isLoggedIn()) {
                sess.setAttribute(SessionData.SA_LOGIN_ERROR, "You must be logged in.");
                response.sendRedirect(Util.getAbsoluteUrl(request, "index.jsp") + "?next=" + java.net.URLEncoder.encode(Util.getCompleteUrl(request), "us-ascii"));
                return;
            }
        } catch (Exception x) {
            x.printStackTrace(System.err);
            response.sendError(500);
            return;
        }
        
        String[] parts = request.getPathInfo() == null ? null : Util.removeEmptyStrings(request.getPathInfo().split("/"));
        
        ReportParameters params = new ReportParameters();
        
        if (parts != null && parts.length > 0) {
            try {
                params.setId(Long.parseLong(parts[0]));
                params.setMode(ReportParameters.Mode.PRETTY);
            } catch (NumberFormatException x) {
            }
        }
        
        if (parts != null && parts.length > 1 && params.getId() >= 0) {
            if ("print".equalsIgnoreCase(parts[1]))
                params.setMode(ReportParameters.Mode.PRINT);
            else if ("csv".equalsIgnoreCase(parts[1]))
                params.setMode(ReportParameters.Mode.CSV);
        }
     
        request.setAttribute("params", params);
        
        if (params.isPrint()) {
            response.setContentType("text/html");
            request.getRequestDispatcher("/WEB-INF/private/report_print.jsp").include(request, response);
        } else if (params.isCsv()) {
            response.setContentType("text/csv");
            serveCSV(params, response, sess); // serve from here since include can't set response headers
        } else {
            response.setContentType("text/html");
            request.getRequestDispatcher("/WEB-INF/private/report_pretty.jsp").include(request, response);
        }
        
    }

    private void serveCSV (ReportParameters params, HttpServletResponse response, SessionData sess) throws IOException {
        
        ReportController.View rview;
        try {
            rview = new ReportController.View(params, null, sess);
        } catch (Exception x) {
            x.printStackTrace(System.err);
            return;
        }
        
        if (rview.getMyReportColumns() == null) {
            response.sendError(404);
            return;
        }
        
        String timestamp = DateTime.now().toString("yyyyMMdd-HHmm");
        response.setHeader("Content-Disposition", String.format("attachment;filename=DisorientReport-%s-%s.csv", timestamp, rview.getMyReportId()));
        CSVPrinter out = new CSVPrinter(new OutputStreamWriter(response.getOutputStream(), "utf-8"), CSVFormat.EXCEL);
        
        List<String> header = new ArrayList<String>();
        for (DataViewer.Column col : rview.getMyReportColumns())
            header.add(col.name);
        
        boolean first = true;
        for (ReportController.View.Section section : rview.getMyReportSections()) {
            if (first)
                first = false;
            else
                out.println();
            if (section.title != null)
                out.printRecord(section.title);
            out.printRecord(header);
            for (DataViewer.Row row : section.rows)
                out.printRecord(row.values);
        }
        
        out.flush();
        
    }
    
}
