package ciex.edu.mx.model;

/**
 * Created by azulyoro on 11/04/16.
 */
import java.io.Serializable;

public class Book implements Serializable {
    String title, level, index, urlPortada, imageName;

    public Book() {
    }

    public Book(String title, String urlPortada, String imageName) {
        this.title = title;
        this.urlPortada = urlPortada;
        this.imageName = imageName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlPortada() {
        return urlPortada;
    }

    public void setUrlPortada(String urlPortada) {
        this.urlPortada = urlPortada;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
