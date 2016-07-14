package d13.web;

import java.util.HashSet;
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
            applyCellChanges(context.getRequest().getParameterMap(), editee, requireMandatory);

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
    
    private void applyCellChanges (Map<String,String[]> params, User user, boolean requireMandatory) {
        
        Set<Long> remove = stringsToLongSet(params.get("xc"));
        Set<Long> add = stringsToLongSet(params.get("c")); 
        remove.removeAll(add);
        
        for (Long r:remove) {
            try {
                user.removeFromCell(Cell.findById(r));
            } catch (Throwable t) {
                System.err.println(t.getMessage());
            }
        }
        
        for (Long r:add) {
            try {
                user.addToCell(Cell.findById(r));
            } catch (Throwable t) {
                System.err.println(t.getMessage());
            }
        }
        
        // note that even in this case; cells are still modified. this doesn't really have any negative
        // side effects and is a kludgy way to make sure the user's cell selections aren't lost if they
        // are kicked back to the cell editor page.
        if (requireMandatory && !user.isInMandatoryCells()) {
            failed = true;
            errorMessage = "Some of the cells below are mandatory. You must sign up for them!";
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
