package Model;

public class User {
    private String email;
    private String id;
    private String username;
    private String name;
    private String imageurl;
    private String bio;
    private String interest;
    private String status;

    public User() {
    }

    public User(String email, String id, String username, String name, String imageurl, String bio, String interest,String status) {
        this.email = email;
        this.id = id;
        this.username = username;
        this.name = name;
        this.imageurl = imageurl;
        this.bio = bio;
        this.interest = interest;
        this.status=status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
