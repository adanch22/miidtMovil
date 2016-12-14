package ciex.edu.mx.model;

/**
 * Created by azulyoro on 11/04/16.
 */
import java.io.Serializable;

public class Unit implements Serializable {
    String description, title, author, name, type;

    public Unit() {
    }

    public Unit(String title, String description, String author, String name, String type) {
        this.title = title;
        this.name = name;
        this.description = description;
        this.author = author;
        this.type = type;
    }

    /*public String getGrammar() {
        return description;
    }

    public void setGrammar(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVocabulary() {
        return author;
    }

    public void setVocabulary(String author) {
        this.author = author;
    }*/

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
