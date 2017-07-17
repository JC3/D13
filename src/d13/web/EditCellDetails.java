package d13.web;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;

import d13.dao.ActivityLogEntry;
import d13.dao.Cell;
import d13.dao.Role;
import d13.dao.User;
import d13.util.HibernateUtil;
import d13.util.Util;

public class EditCellDetails {

    private CellDetailsBean bean = new CellDetailsBean();
    private boolean failed;
    private String errorMessage;
    private String successMessage; // only set on delete right now since that's all that's interesting
    private String failTarget;
    private String successTarget;
    
    public EditCellDetails (PageContext context, SessionData session) {
        
        boolean is_new = false;
        Long cell_id = null;
        if ("new".equals(context.getRequest().getParameter("cell_id")))
            is_new = true;
        else 
            cell_id = Util.getParameterLong(context.getRequest(), "cell_id");
        boolean delete = "1".equals(context.getRequest().getParameter("delete_cell"));
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");

        try {
           
            BeanUtils.populate(bean, context.getRequest().getParameterMap());
            User editor;
            Role role;
            
            if (!session.isLoggedIn())
                throw new SecurityException("Permission denied.");

            editor = session.getUser();
            role = editor.getRole();
            if (!role.canEditCells())
                throw new SecurityException("Permission denied.");

            if (is_new) {
                if (!role.canCreateCells())
                    throw new SecurityException("Permission denied.");
            } else if (cell_id == null) {
                throw new IllegalArgumentException("No cell ID specified.");
            }
            
            if (delete) {
                if (is_new)
                    throw new IllegalArgumentException("Your request made no sense.");
                if (!role.canCreateCells())
                    throw new SecurityException("Permission denied.");
            }

            Cell cell = null;
            if (!is_new) {
                cell = Cell.findById(cell_id);
                if (cell.getParent() == null)
                    throw new SecurityException("Permission denied."); // can't edit root cell
            }
            
            if (delete && cell.isCategory() && cell.getNonCategoryChildCount() != 0)
                throw new IllegalArgumentException("You must remove all cells from this category before you can delete it.");
            
            if (delete) {
                doDeleteCell(cell, editor);
                return;
            }
            
            boolean hideFull = "1".equals(bean.getHideFull());
            boolean mandatory = "1".equals(bean.getMandatory());
            boolean hidden = "1".equals(bean.getHidden());
            String name = StringUtils.trimToEmpty(bean.getName());
            String desc = StringUtils.trimToEmpty(bean.getDesc());
            int people = -1;
            int parentId = Util.parseIntDefault(bean.getParent(), is_new ? (int)Cell.findRoot().getCellId() : (int)cell.getParent().getCellId());
            String newcatName = StringUtils.trimToEmpty(bean.getNewcatName());
            int newcatParentId = Util.parseIntDefault(bean.getNewcatParent(), 0);
            Cell parent = null, newcatParent = null;
            
            if (name.isEmpty())
                throw new IllegalArgumentException("Cell name must be specified.");
            
            try {
                String peopleStr = StringUtils.trimToEmpty(bean.getPeople());
                if (peopleStr.isEmpty())
                    peopleStr = "0";
                people = Integer.parseInt(peopleStr);
            } catch (Throwable t) {
                throw new IllegalArgumentException("Invalid volunteer count specified.");
            }
            
            if (people < 0)
                throw new IllegalArgumentException("Invalid volunteer count specified.");
            
            if (role.canCreateCells()) {
                if (parentId < 0) {
                    if (newcatName.isEmpty())
                        throw new IllegalArgumentException("New category name must be specified.");
                    if (newcatParentId <= 0)
                        throw new IllegalArgumentException("New category parent must be specified.");
                    newcatParent = Cell.findById((long)newcatParentId);
                } else {            
                    parent = Cell.findById((long)parentId);
                }
            }
            
            // --- all params parsed and validated except some parent details ---
            // if (parent) then change parent
            // else if (newcatParent) then create new cat under newcatParent, set as parent.
            // else do nothing (perhaps e.g. we don't have privs).
            
            int flags = 0;
            
            if (is_new) {
                
                // Do this first so if it fails we don't modify the rest. TODO: Re-evaluate *all* the dao
                // stuff and use transactions properly.
                Cell myparent = null;
                if (parent != null) {
                    myparent = parent;
                } else if (newcatParent != null) {
                    myparent = newcatParent.addCategory(newcatName, editor);
                } else {
                    throw new IllegalArgumentException("Parent must be specified.");
                }

                cell = myparent.addCell(name, editor);
                
            } else {
            
                if (!name.equals(cell.getName())) flags |= Cell.CHANGED_NAME;
                if (!desc.equals(StringUtils.trimToEmpty(cell.getDescription()))) flags |= Cell.CHANGED_DESCRIPTION;
                if (people != cell.getPeople()) flags |= Cell.CHANGED_PEOPLE;
                if (hideFull != cell.isHideWhenFull()) flags |= Cell.CHANGED_HIDEWHENFULL;
                if (mandatory != cell.isMandatory()) flags |= Cell.CHANGED_MANDATORY;
                if (hidden != cell.isHidden()) flags |= Cell.CHANGED_HIDDEN;
                boolean parentChanged = false;
                
                // Do this first so if it fails we don't modify the rest. TODO: Re-evaluate *all* the dao
                // stuff and use transactions properly.
                if (parent != null) {
                    parentChanged = cell.changeParent(parent);
                } else if (newcatParent != null) {
                    Cell newParent = newcatParent.addCategory(newcatName, editor);
                    parentChanged = cell.changeParent(newParent);
                }
                
                if (parentChanged) flags |= Cell.CHANGED_CATEGORY;
                
                cell.setName(name);
                
            }
            
            cell.setDescription(desc);
            cell.setPeople(people);
            cell.setHideWhenFull(hideFull);
            cell.setMandatory(mandatory);
            cell.setHidden(hidden);
            
            if (!is_new)
                cell.addEditedActivityLogEntry(session.getUser(), flags);

        } catch (InvocationTargetException x) {
            
            failed = true;
            errorMessage = x.getCause().getMessage();
            x.printStackTrace();
            
        } catch (Throwable t) {
            
            failed = true;
            errorMessage = t.getMessage();            
            t.printStackTrace();
            
        }
        
    }
    
