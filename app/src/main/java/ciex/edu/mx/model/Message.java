package ciex.edu.mx.model;

/**
 * Created by azulyoro on 11/04/16.
 */
import java.io.Serializable;

public class Message implements Serializable {
    String id, title, message, createdAt, image;
    User user;

    public Message() {
    }

    public Message(String id, String title, String message, String createdAt, User user, String image) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
