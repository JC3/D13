package d13.tests;

import java.util.List;

import d13.dao.Cell;
import d13.dao.User;
import d13.util.HibernateUtil;

public class CellTroubleshooting {

    public static void main(String[] args) throws Exception {

        HibernateUtil.beginTransaction();
       
        List<Cell> all = Cell.findAll();
        //Cell root = Cell.findRoot();
        
        for (Cell cell:all) {
            if (cell == null)
                System.out.println("    !!!! NULL !!!!");
            else {
                System.out.println(cell.getCellId() + " " + cell.getFullName() + " " + cell.getChildren().size());
                for (Cell child:cell.getChildren()) {
                    if (child == null)
                        System.out.println("    !!!! NULL !!!!");
                    else
                        System.out.println("    " + child.getCellId() + " " + child.getFullName() + " " + child.getChildren().size());
                }
            }
        }
        
        List<Cell> mand = Cell.findMandatory();
        
        for (Cell cell : mand) {
            System.out.println("MANDATORY: " + cell.getCellId() + " " + cell.getFullName() + " " + cell.getChildren().size());
        }
        
        for (User u : User.findAll()) {
            System.out.println("USER: " + u.getUserId() + " " + u.getEmail() + " any:" + u.isInCells() + " enough:" + u.isInEnoughCells() + " mand:" + u.isInMandatoryCells());
        }
        
        HibernateUtil.commitTransaction();
        
    }

}
