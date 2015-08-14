package d13.dao;

import org.joda.time.DateTime;

public class Comment {

    private long commentId;
    private User subject;
    private User author;
    private DateTime time;
    private String comment;
    
    Comment () {
    }
   
    public Comment (User subject, User author, String comment) {
        if (subject == null)
            throw new IllegalArgumentException("No subject user.");
        if (author == null)
            throw new IllegalArgumentException("No author.");
        if (comment == null || comment.trim().isEmpty())
            throw new IllegalArgumentException("Comment must be specified.");
        this.subject = subject;
        this.author = author;
        this.time = DateTime.now();
        this.comment = comment.trim();
    }

    public long getCommentId() {
        return commentId;
    }

    public User getSubject() {
        return subject;
    }

    public User getAuthor() {
        return author;
    }

    public DateTime getTime() {
        return time;
    }

    public String getComment() {
        return comment;
    }
    
    public void setTime (DateTime time) {
        this.time = time;
    }
    
}
