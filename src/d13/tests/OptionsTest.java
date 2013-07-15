package d13.tests;

import d13.dao.RuntimeOptions;
import d13.util.HibernateUtil;

public class OptionsTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        
        HibernateUtil.beginTransaction();
        System.out.println("a = " + RuntimeOptions.getOption("a", "default"));
        System.out.println("b = " + RuntimeOptions.getOption("b", "default"));
        System.out.println("c = " + RuntimeOptions.getOption("c", "default"));
        HibernateUtil.commitTransaction();
        
        HibernateUtil.beginTransaction();
        RuntimeOptions.setOption("a", "first");
        RuntimeOptions.setOption("b", "first");
        HibernateUtil.commitTransaction();
        
        HibernateUtil.beginTransaction();
        System.out.println("a = " + RuntimeOptions.getOption("a", "default"));
        System.out.println("b = " + RuntimeOptions.getOption("b", "default"));
        System.out.println("c = " + RuntimeOptions.getOption("c", "default"));
        HibernateUtil.commitTransaction();
        
        HibernateUtil.beginTransaction();
        RuntimeOptions.setOption("b", "second");
        RuntimeOptions.setOption("c", "second");
        HibernateUtil.commitTransaction();

        HibernateUtil.beginTransaction();
        System.out.println("a = " + RuntimeOptions.getOption("a", "default"));
        System.out.println("b = " + RuntimeOptions.getOption("b", "default"));
        System.out.println("c = " + RuntimeOptions.getOption("c", "default"));
        HibernateUtil.commitTransaction();
       
        HibernateUtil.beginTransaction();
        RuntimeOptions.unsetOption("a");
        RuntimeOptions.unsetOption("d");
        HibernateUtil.commitTransaction();
        
        HibernateUtil.beginTransaction();
        System.out.println("a = " + RuntimeOptions.getOption("a", "default"));
        System.out.println("b = " + RuntimeOptions.getOption("b", "default"));
        System.out.println("c = " + RuntimeOptions.getOption("c", "default"));
        HibernateUtil.commitTransaction();

    }

}
