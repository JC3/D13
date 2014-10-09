package d13.tests;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import d13.dao.Cell;
import d13.dao.User;
import d13.util.HibernateUtil;

public class CellSortTest {

    static void printHeader (PrintStream out, List<Cell> cells) {

        for (int n = 0, div = 100; n < 3; ++ n, div /= 10) {
            out.print(n == 2 ? "USER  " : "      ");
            for (Cell cell:cells) {
                out.print((cell.getCellId() / div) % 10);
                out.print(" ");
            }
            out.println();
        }
        
    }
    
    static void printUser (PrintStream out, List<Cell> cells, User user) {
        if (user.getCells().isEmpty())
            return;
        out.print(String.format("%04d  ", user.getUserId()));
        for (Cell cell:cells) {
            if (user.isInCell(cell))
                out.print("X ");
            else
                out.print("- ");
        }
        out.println();
        
    }

    static void printUserCode (PrintStream out, List<Cell> cells, User user) {
        if (user.getCells().isEmpty())
            return;
        out.print(String.format("users.add(new User(%3d", user.getUserId()));
        for (Cell cell:cells) {
            if (!cell.getUsers().isEmpty()) {
                if (user.isInCell(cell))
                    out.print(",1");
                else
                    out.print(",0");
            }
        }
        out.println("));");
        
    }

    public static void main (String[] args) throws Exception {

        HibernateUtil.beginTransaction();

        List<User> users = User.findAll();
        List<Cell> cells = Cell.findAll();
        
        printHeader(System.out, cells);
        
        Map<Integer,Integer> cellToIndex = new HashMap<Integer,Integer>();
        for (int n = 0; n < cells.size(); ++ n)
            cellToIndex.put((int)cells.get(n).getCellId(), n);
        
        List<User> sorted = new ArrayList<User>(users);
        Collections.sort(sorted, new SizeWeightedCellSort(cellToIndex));
        
        for (User user:users)
            printUserCode(System.out, cells, user);
        
        HibernateUtil.commitTransaction();
        
    }

    static abstract class CellSort implements Comparator<User> {
        abstract double metric (User u);
        final Map<Integer,Integer> cellids;
        CellSort (Map<Integer,Integer> cellids) {
            this.cellids = cellids;
        }
        @Override public int compare (User a, User b) {
            return Double.compare(metric(a), metric(b));
        }
    }
    
    static class SizeWeightedCellSort extends CellSort {
        SizeWeightedCellSort (Map<Integer,Integer> cellids) {
            super(cellids);
        }
        @Override double metric (User u) {
            double sum = 0;
            int count = 0;
            for (Cell c : u.getCells()) {
                count += c.getUsers().size();
                sum += cellids.get((int)c.getCellId()) * c.getUsers().size();                
            }
            return (count != 0) ? (sum / count) : 0;
        }
    }
    
    static class MeanCellSort extends CellSort {
        MeanCellSort (Map<Integer,Integer> cellids) {
            super(cellids);
        }
        @Override double metric (User u) {
            double sum = 0;
            int count = 0;
            for (Cell c : u.getCells()) {
                ++ count;
                sum += cellids.get((int)c.getCellId());                
            }
            return (count != 0) ? (sum / count) : 0;
        }
    }
    
}
