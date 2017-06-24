package d13.tests;

import d13.dao.Cell;
import d13.util.HibernateUtil;

public class MakeTestCells {

    public static void main(String[] args) throws Exception {

        HibernateUtil.beginTransaction();
        
        if (Cell.findRoot() == null) {
            System.out.println("ADDING CELLS");
            Cell root = Cell.newRoot();
            Cell cat1 = root.addCategory("Category A");
            cat1.addCell("Cell 1");
            cat1.addCell("Cell 2");
            cat1.addCell("Cell 3");
            cat1.addCell("Cell 4");
            cat1.addCell("Cell 5");
            root.addCategory("Category B");
            HibernateUtil.getCurrentSession().save(root);
        }
        
        HibernateUtil.commitTransaction();

    }

}
