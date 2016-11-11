package ciex.edu.mx.model;

/**
 * Created  on 11/04/16.
 */
import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Exercise implements Serializable {
    String type, information ,question, answer1, answer2, answer3, answerok;
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

    //adan chavez
    public String getQuestion() {
        return question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public String getAnswerok() {
        return answerok;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public void setAnswerok(String answerok) {
        this.answerok = answerok;
    }
}
