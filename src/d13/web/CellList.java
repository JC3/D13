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
        
        int total = cell.getPeople();
        if (total == 0) {
            volunteers = "This cell still needs volunteers.";
        } else {
            int sofar = cell.getUsers().size();
            int remaining = total - sofar;
            if (remaining <= 0)
                volunteers = null;
            else
                volunteers = String.format("This cell still needs %s volunteer%s.", remaining, remaining == 1 ? "" : "s");
        }
        
        boolean isInCell = user.isInCell(cell);
        
        if (volunteers == null && cell.isHideWhenFull()) {
            if (user.getRole().canViewFullCells()) {
                trstyle = "";
                titlestyle = " style=\"color:red;\"";
            } else if (!isInCell) {
                trstyle = " style=\"display:none;\"";
                titlestyle = "";
            }
        }
        
        out.println(String.format("<tr%s><td width=\"100%%\">", trstyle));
        out.println(String.format("<div class=\"cellname\"%s>%s</div>", titlestyle, Util.html(cell.getFullName())));
                
        if (volunteers != null)
            out.println(String.format("<div class=\"cellvol\">%s</div>", Util.html(volunteers)));
     
        String desc = cell.getDescription();
        if (desc != null && !desc.trim().isEmpty()) {
            out.println(String.format("<div class=\"celllink\" id=\"celllink_%s\"><a href=\"#\" onclick=\"return showMore(%s);\">[ Read More ]</a></div>", cell.getCellId(), cell.getCellId()));
            out.println(String.format("<div class=\"celldesc\" id=\"celldesc_%s\">%s</div>", cell.getCellId(), Util.html(desc)));
        }
        
        out.println(String.format("<td class=\"cellcheck\"><div><input type=\"hidden\" name=\"xc\" value=\"%s\"><input type=\"checkbox\" name=\"c\" value=\"%s\"%s>Volunteer!</div>", cell.getCellId(), cell.getCellId(), isInCell ? " checked" : ""));
        
    }
       
}
