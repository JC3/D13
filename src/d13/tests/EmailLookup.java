package d13.tests;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import d13.dao.User;
import d13.util.HibernateUtil;

public class EmailLookup {

    static final String[] names = new String[] {
        "Alex Zaytsev             ",  
        "Alexander Savich     ",
        "Andrew Pistella      ",
        "Angela Bonura        ",
        "Audrey           ",
        "Billy Archer         ",
        "Chris Bryden         ",
        "Chris Jeffries       ",
        "Christian Trella     ",
        "Christine Devarona   ",
        "Colin Hu         ",
        "Corey Angelo         ",
        "Dan Gardner          ",
        "Dan Eicheldorf       ",
        "Deirdre Byrne        ",
        "Dmitry Yezersky      ",
        "Firas Fa         ",
        "Fay Wallace          ",
        "Freeman Murray       ",
        "George Mu        ",
        "Glen Duncan",
        "Guillaume Clave      ",
        "Hruby            ",
        "Ivan Fokin       ",
        "Jacob Perlman        ",
        "James Hagerman       ",
        "Jason Cipriani       ",
        "Jason McHugh         ",
        "Jeremy Reich         ",
        "Jernelle Antoine     ",
        "Jesse Sachs          ",
        "Jillian Lange        ",
        "Jim Hollingsworth    ",
        "John Dingee          ",
        "Justine Bach         ",
        "Katja Shevchuk       ",
        "Kellye Greene        ",
        "Kiril Shevyakov      ",
        "Leslie McLaughlin    ",
        "Martina Swart        ",
        "Matthew Jacobs       ",
        "Matt Pinner          ",
        "Michael Eogcumbe     ",
        "Michael Chaffin      ",
        "Mike Durgavich       ",
        "Natalie Nicol        ",
        "Patricia Wright      ",
        "Rendall Koski        ",
        "Rhonda Bennett       ",
        "Ria Rajan        ",
        "Richard Wolf Rubel   ",
        "Roman Arzhintar      ",
        "Russel Pace          ",
        "Sage Fly         ",
        "Sai Sabris       ",
        "Shaul Shtock         ",
        "Sofya Yuditskaya     ",
        "Stephanie Kiriakopoios   ",
        "Theresa Champagne    ",
        "Tonimarie Jeffries   ",
        "Tori Sigier          ",
        "Wendy Ingram         ",
        "Yoshan Churnac       ",
        "Yvonne Villareal     ",
        "Zach Robert              "
    };
    
    public static final void main (String[] args) throws Exception {
   
        HibernateUtil.beginTransaction();

        for (String name:names) {
            String email = null;
            try {
                User user = (User)HibernateUtil.getCurrentSession()
                        .createCriteria(User.class)
                        .add(Restrictions.ilike("realName", name.trim(), MatchMode.ANYWHERE))
                        .uniqueResult();
                if (user != null)
                    email = user.getEmail();
            } catch (Throwable t) {
                System.err.println(t.getMessage());
            }
            System.out.println(name + ", " + email);
        }
        
        HibernateUtil.commitTransaction();
        
    }
    
}
