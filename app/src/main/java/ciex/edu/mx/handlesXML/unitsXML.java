package ciex.edu.mx.handlesXML;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ciex.edu.mx.model.Unit;

/**
 * Created by azulyoro on 01/07/16.
 */
public class unitsXML {
    private ArrayList<Unit> units;
    private volatile boolean readcorrect = true;
    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;

    public boolean isReadcorrect() {
        return readcorrect;
    }

    public unitsXML(String url){
        this.urlString = url;
        this.units = new ArrayList<>();
    }

    public Unit getUnits(int position) {
        return units.get(position);
    }

    public int getCountUnits(){
        return this.units.size();
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;
        Unit unit = new Unit();
        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        if(name.equals("unit")){
                            unit = new Unit();
                            unit.setTitle(myParser.getAttributeValue(null,"value"));
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("grammar")){
                            unit.setGrammar(text);
                        }
                        else if(name.equals("vocabulary")){
                            unit.setVocabulary(text);
                        }else if(name.equals("unit")){
                            units.add(unit);
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
                    URL url = new URL(urlString+"content.xml");
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
}
