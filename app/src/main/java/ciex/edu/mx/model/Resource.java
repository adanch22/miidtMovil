package ciex.edu.mx.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by adanchavezolivera on 09/11/16.
 */

public class Resource implements Serializable {
    String type, archivo ,title, url;
    Bitmap image;

    public Resource() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public String getArchivo() {
        return archivo;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
