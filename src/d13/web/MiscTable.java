package d13.web;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import d13.util.Util;

/**
 * A thing for rendering basic table-style data to HTML, CSV, etc. Not used in the main site, 
 * this is mostly for all the various hacky utilities in /misc/.
 */
public class MiscTable {

    public static class Style implements Cloneable {
        public static final Style DEFAULT = new Style();
        private String cssClass;
        private boolean bulletList;
        public Style css (String cssClass) {
            Style copy = clone();
            copy.cssClass = cssClass;
            return copy;
        }
        public Style bulleted (boolean bulletList) {
            Style copy = clone();
            copy.bulletList = bulletList;
            return copy;
        }
        @Override protected Style clone () {
            Style copy = new Style();
            copy.cssClass = this.cssClass;
            copy.bulletList = this.bulletList;
            return copy;
        }
    }

    public static class Cell {
        private final Style style;
        private final String text;
        public Cell (String text, Style style) {
            this.style = style;
            this.text = text;
        }
    }
    
    public static class Row {      
        private final List<Cell> cells = new ArrayList<Cell>();
        public void add (String s) {
            add(s, null);
        }
        public void add (String s, Style t) {
            cells.add(new Cell(s, t));
        }
        public void clear () {
            cells.clear();
        }
        public String getText (int n) {
            return cells.get(n).text;
        }
        public List<String> getText () {
            List<String> vals = new ArrayList<String>();
            for (Cell c : cells)
                vals.add(c.text);
            return vals;
        }
    }
    
    private final Row header = new Row();
    private final List<Row> rows = new ArrayList<Row>();
    private final List<Style> defaultStyles = new ArrayList<Style>();
    private boolean showHeader = true;
    private String htmlTableAttrs = "border=\"1\"";
    
    public MiscTable setShowHeader (boolean showHeader) {
        this.showHeader = showHeader;
        return this;
    }
    
    public MiscTable setHTMLTableAttributes (String attrs) {
        this.htmlTableAttrs = attrs;
        return this;
    }
    
    private boolean isHeaderVisible () {
        return showHeader && !header.cells.isEmpty();
    }
  
    public void addHeader (String text) {
        header.add(text);
    }
    
    public void addHeader (String text, Style style) {
        header.add(text, style);
    }
    
    public void setHeader (String ... values) {
        header.clear();
        for (String value : values)
            header.add(value);
    }
    
    public void setDefaultStyles (Style ... styles) {
        defaultStyles.clear();
        for (Style style : styles)
            defaultStyles.add(style);
    }
    
    public void setDefaultStyles (String ... css) {
        defaultStyles.clear();
        for (String c : css)
            defaultStyles.add(Style.DEFAULT.css(c));
    }
    
    public Row addRow () {
        Row row = new Row();
        rows.add(row);
        return row;
    }
     
    public void toCSV (Appendable out) throws IOException {
        CSVPrinter p = new CSVPrinter(out, CSVFormat.EXCEL);
        if (isHeaderVisible())
            p.printRecord(header.getText());
        for (Row r : rows)
            p.printRecord(r.getText());
        p.flush();
    }
    
    public String toCSV () {
        String str;
        try {
            StringWriter out = new StringWriter();
            toCSV(out);
            out.flush();
            out.close();
            str = out.toString();
        } catch (IOException x) {
            str = "";
        }
        return str;
    }
    
    private Style getStyle (Row row, int column) {
        Style style = null;
        if (row != null && column >= 0 && column < row.cells.size())
            style = row.cells.get(column).style;
        if (style == null && column >= 0 && column < defaultStyles.size())
            style = defaultStyles.get(column);
        if (style == null)
            style = Style.DEFAULT;
        return style;
    }
    
    private void printHTMLRecord (Appendable out, Row row, String te) throws IOException {
        out.append("<tr>\n");
        for (int n = 0; n < row.cells.size(); ++ n) {
            Style style = getStyle(row, n);
            String cls = (style.cssClass != null ? (" class=\"" + style.cssClass + "\"") : "");
            out.append("  <" + te + cls + ">");
            if (style.bulletList) {
                String[] data = row.getText(n).trim().split("\\n");
                for (String s : data) {
                    s = s.trim();
                    if (!s.isEmpty()) {
                        out.append("&bull; ");
                        out.append(Util.html(s));
                        out.append("<br>\n");
                    }
                }
            } else {
                out.append(Util.html(row.getText(n)));
            }
            out.append("\n");
        }
    }
    
    public void toHTMLTable (Appendable out) throws IOException {
        out.append("<table " + (htmlTableAttrs == null ? "" : htmlTableAttrs) + ">\n");
        if (isHeaderVisible())
            printHTMLRecord(out, header, "th");
        for (Row r : rows)
            printHTMLRecord(out, r, "td");
        out.append("</table>");
    }
    
    public String toHTMLTable () {
        String str;
        try {
            StringWriter out = new StringWriter();
            toHTMLTable(out);
            out.flush();
            out.close();
            str = out.toString();
        } catch (IOException x) {
            str = "";
        }
        return str;
    }
    
}
