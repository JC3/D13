package d13.web;

import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.BeanUtils;

import d13.dao.User;

public class PostComment {

    private boolean failed = false;
    private String errorMessage = null;
    private String target = null;
    
    public PostComment (PageContext context, SessionData session) {
        
        try {
        
            if (!session.isLoggedIn())
                throw new IllegalArgumentException("Permission denied.");
            
            PostCommentBean bean = new PostCommentBean();
            BeanUtils.populate(bean, context.getRequest().getParameterMap());

            target = bean.getNext();
                        
            User subject = User.findById(bean.getSubject());
            if (subject == null)
                throw new IllegalArgumentException("Invalid request.");

            User author = session.getUser();
            if (!subject.isCommentableBy(author))
                throw new IllegalArgumentException("Permission denied.");

            subject.addComment(author, bean.getComment());
            
        } catch (Throwable t) {
            
            failed = true;
            errorMessage = t.getMessage();
            
        }
            

    }
    
    public boolean isFailed () {
        
        return failed;
        
    }
    
    public String getErrorMessage () {
       
        return errorMessage;
        
    }
    
    public String getTarget () {
        
        return target;
        
    }

}
