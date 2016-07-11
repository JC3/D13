package d13.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import d13.ThisYear;
import d13.dao.Cell;
import d13.dao.User;
import d13.questions.ApprovalQuestions;
import d13.questions.ProfileQuestions;
import d13.questions.Question;
import d13.questions.RegistrationQuestions;

public class MiscDataExport {
    
    private static enum DataType { TABLE, TEXT };
    
    private boolean failed;
    private boolean download;
    private MiscTable tdata = new MiscTable();
    //private String sdata;
    private DataType type;
    private String downloadFilename;
    
    public MiscDataExport (PageContext context, SessionData session) {

        User current = session.getUser();
        if (!current.getRole().canViewUsers()) {
            failed = true;
            return; // permission denied
        }
        
        String what = context.getRequest().getParameter("what");
        what = (what == null ? "" : what.trim());
        download = (context.getRequest().getParameter("download") != null);
        
        if ("cells".equals(what)) {
            type = exportCells(tdata);
            downloadFilename = ThisYear.CAMP_YEAR + "_cells.csv";
        //} else if ("terms".equals(what)) {
        //    type = exportTerms(sdata);
        } else if ("profile".equals(what)) {
            type = exportQuestions(tdata, ProfileQuestions.getQuestions());
            downloadFilename = ThisYear.CAMP_YEAR + "_profile.csv";
        } else if ("registration".equals(what)) {
            type = exportQuestions(tdata, RegistrationQuestions.getQuestions());
            downloadFilename = ThisYear.CAMP_YEAR + "_registration.csv";
        } else if ("survey".equals(what)) {
            type = exportQuestions(tdata, ApprovalQuestions.getQuestions());
            downloadFilename = ThisYear.CAMP_YEAR + "_survey.csv";
        } else {
            failed = true;
            return; // invalid parameter
        }
        
    }
    
    public boolean isFailed () {
        return failed;
    }
    
    public boolean isDownload () {
        return download;
    }
        
    private static void buildCellList (List<Cell> cs, Cell c) {
        if (c.isCategory()) {
            for (Cell cell:c.getChildren())
                buildCellList(cs, cell);
        } else {
            cs.add(c);
        }
    }

    private DataType exportCells (MiscTable table) {
        
        List<Cell> cells = new ArrayList<Cell>();
        buildCellList(cells, Cell.findRoot());

        table.setDefaultStyles("cat", "name", "people", "hide", "mand", "desc");
        table.setHeader("Category", "Name", "People", "Hide when full?", "Mandatory?", "Description");

        for (Cell c : cells) {
            MiscTable.Row row = table.addRow();
            row.add(c.getParentName());
            row.add(c.getName());
            row.add(Integer.toString(c.getPeople()));
            row.add(c.isHideWhenFull() ? "Yes" : "No");
            row.add(c.isMandatory() ? "Yes" : "No");
            row.add(c.getDescription());
        }

        return DataType.TABLE;
        
    }
    
    private DataType exportQuestions (MiscTable table, List<Question> qs) {
        
        table.setDefaultStyles("type", "brief", "detail", "choices");
        table.setHeader("Type", "Brief", "Detail", "Choices");
      
        for (Question q : qs) {
            
            if (q.isForLogin())
                continue;
         
            String typestr;
            switch (q.getType()) {
            case Question.TYPE_DROPLIST: typestr = "Drop List"; break;
            case Question.TYPE_LONG_TEXT: typestr = "Long Text"; break;
            case Question.TYPE_MULTI_CHOICE: typestr = "Multi Choice"; break;
            case Question.TYPE_PASSWORD: typestr = "Password"; break;
            case Question.TYPE_SHORT_TEXT: typestr = "Short Text"; break;
            case Question.TYPE_SINGLE_CHOICE: typestr = "Single Choice"; break;
            case Question.TYPE_USER_DROPLIST: typestr = "User List"; break;
            default: typestr = Integer.toString(q.getType()); break;
            }
            
            MiscTable.Row row = table.addRow();
            row.add(typestr);
            row.add(q.getBrief());
            row.add(q.getDetail());
            
            List<Question.Choice> choices = q.getChoices();
            if (choices == null) {
                row.add("");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Question.Choice c : choices)
                    sb.append(c.isOther() ? "Other" : c.getText()).append("\n");
                row.add(sb.toString(), MiscTable.Style.DEFAULT.css("choices").bulleted(true));
            }
            
        }
        
        return DataType.TABLE;
        
    }

    public String toHTML () {
        if (type == DataType.TABLE)
            return tdata.toHTMLTable();
        else
            return "";
    }
    
    public String toDownload () {
        if (type == DataType.TABLE)
            return tdata.toCSV();
        else
            return "";
    }
    
    public void toDownload (Appendable out) throws IOException {
        if (type == DataType.TABLE)
            tdata.toCSV(out);
    }
    
    public String getDownloadContentType () {
        if (type == DataType.TABLE)
            return "text/csv";
        else
            return "text/plain";
    }
    
    public String getDownloadFilename () {
        return downloadFilename;
    }
    
}
