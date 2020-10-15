package Model;

import java.util.Comparator;

public class Post {

    private String imageUrl;
    private String postId;
    private String publisher;
    private String title;
    private String article;
    private String like;

    public Post() {
    }

    //sort by popularity in home fragment
    public static Comparator<Post> SortbyLikes =new Comparator<Post>() {
        @Override
        public int compare( Post o1, Post o2) {
            return o1.getLike().compareTo(o2.getLike());
        }
    };
    public static Comparator<Post> SortbyTitle =new Comparator<Post>() {
        @Override
        public int compare( Post o1, Post o2) {
            return o2.getTitle().toLowerCase().compareTo(o1.getTitle().toLowerCase());
        }
    };

    public Post(String imageUrl, String postId, String publisher, String title, String article,String like) {
        this.imageUrl = imageUrl;
        this.postId = postId;
        this.publisher = publisher;
        this.title = title;
        this.article = article;
        this.like=like;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) { this.like = like; }
}
