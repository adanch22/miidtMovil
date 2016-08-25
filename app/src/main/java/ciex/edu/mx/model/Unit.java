package ciex.edu.mx.model;

/**
 * Created by azulyoro on 11/04/16.
 */
import java.io.Serializable;

public class Unit implements Serializable {
    String grammar, title, vocabulary;

    public Unit() {
    }

    public Unit(String title, String grammar, String vocabulary) {
        this.title = title;
        this.grammar = grammar;
        this.vocabulary = vocabulary;
    }

    public String getGrammar() {
        return grammar;
    }

    public void setGrammar(String grammar) {
        this.grammar = grammar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }
}
