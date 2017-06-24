package d13.web;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import d13.dao.Cell;
import d13.dao.Role;
import d13.util.Util;

public class EditCellDetails {

    private CellDetailsBean bean = new CellDetailsBean();
    private boolean failed;
    private String errorMessage;
    private String failTarget;
    private String successTarget;

    public EditCellDetails (PageContext context, SessionData session) {
        
        Long cell_id = Util.getParameterLong(context.getRequest(), "cell_id");    
        failTarget = context.getRequest().getParameter("fail_target");
        successTarget = context.getRequest().getParameter("success_target");

        try {
           
            BeanUtils.populate(bean, context.getRequest().getParameterMap());
            Role role;
            
            if (!session.isLoggedIn())
                throw new SecurityException("Permission denied.");
            if (cell_id == null)
                throw new IllegalArgumentException("No cell ID specified.");
            if (!(role = session.getUser().getRole()).canEditCells())
                throw new SecurityException("Permission denied.");

            Cell cell = Cell.findById(cell_id);
            
            boolean hideFull = "1".equals(bean.getHideFull());
            boolean mandatory = "1".equals(bean.getMandatory());
            boolean hidden = "1".equals(bean.getHidden());
            String name = StringUtils.trimToEmpty(bean.getName());
            String desc = StringUtils.trimToEmpty(bean.getDesc());
            int people = -1;
            int parentId = Util.parseIntDefault(bean.getParent(), (int)cell.getParent().getCellId());
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
                    newcatParent = Cell.findById((long)parentId);
                } else {            
                    parent = Cell.findById((long)parentId);
                }
            }
            
            // --- all params parsed and validated ---
            // if (parent) then change parent
            // else if (newcatParent) then create new cat under newcatParent, set as parent.
            // else do nothing (perhaps e.g. we don't have privs).
            
            int flags = 0;
            if (!name.equals(cell.getName())) flags |= Cell.CHANGED_NAME;
            if (!desc.equals(StringUtils.trimToEmpty(cell.getDescription()))) flags |= Cell.CHANGED_DESCRIPTION;
            if (people != cell.getPeople()) flags |= Cell.CHANGED_PEOPLE;
            if (hideFull != cell.isHideWhenFull()) flags |= Cell.CHANGED_HIDEWHENFULL;
            if (mandatory != cell.isMandatory()) flags |= Cell.CHANGED_MANDATORY;
            if (hidden != cell.isHidden()) flags |= Cell.CHANGED_HIDDEN;
            
            cell.setName(name);
            cell.setDescription(desc);
            cell.setPeople(people);
            cell.setHideWhenFull(hideFull);
            cell.setMandatory(mandatory);
            cell.setHidden(hidden);
            cell.addEditedActivityLogEntry(session.getUser(), flags);

            if (parent != null) {
                cell.changeParent(parent);
            } else if (newcatParent != null) {
                
            }
            
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
