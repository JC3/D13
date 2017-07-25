package d13.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import d13.util.HibernateUtil;

public class FAQItem {
    
    public static final String ROOT_NAME = "$ROOT";
    
    private long    faqId;
    private boolean category;
    private FAQItem parent;
    private String  title;
    private String  detail;
    private List<FAQItem> children = new ArrayList<FAQItem>();

    FAQItem () {
    }
    
    private static FAQItem newRoot () {
        FAQItem item = new FAQItem();
        item.category = true;
        item.title = ROOT_NAME;
        return item;
    }
    
    private static FAQItem newCategory (FAQItem root, String name) {
        if (root == null || !root.isRoot())
            throw new IllegalArgumentException("Categories may only be added to the root node.");
        FAQItem item = new FAQItem();
        item.category = true;
        item.setTitle(name);
        item.parent = root;
        item.parent.children.add(item);
        return item;
    }
    
    private static FAQItem newQuestion (FAQItem category, String question, String answer) {
        if (category == null || !category.isCategory() || category.isRoot())
            throw new IllegalArgumentException("Questions may only be added to categories.");
        FAQItem item = new FAQItem();
        item.setTitle(question);
        item.setDetail(answer);
        item.parent = category;
        item.parent.children.add(item);
        item.title = question;
        item.detail = answer;
        return item;
    }
    
    public FAQItem addCategory (String name) {
        return newCategory(this, name);
    }
    
    public FAQItem addQuestion (String question, String answer) {
        return newQuestion(this, question, answer);
    }

    public void setTitle (String title) {
        if (title != null)
            title = title.trim();
        if (title == null || title.isEmpty() || ROOT_NAME.equalsIgnoreCase(title))
            throw new IllegalArgumentException(category ? "A valid category name must be specified." : "A question must be specified.");
        this.title = title;
    }

    public void setDetail (String detail) {
        if (detail != null)
            detail = detail.trim();
        if (!category && (detail == null || detail.isEmpty()))
            throw new IllegalArgumentException("An answer must be specified.");
        this.detail = detail;
    }

    public long getFaqId() {
        return faqId;
    }

    public boolean isCategory() {
        return category;
    }

    public boolean isRoot () {
        return parent == null;
    }
    
    public FAQItem getParent() {
        return parent;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public List<FAQItem> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    public static FAQItem findRoot () {
        
        FAQItem root = (FAQItem)HibernateUtil.getCurrentSession()
                .createCriteria(FAQItem.class)
                .add(Restrictions.isNull("parent"))
                .uniqueResult();
        
        if (root == null) {
            System.out.println("FAQ: Creating new root node!");
            root = newRoot();
            HibernateUtil.getCurrentSession().save(root);
        }
        
        return root;
        
    }
    
    public static FAQItem findById (long id) {
        
        FAQItem item = (FAQItem)HibernateUtil.getCurrentSession()
                .get(FAQItem.class, id);
        
        if (item == null)
            throw new IllegalArgumentException("There is no item with the specified ID.");
        
        return item;

    }
    
}
