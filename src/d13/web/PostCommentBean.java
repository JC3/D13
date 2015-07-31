package d13.web;

public class PostCommentBean {
    
    private String next;
    private long subject;
    private String comment;
    
    public String getNext() {
        return next;
    }
    
    public long getSubject() {
        return subject;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setNext(String next) {
        this.next = next;
    }
    
    public void setSubject(long subject) {
        this.subject = subject;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