    public void doDeleteCell (Cell cell, User editor) {
        
        // Add a log entry to the editor's log, since the cell log is about to be deleted.
        String message;
        if (cell.isCategory()) {
            editor.addBasicActivityLogEntry(String.format("Deleted category '%s'.", cell.getFullName()), ActivityLogEntry.TYPE_DELETE_CELL);
            message = String.format("Deleted category '%s'.", cell.getFullName());
        } else {
            editor.addBasicActivityLogEntry(String.format("Deleted cell '%s' with %d user(s).", cell.getFullName(), cell.getUsers().size()), ActivityLogEntry.TYPE_DELETE_CELL);
            message = String.format("Deleted cell '%s'.", cell.getFullName());
        }

        // First remove all users.
        List<Cell> alist = new ArrayList<Cell>();
        alist.add(cell);
        for (User user : new ArrayList<User>(cell.getUsers())) {
            Hibernate.initialize(user);
            user.removeFromCell(cell);
            user.addCellActivityLogEntry(editor, null, alist, false);
        }
        
        // Then remove the cell.
        cell.removeFromParent();
        HibernateUtil.getCurrentSession().delete(cell);
        successMessage = message;
        
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public String getSuccessMessage () {
        return successMessage;
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
    

    public static class Multiple {

        private final List<String> warnings = new ArrayList<String>();
        private final List<ResultState> results = new ArrayList<ResultState>();
        
        public static class ResultState {
            public long cell;
            public boolean autohide;
            public boolean mandatory;
            public boolean hidden;
            ResultState (long cell) { this.cell = cell; }
        }
        
        public Multiple (PageContext context, SessionData session) {
            
            if (!session.isLoggedIn() || !session.getUser().getRole().canEditCells())
                throw new SecurityException("Permission denied.");
            
            Boolean autohide = Util.parseBooleanDefault(context.getRequest().getParameter("autohide"), null);
            Boolean mandatory = Util.parseBooleanDefault(context.getRequest().getParameter("mandatory"), null);
            Boolean hidden = Util.parseBooleanDefault(context.getRequest().getParameter("hidden"), null);
            String[] cell_idstrs = context.getRequest().getParameterValues("cells[]");
         
            if (cell_idstrs == null || cell_idstrs.length == 0)
                throw new IllegalArgumentException("At least one cell must be specified.");
            
            for (String cell_idstr : cell_idstrs) {
                
                // get cell
                Cell cell;
                try {
                    cell = Cell.findById(Long.parseLong(cell_idstr));
                    if (cell.isCategory())
                        throw new Exception("Cannot apply changes to a category.");
                } catch (Exception x) {
                    warnings.add(cell_idstr + ": " + x.getMessage());
                    continue;
                }
             
                // for activity log
                int flags = 0;
                boolean oldAutohide = cell.isHideWhenFull();
                boolean oldMandatory = cell.isMandatory();
                boolean oldHidden = cell.isHidden();
              
                // apply changes
                if (autohide != null)
                    cell.setHideWhenFull(autohide);
                if (mandatory != null)
                    cell.setMandatory(mandatory);
                if (hidden != null)
                    cell.setHidden(hidden);
                
                // store results
                ResultState result = new ResultState(cell.getCellId());
                result.autohide = cell.isHideWhenFull();
                result.mandatory = cell.isMandatory();
                result.hidden = cell.isHidden();
                results.add(result);
                
                // for activity log
                if (result.autohide != oldAutohide) flags |= Cell.CHANGED_HIDEWHENFULL;
                if (result.mandatory != oldMandatory) flags |= Cell.CHANGED_MANDATORY;
                if (result.hidden != oldHidden) flags |= Cell.CHANGED_HIDDEN;                
                cell.addEditedActivityLogEntry(session.getUser(), flags);
                
            }
            
        }
        
        public List<String> getWarnings () { return warnings; }
        public List<ResultState> getResults () { return results; }
        
    }

}
