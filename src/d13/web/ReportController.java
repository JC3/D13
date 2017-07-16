package d13.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import d13.dao.Cell;
import d13.dao.ReportTemplate;
import d13.dao.User;
import d13.util.Util;
import d13.web.servlets.ReportParameters;

public class ReportController {
        
    public static class Save {

        private boolean failed;
        private String errorMessage;
        private boolean reportCreated;
        private long reportId;
        
        public Save (PageContext page, SessionData session) {            
            try {
                
                if (!session.isLoggedIn())
                    throw new SecurityException("You are not logged in.");
                else if (!session.getUser().getRole().canViewUsers())
                    throw new SecurityException("Permission denied.");
                
                String cellstr = page.getRequest().getParameter("cells");
                int filter = Integer.parseInt(page.getRequest().getParameter("filter"));
                String[] fields = filterValidFields(page, session, page.getRequest().getParameterValues("columns[]"));
                boolean excludemc = Util.parseBooleanDefault(page.getRequest().getParameter("excludemc"), false);
                
                int cells;
                if ("no".equals(cellstr))
                    cells = ReportTemplate.CELLS_NONE;
                else if ("list".equals(cellstr))
                    cells = ReportTemplate.CELLS_LIST;
                else if ("split".equals(cellstr))
                    cells = ReportTemplate.CELLS_SPLIT;
                else
                    throw new IllegalArgumentException("Invalid cell mode name specified.");

                ReportTemplate rt = new ReportTemplate(fields, cells, filter, excludemc, session.getUser());
                ReportTemplate dbrt = ReportTemplate.findOrCreate(rt);
                
                reportCreated = (rt == dbrt);
                reportId = dbrt.getId();
                
            } catch (Throwable t) {
                failed = true;
                errorMessage = t.getMessage();
            }
        }

        public boolean isFailed () {
            return failed;
        }
        
        public String getErrorMessage () {
            return errorMessage;
        }
        
        public String getJSONResponse () {            
            String result = ":(";
            try {
                JSONObject r = new JSONObject();
                r.put("error", isFailed());
                r.put("error_message", getErrorMessage());
                if (!failed) {
                    r.put("created", reportCreated);
                    r.put("report", reportId);
                }
                result = r.toString();
            } catch (JSONException x) {
                x.printStackTrace(System.err);
            }
            return result;
        }

        private static String[] filterValidFields (PageContext pageContext, SessionData sess, String[] fields) {
            
            if (fields == null || fields.length == 0)
                return fields;

            ArrayList<String> newfields = new ArrayList<String>();
            DataViewer dv = new DataViewer(pageContext, sess, DataViewer.FLAG_NO_ROWS | DataViewer.FLAG_NO_CELLS);
            
            Set<String> columns = new HashSet<String>();
            for (DataViewer.Column col : dv.getColumns())
                columns.add(col.sid);
            
            for (String field : fields)
                if (columns.contains(field))
                    newfields.add(field);
           
            if (newfields.size() != fields.length)
                System.out.println("Note: ReportController dropped some invalid columns " + fields.length + " => " + newfields.size());
            
            return newfields.toArray(new String[newfields.size()]);
            
        }
        
    }
    
    public static class View {
        
        private long myReportId;
        private ReportTemplate myReportTemplate;
        private Set<String> myReportFields;
        private DataViewer dataViewer;
        private DataViewer myReportDataViewer;
        private List<Section> myReportSections;
                
        public View (ReportParameters rp, PageContext page, SessionData sess) throws Exception {

            if (!sess.isLoggedIn() || !sess.getUser().getRole().canViewUsers())
                throw new SecurityException("Permission denied.");
            
            myReportId = rp.getId();
            myReportTemplate = (myReportId >= 0 ? ReportTemplate.findById(myReportId) : null);
            if (myReportTemplate != null) {
                DataViewer.Parameters params = new DataViewer.Parameters();
                params.qf = myReportTemplate.getFilter(); 
                params.sortby = 0; // first column. todo: let user choose sort.
                myReportFields = new HashSet<String>(Arrays.asList(myReportTemplate.getFieldList()));
                myReportDataViewer = new DataViewer(page, sess, DataViewer.FLAG_NO_CELLS, myReportFields, params);
            }
            
            DataViewer.Parameters params = new DataViewer.Parameters();
            dataViewer = new DataViewer(page, sess, DataViewer.FLAG_NO_CELLS | DataViewer.FLAG_NO_ROWS, null, params);

        }
        
        public Map<String,List<DataViewer.Column>> getReportColumns () {
            return dataViewer.getColumnsByCategory();
        }
        
        public String getLinkHTML (HttpServletRequest request, String format) {
            if (myReportTemplate == null)
                return "<span class=\"nothing-yet\">Generate the report to get a link.";
            else {
                String url = getURL(request, format);
                return String.format("<a href=\"%s\">%s</a>", url, url);
            }
        }
        
        public String getURL (HttpServletRequest request, String format) {
            String url = "report/" + myReportId + (format == null ? "" : ("/" + format));
            return Util.getAbsoluteUrl(request, url);
        }
        
        public boolean isMyReportColumnSelected (String sid) {
            return myReportFields != null && myReportFields.contains(sid);
        }
        
