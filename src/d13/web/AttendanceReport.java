package d13.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import d13.ThisYear;
import d13.dao.Cell;
import d13.dao.User;
import d13.dao.UserState;

public class AttendanceReport {

    private boolean failed;
    private String errorMessage;
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
    private LocalDate firstDate = null;
    private LocalDate lastDate = null;
    private List<LocalDate> dateColumns = new ArrayList<LocalDate>();
    private List<Row> rows = new ArrayList<Row>();
    
    private class ParsedUser {
        final User user;
        final LocalDate arrive;
        final LocalDate depart;
        ParsedUser (User user) {
            this.user = user;
            if (user.isRegistrationComplete()) {
                this.arrive = LocalDate.parse(user.getRegistration().getArrivalDate(), dtf);
                this.depart = LocalDate.parse(user.getRegistration().getDepartureDate(), dtf);
                if (firstDate == null || this.arrive.isBefore(firstDate))
                    firstDate = this.arrive;
                if (lastDate == null || this.depart.isAfter(lastDate))
                    lastDate = this.depart;
            } else {
                this.arrive = null;
                this.depart = null;
            }
        }
    }
    
    public static class Row {
        public final String name;
        public final List<Integer> valuesAll = new ArrayList<Integer>();
        public final List<Integer> valuesApproved = new ArrayList<Integer>();
        public final List<Integer> valuesFinalized = new ArrayList<Integer>();
        Row (String name) {
            this.name = name;
        }
    }
    
    private static void buildCellList (List<Cell> cs, Cell c) {
        if (c.isCategory()) {
            for (Cell cell:c.getChildren())
                buildCellList(cs, cell);
        } else {
            cs.add(c);
        }
    }

    public AttendanceReport (PageContext page, SessionData sess) {
        
        try {
            
            List<ParsedUser> users = new ArrayList<ParsedUser>();
            for (User user : User.findAllExceptNew())
                users.add(new ParsedUser(user));
            
            if (firstDate == null || lastDate == null)
                return; // nobody's going to burning man :(
            
            for (LocalDate date = firstDate.minusDays(1); !date.isAfter(lastDate.plusDays(1)); date = date.plusDays(1))
                dateColumns.add(date);

            List<Cell> cells = new ArrayList<Cell>();
            buildCellList(cells, Cell.findRoot());
            
            rows.add(createRow(dateColumns, users, null));
            for (Cell cell : cells)
                rows.add(createRow(dateColumns, users, cell));
            
        } catch (Throwable t) {
            if (!(t instanceof SecurityException))
                t.printStackTrace(System.err);
            failed = true;
            errorMessage = t.getMessage();
        }
        
    }
    
    private static Row createRow (List<LocalDate> columns, List<ParsedUser> users, Cell cell) {
        
        Row row = new Row(cell == null ? "Total" : cell.getFullName());
        
        for (LocalDate date : columns) {
            int all = 0, approved = 0, finalized = 0;
            for (ParsedUser user : users) {
                if (cell != null && !user.user.isInCell(cell))
                    continue;
                if (date.isBefore(user.arrive) || date.isAfter(user.depart))
                    continue;
                if (user.user.getState() == UserState.REJECT_PENDING || user.user.getState() == UserState.REJECTED)
                    continue;
                ++ all;
                if (user.user.getState() == UserState.APPROVED) {
                    ++ approved;
                    ++ finalized;
                } else if (user.user.getState() == UserState.APPROVE_PENDING) {
                    ++ approved;
                }
            }
            row.valuesAll.add(all);
            row.valuesApproved.add(approved);
            row.valuesFinalized.add(finalized);
        }
        
        return row;
        
    }
    
    public boolean isFailed () {
        return failed;
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    private static String getPeriod (LocalDate date) {
        DateTime dt = date.toDateTime(new LocalTime(12, 0));
        return ThisYear.PlayaWeek.getPhaseShortName(dt);
    }
    
    public JSONObject getEmbeddedData () {
        JSONObject data = new JSONObject();
        try {
            for (LocalDate date : dateColumns) {
                data.append("columns1", date.toString("EEE"));
                data.append("columns2", date.toString("M/d"));
                data.append("period", getPeriod(date));
            }
            for (Row row : rows) {
                JSONObject jrow = new JSONObject();
                jrow.put("name", row.name);
                jrow.append("data", new JSONArray(row.valuesAll));
                jrow.append("data", new JSONArray(row.valuesApproved));
                jrow.append("data", new JSONArray(row.valuesFinalized));
                data.append("rows", jrow);
            }
        } catch (JSONException x) {
        }
        return data;
    }
    
}
