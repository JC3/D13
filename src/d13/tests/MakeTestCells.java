package d13.tests;

import d13.dao.Cell;
import d13.util.HibernateUtil;

public class MakeTestCells {

    public static void main(String[] args) throws Exception {

        HibernateUtil.beginTransaction();
        
        if (Cell.findRoot() == null) {
            System.out.println("ADDING CELLS");
            Cell root = Cell.newRoot();
            Cell cat1 = root.addCategory("Category A", null);
            cat1.addCell("Cell 1", null);
            cat1.addCell("Cell 2", null);
            cat1.addCell("Cell 3", null);
            cat1.addCell("Cell 4", null);
            cat1.addCell("Cell 5", null);
            Cell cat2 = root.addCategory("Category B", null);
            cat2.addCell("Cell 6", null);
            cat2.addCell("Cell 7", null);
            Cell cat3 = cat2.addCategory("B1", null);
            cat3.addCell("Cell 8", null);
            HibernateUtil.getCurrentSession().save(root);
        }
        
        HibernateUtil.commitTransaction();

    }

}
