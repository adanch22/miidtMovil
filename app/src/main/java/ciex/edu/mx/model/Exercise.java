package ciex.edu.mx.model;

/**
 * Created by azulyoro on 11/04/16.
 */
import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Exercise implements Serializable {
    String type, information;
    Bitmap image;
    ArrayList<Question> questions;

    public Exercise() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
