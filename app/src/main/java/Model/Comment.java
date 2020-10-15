package Model;

public class Comment {
    private String id;
    private String publisher;
    private String comment;

    public Comment() {
    }

    public Comment(String id,String publisher, String comment) {
        this.id=id;
        this.publisher = publisher;
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
