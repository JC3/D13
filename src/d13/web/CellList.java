package d13.web;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import d13.dao.Cell;
import d13.dao.User;
import d13.util.Util;

public class CellList {

    public static void writeCellTree (JspWriter out, Cell root, User user) throws IOException {
        
        if (root != null) {
            for (Cell cell:root.getChildren()) {
                if (cell.isCategory())
                    writeCellTree(out, cell, user);
                else
                    writeCell(out, cell, user);
            }
        }
        
    }
    
    private static void writeCell (JspWriter out, Cell cell, User user) throws IOException {
      
        String volunteers;        
        String trstyle = "";
        String titlestyle = "";
       
        boolean cellfull = false;
        
        int total = cell.getPeople();
        if (total == 0) {
            volunteers = "This cell still needs volunteers.";
            cellfull = !cell.getUsers().isEmpty();
        } else {
            int sofar = cell.getUsers().size();
            int remaining = total - sofar;
            if (remaining <= 0) {
                volunteers = null;
                cellfull = true;
            } else {
                volunteers = String.format("This cell still needs %s volunteer%s.", remaining, remaining == 1 ? "" : "s");
            }
        }
        
        boolean isInCell = user.isInCell(cell);
        
        if ((cellfull && cell.isHideWhenFull()) || cell.isHidden()) {
            if (user.getRole().canViewFullCells()) {
                trstyle = "";
                if (cell.isHidden())
                    titlestyle = " style=\"color:cyan;\""; // hidden cells are blue i guess
                else
                    titlestyle = " style=\"color:red;\""; // full cells are red
            } else if (!isInCell) {
                trstyle = " style=\"display:none;\"";
                titlestyle = "";
            }
        }
        
        String tdstyle = "";
        String titleexclass = "";
        boolean mandatory = cell.isMandatoryFor(user);
        if (mandatory) {
            tdstyle = " class=\"mandatory-cell-left\"";
            titleexclass += " mandatory-cell-title";
        }
        
        out.println(String.format("<tr%s><td%s width=\"100%%\">", trstyle, tdstyle));
        out.println(String.format("<div class=\"cellname%s\"%s>%s%s</div>", titleexclass, titlestyle, mandatory ? "REQUIRED: " : "", Util.html(cell.getFullName())));
                
        if (volunteers != null)
            out.println(String.format("<div class=\"cellvol\">%s</div>", Util.html(volunteers)));
     
        String desc = cell.getDescription();
        if (desc != null && !desc.trim().isEmpty()) {
            out.println(String.format("<div class=\"celllink\" id=\"celllink_%s\"><a href=\"#\" onclick=\"return showMore(%s);\">[ Read More ]</a></div>", cell.getCellId(), cell.getCellId()));
            out.println(String.format("<div class=\"celldesc\" id=\"celldesc_%s\">%s</div>", cell.getCellId(), addLineBreaks(Util.html(desc))));
        }
        
        out.println(String.format("<td class=\"cellcheck\"><div><input type=\"hidden\" name=\"xc\" value=\"%s\"><label><input type=\"checkbox\" name=\"c\" value=\"%s\"%s>Volunteer!</label></div>", cell.getCellId(), cell.getCellId(), isInCell ? " checked" : ""));
        
    }
     
    
    private static String addLineBreaks (String html) {
        
        return html.trim().replaceAll("\\n", "<br>").replaceAll("\\r", "");
        
    }
    
    
}
