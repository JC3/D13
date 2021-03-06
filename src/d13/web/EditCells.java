package d13.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.PageContext;

import d13.dao.Cell;
import d13.dao.User;
import d13.util.Util;

public class EditCells {

    private boolean failed;
    private String errorMessage;
    private String failTarget;
    private String successTarget;

    public EditCells (PageContext context, SessionData session) {
        
        Long user_id = Util.getParameterLong(context.getRequest(), "user_id");    
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");

        try {
           
            if (!session.isLoggedIn())
                throw new SecurityException("Permission denied.");
            if (user_id == null)
                throw new IllegalArgumentException("No user ID specified.");
            
            User editor = session.getUser();
            User editee = User.findById(user_id);
            if (!editee.isEditableBy2(editor))
                throw new SecurityException("Permission denied.");
            
            // simple logic for now: if user is editing their own cells assume they are in reg flow and
            // do not let them proceed if mandatory cells are missing. 
            boolean requireMandatory = (editor.getUserId() == editee.getUserId());
        
            // applyCellChanges will set failed/errorMessage if mandatory requirements aren't met
            applyCellChanges(context.getRequest().getParameterMap(), editor, editee, requireMandatory);

        } catch (Throwable t) {
            
            failed = true;
            errorMessage = t.getMessage();            
            t.printStackTrace();
            
        }
        
    }
    
    private Set<Long> stringsToLongSet (String[] strs) {
        
        Set<Long> s = new HashSet<Long>();
        
        if (strs != null) {
            for (String str:strs) {
                if (str == null)
                    continue;
                try {
                    long id = Long.parseLong(str);
                    s.add(id);
                } catch (Throwable t) {
                    continue;
                }
            }
        }
       
        return s;
        
    }
    
    // note: requireMandatory now also controls requiring minimum non-mandatory cells. too lazy to rename.
    private void applyCellChanges (Map<String,String[]> params, User editor, User user, boolean requireMandatory) {
        
        Set<Long> remove = stringsToLongSet(params.get("xc"));
        Set<Long> add = stringsToLongSet(params.get("c")); 
        remove.removeAll(add);
        
        List<Cell> removed = new ArrayList<Cell>();
        List<Cell> added = new ArrayList<Cell>();
        
        for (Long r:remove) {
            try {
                Cell cell = Cell.findById(r);
                if (user.removeFromCell(cell))
                    removed.add(cell);
            } catch (Throwable t) {
                System.err.println(t.getMessage());
            }
        }
        
        for (Long r:add) {
            try {
                Cell cell = Cell.findById(r);
                if (user.addToCell(cell))
                    added.add(cell);
            } catch (Throwable t) {
                System.err.println(t.getMessage());
            }
        }
        
        user.addCellActivityLogEntry(editor, added, removed, true);
        
        // note that even in this case; cells are still modified. this doesn't really have any negative
        // side effects and is a kludgy way to make sure the user's cell selections aren't lost if they
        // are kicked back to the cell editor page.
        if (requireMandatory) {
            if (!user.isInMandatoryCells()) {
                failed = true;
                errorMessage = "Hey there! Some of the cells below are mandatory. You must sign up for them!";
            } else if (!user.isInEnoughCells()) {
                failed = true;
                errorMessage = "Hey there! You must sign up for at least " + User.LOW_CELL_THRESHOLD + " non-mandatory cells. " +
                               "Don't worry, you can come back and change later if something piques your interest post registration!";
            }
        }
        
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public String getFailTarget () {
        return failTarget;
    }
    
    public String getSuccessTarget () {
        return successTarget;
    }
    
    public boolean isFailed () {
        return failed;
    }
    
}
