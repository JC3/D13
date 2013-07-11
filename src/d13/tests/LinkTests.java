package d13.tests;

import d13.dao.Cell;
import d13.dao.User;
import d13.util.HibernateUtil;

public class LinkTests {

    public static final void main (String[] args) throws Throwable {
        
        HibernateUtil.beginTransaction();
        
        User user = User.findByEmail("jason@disorient.info");
        user.addToCell(Cell.findById(218L));
        user.addToCell(Cell.findById(217L));

        user = User.findByEmail("jason.cipriani@gmail.com");
        user.addToCell(Cell.findById(216L));
        user.addToCell(Cell.findById(217L));
        
        HibernateUtil.commitTransaction();
        
    }
    
}
