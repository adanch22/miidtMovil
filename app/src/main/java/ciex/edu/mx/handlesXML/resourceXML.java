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
import ciex.edu.mx.model.Resource;

/**
 * Created by adanchavezolivera on 09/11/16.
 */

public class resourceXML {

    public ArrayList<Resource> resources;
    private ArrayList<Question> questions;
    private volatile boolean readcorrect = true;
    private String urlString = null, unit = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    public volatile boolean ImageDownloadComplete = false;
    private String type;

    public Bitmap imagenAux;

    public boolean isReadcorrect() {
        return readcorrect;
    }

    public resourceXML(String url, String unit){
        this.urlString = url;
        this.unit = unit;
        this.resources = new ArrayList<>();
        this.questions = new ArrayList<>();
    }

    public int getCountUnits(){
        return this.resources.size();
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;
        Resource exercise = new Resource();
        Question question = new Question();
        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("resource")){
                            exercise = new Resource();
                            //   questions = new ArrayList<>();
                            //   exercise.setType(myParser.getAttributeValue(null,"value"));
                            //   type = myParser.getAttributeValue(null,"value");
                        }/*else if(name.equals("question")){
                            exercise.setAnswerok(myParser.getAttributeValue(null,"value"));
                            question.setAnswer(myParser.getAttributeValue(null,"value"));

                        }*/
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("file")){
                           /* getBitmap(urlString+text);
                            while(!ImageDownloadComplete);
                            exercise.setImage(imagenAux);
                            imagenAux=null;
                            ImageDownloadComplete = false;*/
                            exercise.setUrl(urlString+text);
                        }
                        else if(name.equals("resourcetype")){
                            exercise.setType(text);

                        }else if(name.equals("title")){
                            exercise.setTitle(text);

                        }
                        else if(name.equals("resource")){

                            //exercise.setQuestions(questions);
                            resources.add(exercise);
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


    public ArrayList<Resource> getExercises() {
        return resources;
    }



}
