package ciex.edu.mx.handlesXML;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ciex.edu.mx.model.Exercise;
import ciex.edu.mx.model.Question;

/**
 * Created by azulyoro on 01/07/16.
 */
public class exerciseXML {
    public ArrayList<Exercise> exercises;
    private ArrayList<Question> questions;
    private volatile boolean readcorrect = true;
    private String urlString = null, unit = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    public volatile boolean ImageDownloadComplete = false;

    public Bitmap imagenAux;

    public boolean isReadcorrect() {
        return readcorrect;
    }

    public exerciseXML(String url, String unit){
        this.urlString = url;
        this.unit = unit;
        this.exercises = new ArrayList<>();
        this.questions = new ArrayList<>();
    }



    public int getCountUnits(){
        return this.exercises.size();
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;
        Exercise exercise = new Exercise();
        Question question = new Question();
        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("exercise")){
                            exercise = new Exercise();
                            questions = new ArrayList<>();
                            exercise.setType(myParser.getAttributeValue(null,"value"));
                        }else if(name.equals("question")){
                            question.setAnswer(myParser.getAttributeValue(null,"value"));
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("image")){
                            getBitmap(urlString+text);
                            while(!ImageDownloadComplete);
                            exercise.setImage(imagenAux);
                            imagenAux=null;
                            ImageDownloadComplete = false;
                        }
                        else if(name.equals("information")){
                            exercise.setInformation(text);
                        }else if(name.equals("question")){
                            question.setQuestion(text);
                            questions.add(question);
                            question = new Question();
                        }else if(name.equals("exercise")){
                            exercise.setQuestions(questions);
                            exercises.add(exercise);
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString+unit+".xml");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    readcorrect = false;
                    parsingComplete = false;
                }
            }
        });
        thread.start();
    }

    public void getBitmap(final String url){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL newurl = new URL( url );
                    imagenAux = BitmapFactory.decodeStream( newurl.openConnection( ).getInputStream( ) );
                    ImageDownloadComplete = true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }
}