        public int getMyReportFilter () {
            return myReportTemplate == null ? 11 : myReportTemplate.getFilter();
        }
        
        public int getMyReportCells () {
            return myReportTemplate == null ? ReportTemplate.CELLS_NONE : myReportTemplate.getCells();
        }
        
        public boolean getMyReportExcludeMandatoryCells () {
            return myReportTemplate == null ? true : myReportTemplate.getExcludeMandatoryCells();
        }
        
        public Long getMyReportId () {
            return myReportTemplate == null ? null : myReportTemplate.getId();
        }
        
        private List<DataViewer.Column> myReportColumns;
        
        public List<DataViewer.Column> getMyReportColumns () {
            if (myReportDataViewer == null)
                return null;
            if (myReportColumns == null) {
                myReportColumns = myReportDataViewer.getColumns();
                if (myReportTemplate.getCells() == ReportTemplate.CELLS_LIST) {
                    myReportColumns = new ArrayList<DataViewer.Column>(myReportColumns);
                    DataViewer.Column cellColumn = DataViewer.newColumn(false);
                    cellColumn.name = myReportTemplate.getCells() == ReportTemplate.CELLS_LIST ? "Cells" : "Cells (Non-Mandatory)";
                    myReportColumns.add(cellColumn);
                }
            }
            return myReportColumns;
        }
        
        public String getMyReportTitle () {
            return myReportTemplate == null ? null : myReportTemplate.getTitle();
        }
        
        private static void buildCellList (List<Cell> cs, Cell c) {
            if (c.isCategory()) {
                for (Cell cell:c.getChildren())
                    buildCellList(cs, cell);
            } else {
                cs.add(c);
            }
        }
        
        public List<Section> getMyReportSections () {
            
            if (myReportDataViewer == null)
                return null;
            
            if (myReportSections == null) {
                myReportSections = new ArrayList<Section>();
         
                if (myReportTemplate.getCells() == ReportTemplate.CELLS_SPLIT) {
                    
                    // Build sections per cell. We need to keep the users in sort order so
                    // build this by user rather than by cell. First create sections.
                    List<Cell> cells = new ArrayList<Cell>();
                    buildCellList(cells, Cell.findRoot());
                    Map<Long,Section> cellSections = new LinkedHashMap<Long,Section>();
                    for (Cell cell : cells)
                        if (!myReportTemplate.getExcludeMandatoryCells() || !cell.isMandatory())
                            cellSections.put(cell.getCellId(), new Section(cell.getFullName()));
                    cellSections.put(-1L, new Section(myReportTemplate.getExcludeMandatoryCells() ? "No Non-Mandatory Cells" : "No Cells"));
                    
                    // Now distribute rows to sections in order of users.
                    for (DataViewer.Row row : myReportDataViewer.getRows()) {
                        boolean incells = false;
                        for (Cell cell : row.user.getCells()) {
                            Section s = cellSections.get(cell.getCellId());
                            if (s != null) {
                                s.rows.add(row);
                                incells = true;
                            }
                        }
                        if (!incells) {
                            Section s = cellSections.get(-1L);
                            s.rows.add(row);
                        }
                    }
                    
                    // Then prune empty cells.
                    for (Section section : cellSections.values())
                        if (!section.rows.isEmpty())
                            myReportSections.add(section);
                    
                } else {
                    
                    List<DataViewer.Row> rows = myReportDataViewer.getRows(); 
                    
                    // Add cell lists if necessary. Note: We're modifying DataViewer stuff internally here
                    // which is weird but in this context causes no harm.
                    if (myReportTemplate.getCells() == ReportTemplate.CELLS_LIST) {
                        // Do this per cell to keep everything in order.
                        List<Cell> cells = new ArrayList<Cell>();
                        buildCellList(cells, Cell.findRoot());
                        Map<Long,List<Cell>> userCells = new HashMap<Long,List<Cell>>();
                        for (Cell cell : cells) {
                            for (User user : cell.getUsers()) {
                                long id = user.getUserId();
                                List<Cell> uc = userCells.get(id);
                                if (uc == null) {
                                    uc = new ArrayList<Cell>();
                                    userCells.put(id, uc);
                                }
                                uc.add(cell);
                            }
                        }
                        // Now add info.
                        for (DataViewer.Row row : rows) {
                            row.hrefs.add(null);
                            List<Cell> cellList = userCells.get(row.user.getUserId());
                            String cellstr = "";
                            if (cellList != null) {
                                for (Cell cell : cellList) {
                                    if (!myReportTemplate.getExcludeMandatoryCells() || !cell.isMandatory())
                                        cellstr += "• " + cell.getFullName() + "\n";
                                }
                            }
                            cellstr = cellstr.trim();
                            //if (cellstr.isEmpty())
                            //    cellstr = myReportTemplate.getExcludeMandatoryCells() ? "No Non-Mandatory Cells" : "None";
                            row.values.add(cellstr);
                        }
                    }

                    // One section with all the rows.
                    Section s = new Section();
                    s.rows = rows;
                    myReportSections.add(s);
                                        
                }
                
            }

            return myReportSections;
            
        }
        
        public static class Section {
            public String title;
            public List<DataViewer.Row> rows = new ArrayList<DataViewer.Row>();
            Section () { }
            Section (String title) { this.title = title; }
        }
        
    }
       
}
