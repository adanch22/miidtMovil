package ciex.edu.mx.model;

/**
 * Created by azulyoro on 16/08/16.
 */
public class Question {
    private String question, answer;

    public Question() {
    }

    public Question(String question, String answer) {
        this.answer = answer;
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
