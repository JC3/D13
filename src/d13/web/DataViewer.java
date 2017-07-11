package d13.web;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;

import d13.ThisYear;
import d13.dao.ActivityLogEntry;
import d13.dao.ApprovalSurvey;
import d13.dao.Cell;
import d13.dao.Comment;
import d13.dao.RegistrationForm;
import d13.dao.Role;
import d13.dao.User;
import d13.dao.UserSearchFilter;
import d13.dao.UserState;
import d13.util.Util;

public class DataViewer {
    
    private abstract static class ViewDescriptor implements Comparable<ViewDescriptor> {
        String id;
        String field;
        int position;
        boolean longtext;
        boolean email;
        String category;
        public abstract String getString (Object o);
        public abstract Object getObject (Object o);
        @Override public int compareTo (ViewDescriptor other) {
            if (other == null)
                return -1;
            else if (other == this)
                return 0;
            else if (position == -1 && other.position == -1)
                return 0;
            else if (position == -1)
                return 1;
            else if (other.position == -1)
                return -1;
            else
                return position - other.position;
        }
    }
    
    private static class BeanViewDescriptor extends ViewDescriptor {
        Method read;
        DataConverter converter;
        @Override
        public String getString (Object o) {
            if (o == null) {
                return "";
            } else {
                try {
                    Object value = read.invoke(o);
                    String valuestr = converter.asString(value);
                    return (valuestr == null ? "" : valuestr);
                } catch (Throwable t) {
                    t.printStackTrace();
                    return "<ERROR>";
                }
            }
        }
        @Override
        public Object getObject (Object o) {
            if (o != null) {
                try {
                    return read.invoke(o);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            return null;
        }
    }
    
    private static class CellViewDescriptor extends ViewDescriptor {
        Cell cell;
        @Override
        public String getString (Object o) {
            if (o == null || !(o instanceof User))
                return "";
            else
                return ((User)o).isInCell(cell) ? "Yes" : "";
        }
        @Override
        public Object getObject (Object o) {
            if (o == null || !(o instanceof User))
                return false;
            else
                return ((User)o).isInCell(cell);
        }
    }
    
    public static class Row {
        public User user;
        public List<String> values;
        public List<String> hrefs;
        private List<Object> sortvalues;
    }
    
    public static class Column {
        public String sid;
        public String name;
        public String longClass;
        public String shortClass;
        public String category;
    }
        
    private boolean failed;
    private static final List<ViewDescriptor> userPropsFull = getDataViewProps(User.class, "Profile", null);
    private static final List<ViewDescriptor> rformPropsFull = getDataViewProps(RegistrationForm.class, "Registration", null);
    private static final List<ViewDescriptor> aformPropsFull = getDataViewProps(ApprovalSurvey.class, "Survey", null);
    private final List<ViewDescriptor> userProps;
    private final List<ViewDescriptor> rformProps;
    private final List<ViewDescriptor> aformProps;
    private final List<ViewDescriptor> cellProps;
    private final List<Column> columns; // must be same order as getDataViewRow
    private final List<Integer> wideIndices = new ArrayList<Integer>();
    private final List<Integer> cellIndices = new ArrayList<Integer>();
    private final List<Row> rows = new ArrayList<Row>();
    private List<Note> notes;
    private boolean downloadCSV;
    private User theSingleUser; 
    
    public int getProfileBorderIndex () {
        return userProps.size() - 1;
    }
    
    public int getRegistrationBorderIndex () {
        return userProps.size() + rformProps.size() - 1;
    }
    
    public int getCellBorderIndex () {
        if (cellProps == null)
            return -1;
        else
            return userProps.size() + rformProps.size() + cellProps.size() - 1;
    }
    
    public int getApprovalBorderIndex () {
        return userProps.size() + rformProps.size() + (cellProps == null ? 0 : cellProps.size()) + aformProps.size() - 1;        
    }
    
    private static void addCellProps (List<ViewDescriptor> props, Cell cell) {
        
        if (cell == null)
            return;
      
        for (Cell sub:cell.getChildren()) {
            if (sub.isCategory())
                addCellProps(props, sub);
            else {
                CellViewDescriptor vd = new CellViewDescriptor();
                vd.id = String.format("cell-%d", sub.getCellId());
                vd.field = sub.getFullName();
                vd.position = props.size();
                vd.longtext = false;
                vd.cell = sub;
                vd.category = "Cells";
                props.add(vd);
            }
        }
        
    }
    
    private static List<ViewDescriptor> getCellProps () {
        List<ViewDescriptor> viewed = new ArrayList<ViewDescriptor>();
        try {
            addCellProps(viewed, Cell.findRoot());
        } catch (Throwable t) {
        }
        return viewed;
    }
    
    private static List<ViewDescriptor> getDataViewProps (Class<?> c, String category, Collection<String> onlyFields) {
    
        PropertyDescriptor[] all = PropertyUtils.getPropertyDescriptors(c);
        List<ViewDescriptor> viewed = new ArrayList<ViewDescriptor>();
        BeanViewDescriptor view;
        
        for (PropertyDescriptor prop:all) {
            if (onlyFields != null && !onlyFields.contains(prop.getName()))
                continue;
            view = new BeanViewDescriptor();
            view.id = prop.getName();
            view.field = prop.getName();
            view.read = prop.getReadMethod();
            if (view.read == null) continue;
            DataView dv = view.read.getAnnotation(DataView.class);
            if (dv == null) continue;
            try {
                view.converter = dv.value().newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            view.position = dv.i();
            view.longtext = dv.longtext();
            view.email = dv.email();
            view.category = category;
            if (!dv.n().isEmpty()) view.field = dv.n();
            viewed.add(view);
        }
        
        Collections.sort(viewed);
        return viewed;
        
    }
    
    @SafeVarargs
    private static List<Column> getDataViewColumns (List<ViewDescriptor> ... lists) {
        
        List<Column> columns = new ArrayList<Column>();
       
        for (List<ViewDescriptor> vds:lists)
            for (ViewDescriptor vd:vds) {
                Column c = new Column();
                c.name = vd.field;
                c.sid = vd.id;
                c.shortClass = vd.longtext ? "w" : null; // todo just store longtext flag move classes to views
                c.longClass = vd.longtext ? "wide" : "standard";
                c.category = vd.category;
                columns.add(c);               
            }
        
        return columns;
        
    }
    
    @SafeVarargs
    private final void setupDataViewColumnIndices (List<ViewDescriptor> ... lists) {
        
        wideIndices.clear();
        cellIndices.clear();
        
        int n = 0;
        for (List<ViewDescriptor> vds:lists) {
            for (ViewDescriptor vd:vds) {
                if (vd.longtext) wideIndices.add(n);
                if (vd instanceof CellViewDescriptor) cellIndices.add(n);
                ++ n;
            }
        }
                
    }

    private static String href (String val, boolean email) {
        if (email)
            return "mailto:" + val + "?subject=Disorient " + ThisYear.CAMP_YEAR + " Registration";
        else
            return null;
    }

    private Row getDataViewRow (User user, User current, boolean nocells) {
    
        Row row = new Row();
        row.user = user;
        row.values = new ArrayList<String>();
        row.hrefs = new ArrayList<String>();
        row.sortvalues = new ArrayList<Object>();
        
        for (ViewDescriptor vd:userProps) {
            String str = vd.getString(user);
            row.values.add(str);
            row.hrefs.add(href(str, vd.email));
            row.sortvalues.add(vd.getObject(user));
        }
        
        RegistrationForm rform = user.isRegistrationComplete() ? user.getRegistration() : null;
        for (ViewDescriptor vd:rformProps) {
            String str = vd.getString(rform);
            row.values.add(str);
            row.hrefs.add(href(str, vd.email));
            row.sortvalues.add(vd.getObject(rform));
        }

        if (!nocells) {
            for (ViewDescriptor vd:cellProps) {
                String str = vd.getString(user);
                row.values.add(str);
                row.hrefs.add(href(str, vd.email));
                row.sortvalues.add(vd.getObject(user));
            }
        }
        
        ApprovalSurvey aform = user.isApprovalComplete() ? user.getApproval() : null;
        for (ViewDescriptor vd:aformProps) {
            String str = vd.getString(aform);
            row.values.add(str);
            row.hrefs.add(href(str, vd.email));
            row.sortvalues.add(vd.getObject(aform));
        }
        
        //for (int n = 0; n < row.values.size(); ++ n)
        //    if (row.sortvalues.get(n) instanceof Role)
        //        System.out.println(n + " " + row.sortvalues.get(n).getClass().getSimpleName() + " => " + row.values.get(n));
        
        return row;
        
    }
    
    private static int intParam (String value) {
        if (value == null)
            return 0;
        try {
            return Integer.parseInt(value);
        } catch (Throwable t) {
            return 0;
        }
    }
    
    private static class ColumnValueComparator implements Comparator<Row> {
        
        final int index;
        
        ColumnValueComparator (int index) {
            this.index = index;
        }

        @Override public int compare (Row a, Row b) {
            try {
                //return a.values.get(index).compareToIgnoreCase(b.values.get(index));
                return compareObjects(a.sortvalues.get(index), b.sortvalues.get(index));
            } catch (Throwable t) {
                return 0;
            }
        }

        private static int compareDataStrings (String astr, String bstr) {
            
            astr = astr.trim();
            bstr = bstr.trim();
            int cmp = astr.compareToIgnoreCase(bstr);
            
            if (cmp != 0) {
                if (astr.isEmpty())
                    return 1;
                else if (bstr.isEmpty())
                    return -1;
            }
            
            return cmp;

        }
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static int compareObjects (Object a, Object b) {
          
            if (a == b)
                return 0;
            else if (a == null || b == null)
                return a == null ? 1 : -1; // 2015-06-27: order swapped to put empty stuff at end
            
            // special case for role to mix Role.DEFAULT_ROLE with hibernate/javassist Role subclasses. 7/18/2015
            if (a.getClass().equals(b.getClass()) || (a instanceof Role && b instanceof Role)) {
                // additional special cases can go here
                if (a instanceof String) {
                    // 2015-06-27: empty strings at end instead of beginning; also case-insensitive
                    return compareDataStrings((String)a, (String)b);
                } else if (a instanceof UserState) {
                    return ((UserState)a).compareToOrdered((UserState)b);
                } else if (a instanceof Comparable) {
                    try {
                        Comparable ac = (Comparable)a;
                        Comparable bc = (Comparable)b;
                        return ac.compareTo(bc);
                    } catch (Throwable t) {
                        // fall through to string compare
                        //System.out.println("dv warn: when comparing: " + t.getMessage());
                    }
                }
            } else {
                //System.out.println("dv warn: classes not equal " + a.getClass().getSimpleName() + " " + b.getClass().getSimpleName());
            }
            
            // normal string compare
            //return a.toString().compareTo(b.toString());
            
            // 2015-06-27: empty strings at end instead of beginning; also case-insensitive
            return compareDataStrings(a.toString(), b.toString());
            
        }
        
    }
    
    public DataViewer (PageContext context, SessionData session) {
        this(context, session, 0, null, null);
    }
    
    public DataViewer (PageContext context, SessionData session, int flags) {
        this(context, session, flags, null, null);
    }
    
    public static final int FLAG_SINGLE_USER = 1<<0;
    public static final int FLAG_NO_CELLS = 1<<1;
    public static final int FLAG_NO_ROWS = 1<<2;
    
    public static class Parameters {
        Long u;
        int sortby;
        int qf;
        String search;
        boolean download;
    }
    
    public DataViewer (PageContext context, SessionData session, int flags, Collection<String> onlyFields, Parameters params) {

        User current = session.getUser();
        if (!current.getRole().canViewUsers()) {
            failed = true;
            columns = null; // kludges:
            userProps = null;
            aformProps = null;
            rformProps = null;
            cellProps = null; // end kludges
            return; // permission denied
        }
        
        if (params == null) {
            params = new Parameters();
            params.u = Util.getParameterLong(context.getRequest(), "u");
            params.sortby = intParam(context.getRequest().getParameter("sortby"));
            params.qf = intParam(context.getRequest().getParameter("qf"));
            params.search = context.getRequest().getParameter("search");
            params.download = (context.getRequest().getParameter("download") != null);
        }
        
        if (onlyFields == null) {
            // use the static cached ones
            userProps = userPropsFull;
            rformProps = rformPropsFull;
            aformProps = aformPropsFull;
        } else {
            userProps = getDataViewProps(User.class, "Profile", onlyFields);
            rformProps = getDataViewProps(RegistrationForm.class, "Registration", onlyFields);
            aformProps = getDataViewProps(ApprovalSurvey.class, "Survey", onlyFields);
            flags |= FLAG_NO_CELLS;
        }
        
        if ((flags & FLAG_NO_CELLS) == 0) {
            cellProps = getCellProps();
            columns = Collections.unmodifiableList(getDataViewColumns(userProps, rformProps, cellProps, aformProps));
            setupDataViewColumnIndices(userProps, rformProps, cellProps, aformProps);
        } else {
            cellProps = null;
            columns = Collections.unmodifiableList(getDataViewColumns(userProps, rformProps, aformProps));
            setupDataViewColumnIndices(userProps, rformProps, aformProps);
        }
                
        if ((flags & FLAG_SINGLE_USER) != 0) {
            
            Long id = params.u;
            if (id == null) {
                failed = true;
                return; // missing required parameter
            }
            
            User user;
            try {
                user = User.findById(id);
            } catch (Throwable t) {
                failed = true;
                return; // no such user
            }
            
            if (!user.isViewableBy2(current)) {
                failed = true;
                return; // permission denied
            }
    
            theSingleUser = user;
            rows.add(getDataViewRow(user, current, (flags & FLAG_NO_CELLS) != 0));
            
            if (current.getRole().canViewLogs()) {
                if (notes == null)
                    notes = new ArrayList<Note>();
                for (ActivityLogEntry e : user.getActivityLog())
                    notes.add(Note.from(e));
            }
            
            if (current.getRole().canViewComments()) {
                if (notes == null)
                    notes = new ArrayList<Note>();
                for (Comment c : user.getComments())
                    notes.add(Note.from(c));
            }
            
            if (notes != null)
                Collections.sort(notes);
            
        } else {
            
            downloadCSV = params.download;
            
            if ((flags & FLAG_NO_ROWS) == 0) {
                
                int sortby = params.sortby;
                int qf = params.qf;
                String search = params.search;
                search = (search == null ? "" : search.trim());
                
                try {
                    List<User> users;
                    if (search != "")
                        users = UserSearchFilter.search(search);
                    else
                        users = UserSearchFilter.quickFilter(qf);
                    for (User user:users) {
                        if (user.isViewableBy2(current))
                            rows.add(getDataViewRow(user, current, (flags & FLAG_NO_CELLS) != 0));
                    }
                } catch (Exception x) {
                    System.err.println("ERROR while getting user list: " + x.getMessage());
                    x.printStackTrace();
                    failed = true;
                    return;
                }
                
                Collections.sort(rows, new ColumnValueComparator(sortby));
                
            }
            
        }
        
    }
   
    public List<Column> getColumns () {
        return columns;
    }
    
    public Map<String,List<Column>> getColumnsByCategory () {
        Map<String,List<Column>> cc = new LinkedHashMap<String,List<Column>>();
        for (Column c : columns) {
            List<Column> cs = cc.get(c.category);
            if (cs == null) {
                cs = new ArrayList<Column>();
                cc.put(c.category, cs);
            }
            cs.add(c);
        }
        return cc;
    }
    
    public List<Integer> getWideColumnIndices () {
        return wideIndices;
    }
    
    public List<Integer> getCellColumnIndices () {
        return cellIndices;
    }
  
    public List<Row> getRows () {
        return rows;
    }
    
    public List<Note> getNotes () {
        return notes;
    }
    
    public boolean isFailed () {
        return failed;
    }
    
    public boolean isDownloadCSV () {
        return downloadCSV;
    }
 
    public void writeCSV (JspWriter out) throws IOException {
        
        boolean first = true;
        
        for (Column s:getColumns()) {
            if (first)
                first = false;
            else
                out.print(",");
            out.print(StringEscapeUtils.escapeCsv(s.name));
        }
        out.println();
       
        for (Row r:getRows()) {
            first = true;
            for (String s:r.values) {
                if (first)
                    first = false;
                else
                    out.print(",");
                out.print(StringEscapeUtils.escapeCsv(s));                
            }
            out.println();
        }
        
    }
    
    public User getSingleUser () {
        return theSingleUser;
    }
    
}
