package d13.web;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;

import d13.dao.ApprovalSurvey;
import d13.dao.Cell;
import d13.dao.RegistrationForm;
import d13.dao.User;
import d13.dao.UserSearchFilter;
import d13.util.Util;

public class DataViewer {
    
    private abstract static class ViewDescriptor implements Comparable<ViewDescriptor> {
        String field;
        int position;
        boolean longtext;
        boolean email;
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
    
    private boolean failed;
    private static final List<ViewDescriptor> userProps = getDataViewProps(User.class);
    private static final List<ViewDescriptor> rformProps = getDataViewProps(RegistrationForm.class);
    private static final List<ViewDescriptor> aformProps = getDataViewProps(ApprovalSurvey.class);
    private final List<ViewDescriptor> cellProps;
    private final List<String> columns; // must be same order as getDataViewRow
    private final List<String> colclasses;
    private final List<Row> rows = new ArrayList<Row>();
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
                vd.field = sub.getFullName();
                vd.position = props.size();
                vd.longtext = false;
                vd.cell = sub;
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
    
    private static List<ViewDescriptor> getDataViewProps (Class<?> c) {
    
        PropertyDescriptor[] all = PropertyUtils.getPropertyDescriptors(c);
        List<ViewDescriptor> viewed = new ArrayList<ViewDescriptor>();
        BeanViewDescriptor view;
        
        for (PropertyDescriptor prop:all) {
            view = new BeanViewDescriptor();
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
            if (!dv.n().isEmpty()) view.field = dv.n();
            viewed.add(view);
        }
        
        Collections.sort(viewed);
        return viewed;
        
    }
    
    private static List<String> getDataViewColumns (List<ViewDescriptor> ... lists) {
        
        List<String> columns = new ArrayList<String>();
       
        for (List<ViewDescriptor> vds:lists)
            for (ViewDescriptor vd:vds) {
                columns.add(vd.field);
                
            }
        
        return columns;
        
    }
    
    private static List<String> getDataViewColumnClasses (List<ViewDescriptor> ... lists) {
        
        List<String> columns = new ArrayList<String>();
       
        for (List<ViewDescriptor> vds:lists)
            for (ViewDescriptor vd:vds)
                columns.add(vd.longtext ? "wide" : "standard");
        
        return columns;
        
    }
    
    private static String href (String val, boolean email) {
        if (email)
            return "mailto:" + val + "?subject=Disorient 2013 Registration";
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
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static int compareObjects (Object a, Object b) {
          
            if (a == b)
                return 0;
            else if (a == null || b == null)
                return a == null ? -1 : 1;
           
            if (a.getClass().equals(b.getClass())) {
                if (a instanceof Comparable) {
                    Comparable ac = (Comparable)a;
                    Comparable bc = (Comparable)b;
                    return ac.compareTo(bc);
                }
            }
            
            return a.toString().compareTo(b.toString());
            
        }
        
    }
    
    public DataViewer (PageContext context, SessionData session) {
        this(context, session, 0);
    }
    
    public static final int FLAG_SINGLE_USER = 1<<0;
    public static final int FLAG_NO_CELLS = 1<<1;
    
    @SuppressWarnings("unchecked")
    public DataViewer (PageContext context, SessionData session, int flags) {
        
        User current = session.getUser();
        if (!current.getRole().canViewUsers()) {
            failed = true;
            columns = null; // kludge
            colclasses = null;
            cellProps = null; // same
            return; // permission denied
        }

        if ((flags & FLAG_NO_CELLS) == 0) {
            cellProps = getCellProps();
            columns = Collections.unmodifiableList(getDataViewColumns(userProps, rformProps, cellProps, aformProps));
            colclasses = Collections.unmodifiableList(getDataViewColumnClasses(userProps, rformProps, cellProps, aformProps));
        } else {
            cellProps = null;
            columns = Collections.unmodifiableList(getDataViewColumns(userProps, rformProps, aformProps));
            colclasses = Collections.unmodifiableList(getDataViewColumnClasses(userProps, rformProps, aformProps));
        }
                
        if ((flags & FLAG_SINGLE_USER) != 0) {
            
            Long id = Util.getParameterLong(context.getRequest(), "u");
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
            
        } else {
            
            downloadCSV = (context.getRequest().getParameter("download") != null);
            
            int sortby = intParam(context.getRequest().getParameter("sortby"));
            int qf = intParam(context.getRequest().getParameter("qf"));
            String search = context.getRequest().getParameter("search");
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
   
    public List<String> getColumns () {
        return columns;
    }
    
    public List<String> getColumnClasses () {
        return colclasses;
    }
  
    public List<Row> getRows () {
        return rows;
    }
    
    public boolean isFailed () {
        return failed;
    }
    
    public boolean isDownloadCSV () {
        return downloadCSV;
    }
 
    public void writeCSV (JspWriter out) throws IOException {
        
        boolean first = true;
        
        for (String s:getColumns()) {
            if (first)
                first = false;
            else
                out.print(",");
            out.print(StringEscapeUtils.escapeCsv(s));
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
