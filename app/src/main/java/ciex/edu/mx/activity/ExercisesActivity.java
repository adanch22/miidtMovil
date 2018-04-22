package ciex.edu.mx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ciex.edu.mx.R;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.app.StringComparation;
import ciex.edu.mx.connection.ConnectivityReceiver;
import ciex.edu.mx.dialog.SimpleDialog;
import ciex.edu.mx.handlesXML.exerciseXML;
import ciex.edu.mx.model.Exercise;
import ciex.edu.mx.model.Question;

public class ExercisesActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = ExercisesActivity.class.getSimpleName();
    private String title, level, book, unit, exerciseType;
    private Menu menu;
    private ArrayList<Exercise> exercises;
    private ImageView iv1;
    private TextView tv1,nov1, tv2;
    private RadioButton rb1,rb2,rb3, rbm1, rbm2, rbm3;
    private int exercisePosition = 0, exercise, rbcheked=1;
    private ViewFlipper vf;
    private Boolean finish;
    private int error, ok;

    private CoordinatorLayout coordinatorLayout;
    private  Snackbar snackbar;
    //variables de la progressbar
    private int pbne=0,  progressStatus = 0;
    private Handler handler = new Handler();
    ProgressBar myProgressBar;

    private RelativeLayout relativeLmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        /**
         * Check for login session. If not logged in launch
         * login activity
         * */
        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            launchLoginActivity();
        }

        title = getIntent().getStringExtra("title");
        level = getIntent().getStringExtra("level");
        book = getIntent().getStringExtra("book");
        unit = getIntent().getStringExtra("unit");
        unit = cleanString(unit);
        exercises=loadXml();


        finish = false;
        error=0;

        setContentView(R.layout.activity_exercises);
        vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //image = (ImageView) findViewById(R.id.imagePresentation);
        //information = (TextView) findViewById(R.id.infoPresentation);
        ok =0;
        //
        showExercise();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabexercise);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finish==true){

                    Intent intent = new Intent(ExercisesActivity.this, UnitActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("level",level);
                    intent.putExtra("book",book);
                    startActivity(intent);


                } else {
                    boolean correct=false;

                    switch (exerciseType){
                        case "presentation":
                            correct=presentation();
                            break;
                        case "questionary" :
                            correct=questionary();
                            break;

                        case "multipleoptions":
                            correct=multipleoptions();
                            break;

                        case "presentationm":
                            correct = presentationmultipleoptions();
                            break;
                    }
                    if(!correct){
                        switch (exerciseType){
                            case "presentation":
                                error= error+1;
                                if (error<3)
                                    Snackbar.make(view, "Respuesta incorrecta, Intentalo de nuevo",
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                else
                                    Snackbar.make(view, "La respuesta coorrecta es: "+ exercises.get(exercisePosition).getAnswerok(),
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                break;
                            case "questionary" :
                                error= error+1;
                                if (error<3)
                                    Snackbar.make(view, "Respuesta incorrecta, Intentalo de nuevo",
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                else
                                    Snackbar.make(view, "La respuesta coorrecta es "+ exercises.get(exercisePosition).getAnswerok(),
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                break;

                            case "multipleoptions":
                                error= error+1;
                                if (error<3)
                                    Snackbar.make(view, "Respuesta incorrecta, Intentalo de nuevo",
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                else
                                    Snackbar.make(view, "La respuesta coorrecta es la opción: "+ exercises.get(exercisePosition).getAnswerok(),
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                break;

                            case "presentationm":
                                error= error+1;
                                if (error<3)
                                    Snackbar.make(view, "Respuesta incorrecta, Intentalo de nuevo",
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                else
                                    Snackbar.make(view, "La respuesta coorrecta es la opción: "+ exercises.get(exercisePosition).getAnswerok(),
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                break;

                        }
                    }else{

                        if (ok==1) {
                            cleanText();
                            error=0;
                            exercisePosition++;
                            showExercise();
                            ok =0;

                        }else{
                            Snackbar.make(view, "Respuesta correcta, pulsa el boton azul para continuar", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            ok = ok +1;
                        }
                    }
                }



            }

        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
    }

    @Override
    public void onBackPressed()
    {
       new SimpleDialog(title, level,book).show(getSupportFragmentManager(), "SimpleDialog");
        // super.onBackPressed();  // Invoca al método
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {

            message = "Sorry! Not connected to internet";
            color = Color.RED;

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.fabexercise), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }else{

            /**
             * Always check for google play services availability before
             * proceeding further with GCM
             * */
/*
            if (checkPlayServices()) {
                registerGCM();
            }
*/
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //for(Exercise exercise: exercises){
        //    image.setImageBitmap(exercise.getImage());
        //    information.setText(exercise.getInformation().replace("\\n",System.getProperty("line.separator")));
        //}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resource, menu);
        this.menu = menu;

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.resource_m:
                Intent intent = new Intent(ExercisesActivity.this, ResourcesActivity.class);
                intent.putExtra("title",title);
                intent.putExtra("level",level);
                intent.putExtra("book",book);
                intent.putExtra("unit",unit);
                startActivity(intent);
                break;


        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void launchLoginActivity() {
        Intent intent = new Intent(ExercisesActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public ArrayList<Exercise> loadXml(){
        exerciseXML obj;
        String url = EndPoints.UNITS_CONTENT_URL.replace("level?","level"+level)
                .replace("book?",title.replace(" ", ""));
        obj = new exerciseXML(url, unit);
        obj.fetchXML();
        while(obj.parsingComplete);
        if (obj.isReadcorrect()) {
            return obj.getExercises();
        }else{
            Log.e(TAG, "Conection Error");
            return null;
        }
    }

    public void showExercise(){
        if(exercisePosition<exercises.size()) {
            switch (exercises.get(exercisePosition).getType()) {
                case "presentation":
                    exerciseType = "presentation";
                    vf.setDisplayedChild(0);
                    nov1 = (TextView) findViewById(R.id.nopresentation);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );

                    iv1 = (ImageView) findViewById(R.id.imagePresentation);
                    iv1.setImageBitmap(exercises.get(exercisePosition).getImage());

                    int i = 0;
                    for (Question qt : exercises.get(exercisePosition).getQuestions()) {
                        i++;

                        int resID = getResources().getIdentifier("tv"+i, "id", getPackageName());
                        TextView tvAux=(TextView) findViewById(resID);
                        tvAux.setText(qt.getQuestion());
                        tvAux.setVisibility(View.VISIBLE);

                        resID = getResources().getIdentifier("et"+i, "id", getPackageName());
                        EditText etAux=(EditText) findViewById(resID);
                        etAux.setVisibility(View.VISIBLE);
                        Drawable drawable = etAux.getBackground();
                        editTextSetColor(Color.BLACK, drawable, etAux);
                    }
                    final ScrollView myscrollp=((ScrollView) findViewById(R.id.scrollpresentation));
                    myscrollp.post(new Runnable() {
                        @Override
                        public void run() {
                            myscrollp.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });



                    snackbar = Snackbar
                            .make(findViewById(R.id.fabexercise), "Ingresa un valor y evalúa tu respuesta con el botón azul", Snackbar.LENGTH_INDEFINITE)
                            .setAction("ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });

                    snackbar.show();
                    break;

                case "questionary":
                    exerciseType = "questionary";
                    //vf.setDisplayedChild(R.id.questionaryView);
                    //vf.showNext();
                    vf.setDisplayedChild(1);
                    //iv1 = (ImageView) findViewById(R.id.imagePresentation);
                    nov1 = (TextView) findViewById(R.id.noquestionary);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );
                    tv1 = (TextView) findViewById(R.id.infoquestionary);
                    // iv1.setImageBitmap(exercises.get(exercisePosition).getImage());
                    tv1.setText(exercises.get(exercisePosition).getInformation().replace("\\n", System.getProperty("line.separator")));

                    int x = 0;
                    for (Question qt : exercises.get(exercisePosition).getQuestions()) {
                        x++;

                        int resID = getResources().getIdentifier("qtv" + x, "id", getPackageName());
                        TextView tvAux=(TextView) findViewById(resID);
                        tvAux.setText(qt.getQuestion());
                        tvAux.setVisibility(View.VISIBLE);

                        resID = getResources().getIdentifier("qet"+ x, "id", getPackageName());
                        EditText etAux=(EditText) findViewById(resID);
                        etAux.setVisibility(View.VISIBLE);
                        Drawable drawable = etAux.getBackground();
                        editTextSetColor(Color.BLACK, drawable, etAux);
                    }
                    snackbar = Snackbar
                            .make(findViewById(R.id.fabexercise), "Ingresa un valor y evalúa tu respuesta con el botón azul", Snackbar.LENGTH_INDEFINITE)
                            .setAction("ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });

                    snackbar.show();

                    break;

                case "multipleoptions":
                    exerciseType = "multipleoptions";
                    //vf.setDisplayedChild(R.id.questionaryView);
                    //vf.showNext();
                    vf.setDisplayedChild(2);
                    //iv1 = (ImageView) findViewById(R.id.imagePresentation);
                    nov1 = (TextView) findViewById(R.id.nomultiple);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );
                    tv1 = (TextView) findViewById(R.id.infomultipleoptions);
                    // iv1.setImageBitmap(exercises.get(exercisePosition).getImage());
                    tv1.setText(exercises.get(exercisePosition).getInformation().replace("\\n", System.getProperty("line.separator")));
                    tv2 = (TextView) findViewById(R.id.mtv1);
                    tv2.setText(exercises.get(exercisePosition).getQuestion());

                    rb1 =(RadioButton) findViewById(R.id.ranswer1);
                    rb1.setText(exercises.get(exercisePosition).getAnswer1());
                    rb1.setChecked(true);
                    rbcheked= 1;

                    rb2 =(RadioButton) findViewById(R.id.ranswer2);
                    rb2.setText(exercises.get(exercisePosition).getAnswer2());

                    rb3 =(RadioButton) findViewById(R.id.ranswer3);
                    rb3.setText(exercises.get(exercisePosition).getAnswer3());
                    rb1.setTextColor(getResources().getColor(R.color.black));
                    rb2.setTextColor(getResources().getColor(R.color.black));
                    rb3.setTextColor(getResources().getColor(R.color.black));

                    snackbar = Snackbar
                            .make(findViewById(R.id.fabexercise), "Elije una opción  y evalúa tu respuesta con el botón azul", Snackbar.LENGTH_INDEFINITE)
                            .setAction("ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });

                    snackbar.show();
                    break;


                case "presentationm":
                    exerciseType = "presentationm";
                    vf.setDisplayedChild(3);
                    nov1 = (TextView) findViewById(R.id.nopresentationm);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );

                    iv1 = (ImageView) findViewById(R.id.imagePresentationmultiple);
                    iv1.setImageBitmap(exercises.get(exercisePosition).getImage());


                    tv2 = (TextView) findViewById(R.id.pmtv1);
                    tv2.setText(exercises.get(exercisePosition).getQuestion());
                    tv2.setFocusable(true);

                    rbm1 =(RadioButton) findViewById(R.id.pranswer1);
                    rbm1.setText(exercises.get(exercisePosition).getAnswer1());
                    rbm1.setChecked(true);
                    rbcheked= 1;

                    rbm2 =(RadioButton) findViewById(R.id.pranswer2);
                    rbm2.setText(exercises.get(exercisePosition).getAnswer2());

                    rbm3 =(RadioButton) findViewById(R.id.pranswer3);
                    rbm3.setText(exercises.get(exercisePosition).getAnswer3());
                    rbm1.setTextColor(getResources().getColor(R.color.black));
                    rbm2.setTextColor(getResources().getColor(R.color.black));
                    rbm3.setTextColor(getResources().getColor(R.color.black));
                   final ScrollView myscrollview=((ScrollView) findViewById(R.id.scrollpmultiple));
                    myscrollview.post(new Runnable() {
                        @Override
                        public void run() {
                            myscrollview.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                    snackbar = Snackbar
                            .make(findViewById(R.id.fabexercise), "Elije una opción  y evalua tu respuesta con el botón azul", Snackbar.LENGTH_INDEFINITE)
                            .setAction("ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });

                    snackbar.show();
                    break;



            }
        }else{

            finish = true;
            vf.setDisplayedChild(5);

        }
    }
    public void cleanText(){
        for(int i = 1; i<=exercises.get(exercisePosition).getQuestions().size(); i++){

            if(exerciseType=="presentation"){
                int resID = getResources().getIdentifier("tv"+i, "id", getPackageName());
                TextView tvAux=(TextView) findViewById(resID);
                tvAux.setText("");
                tvAux.setVisibility(View.GONE);

                resID = getResources().getIdentifier("et"+i, "id", getPackageName());
                EditText etAux=(EditText) findViewById(resID);
                etAux.setText("");
                etAux.setVisibility(View.GONE);
                Drawable drawable = etAux.getBackground();
                editTextSetColor(Color.BLACK, drawable, etAux);

            }
            else if (exerciseType=="questionary"){
                int resID = getResources().getIdentifier("qtv"+i, "id", getPackageName());
                TextView tvAux=(TextView) findViewById(resID);
                tvAux.setText("");
                tvAux.setVisibility(View.GONE);

                resID = getResources().getIdentifier("qet"+i, "id", getPackageName());
                EditText etAux=(EditText) findViewById(resID);
                etAux.setText("");
                etAux.setVisibility(View.GONE);
                Drawable drawable = etAux.getBackground();
                editTextSetColor(Color.BLACK, drawable, etAux);
            }

        }
    }

    private void editTextSetColor(int color, Drawable drawable, TextView text){
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        if(Build.VERSION.SDK_INT > 16) {
            text.setBackground(drawable); // set the new drawable to EditText
        }else{
            text.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }
    }

    private int tokens(String answer, char token){
        int count=0;
        for(int ch=0; ch<answer.length(); ch++){
            if(answer.charAt(ch)==token){
                count++;
            }
        }
        return count;
    }

    private boolean presentation(){
        boolean correct=true;
        String answerRecibed,qtAnswer;
        int i=0;
        int resID;
        correct=true;
        StringComparation lv = new StringComparation();

        for(Question qt:exercises.get(exercisePosition).getQuestions()){
            i++;
            resID = getResources().getIdentifier("et"+i, "id", getPackageName());
            EditText text=(EditText) findViewById(resID);

            Drawable drawable = text.getBackground(); // get current EditText drawable

            answerRecibed = text.getText().toString();
            if(answerRecibed.length()>0){
                qtAnswer = qt.getAnswer().replace("[","").replace("]","").replace("{","").replace("}","");
                if(qtAnswer.equalsIgnoreCase(answerRecibed)){
                    editTextSetColor(getResources().getColor(R.color.green_backgroud),drawable,text);
                }else {
                    int impAnswers=tokens(qt.getAnswer(),']');
                    if(impAnswers>0){
                        qtAnswer=qt.getAnswer().substring(qt.getAnswer().indexOf('[')+1, qt.getAnswer().indexOf(']'));
                    }else{
                        qtAnswer="";
                    }
                    if(answerRecibed.contains(qtAnswer) && !qtAnswer.equals("")){//this is the part exactly
                        answerRecibed=answerRecibed.replace(qtAnswer,"");
                        if(answerRecibed.charAt(0)==' ')
                            answerRecibed=answerRecibed.substring(1);
                        qtAnswer=qt.getAnswer().replace(qtAnswer,"").replace("[","").replace("]","");
                        if(qtAnswer.charAt(0)==' ')
                            qtAnswer=qtAnswer.substring(1);
                        int noImpAnswers=tokens(qtAnswer,'}');
                        if(noImpAnswers>0){//there are answers not importants
                            String auxQtNiA, niaStr=qtAnswer.toLowerCase();
                            answerRecibed=answerRecibed.toLowerCase();
                            for(int nia=0; nia<noImpAnswers; nia++){
                                auxQtNiA=niaStr.substring(niaStr.indexOf('{')+1, niaStr.indexOf('}'));
                                StringTokenizer tokAnswerReceived= new StringTokenizer(answerRecibed);
                                int words =tokAnswerReceived.countTokens();
                                for(int pal=0; pal<words; pal++){ //search answers not importans and quit it
                                    int theshold=0;
                                    if(auxQtNiA.length()<=3){theshold=0;}
                                    else if(auxQtNiA.length()<=6){theshold=1;}
                                    else if(auxQtNiA.length()<=9){theshold=2;}
                                    else if(auxQtNiA.length()<=15 && auxQtNiA.length()>15){theshold=3;}
                                    String anstok = tokAnswerReceived.nextToken();
                                    if(lv.calculate(auxQtNiA,anstok)<=theshold){
                                        answerRecibed=answerRecibed.replace(auxQtNiA+" ","");
                                        answerRecibed=answerRecibed.replace(" "+auxQtNiA,"");
                                        answerRecibed=answerRecibed.replace(auxQtNiA,"");
                                        niaStr=niaStr.replace("{"+auxQtNiA+"} ","");
                                        niaStr=niaStr.replace(" {"+auxQtNiA+"}","");
                                        niaStr=niaStr.replace("{"+auxQtNiA+"}","");
                                    }
                                }
                            }
                            for(int nia=0; nia<noImpAnswers; nia++){
                                String aux = qtAnswer.substring(qtAnswer.indexOf('{'), qtAnswer.indexOf('}')+1);
                                qtAnswer=qtAnswer.replace(" "+aux,"");
                                qtAnswer=qtAnswer.replace(aux+" ","");
                                qtAnswer=qtAnswer.replace(aux,"");
                            }
                        }

                        StringTokenizer tokqAnswer= new StringTokenizer(qtAnswer);
                        StringTokenizer tokAnswerReceived= new StringTokenizer(qtAnswer);
                        String auxa="", auxb="";
                        if(tokqAnswer.countTokens()==tokqAnswer.countTokens()){
                            editTextSetColor(getResources().getColor(R.color.green_backgroud),drawable,text);
                            while(tokqAnswer.countTokens()>0){
                                int theshold=0;
                                auxa = tokqAnswer.nextToken();
                                auxb = tokAnswerReceived.nextToken();
                                if(auxa.length()<=3){theshold=0;}
                                else if(auxa.length()<=6){theshold=1;}
                                else if(auxa.length()<=9){theshold=2;}
                                else if(auxa.length()<=15 && auxa.length()>15){theshold=3;}
                                if(lv.calculate(auxa,auxb)>theshold){
                                    correct=false;
                                    editTextSetColor(Color.RED,drawable,text);
                                }
                            }

                        }else {
                            correct=false;
                            editTextSetColor(Color.RED,drawable,text);
                        }
                    }else{
                        correct=false;
                        editTextSetColor(Color.RED,drawable,text);
                    }
                }
            }else{
                editTextSetColor(Color.RED,drawable,text);
                correct=false;
            }

        }
        return correct;
    }

    private boolean questionary(){
        boolean correct=true;
        String answerRecibed,qtAnswer;
        int i=0;
        int resID;
        correct=true;
        StringComparation lv = new StringComparation();

        for(Question qt:exercises.get(exercisePosition).getQuestions()){
            i++;
            resID = getResources().getIdentifier("qet"+i, "id", getPackageName());
            EditText text=(EditText) findViewById(resID);

            Drawable drawable = text.getBackground(); // get current EditText drawable

            answerRecibed = text.getText().toString();

            if(answerRecibed.length()>0){//si lo escrito es mayor de 0 caracteres

                qtAnswer = qt.getAnswer().replace("[","").replace("]","").replace("{","").replace("}","");
                if(qtAnswer.equalsIgnoreCase(answerRecibed)){
                    editTextSetColor(getResources().getColor(R.color.green_backgroud),drawable,text);
                }else {
                    int impAnswers=tokens(qt.getAnswer(),']');
                    if(impAnswers>0){
                        qtAnswer=qt.getAnswer().substring(qt.getAnswer().indexOf('[')+1, qt.getAnswer().indexOf(']'));
                    }else{
                        qtAnswer="";
                    }
                    if(answerRecibed.contains(qtAnswer) && !qtAnswer.equals("")){//this is the part exactly
                        answerRecibed=answerRecibed.replace(qtAnswer,"");
                        if(answerRecibed.charAt(0)==' ')
                            answerRecibed=answerRecibed.substring(1);
                        qtAnswer=qt.getAnswer().replace(qtAnswer,"").replace("[","").replace("]","");
                        if(qtAnswer.charAt(0)==' ')
                            qtAnswer=qtAnswer.substring(1);
                        int noImpAnswers=tokens(qtAnswer,'}');
                        if(noImpAnswers>0){//there are answers not importants
                            String auxQtNiA, niaStr=qtAnswer.toLowerCase();
                            answerRecibed=answerRecibed.toLowerCase();
                            for(int nia=0; nia<noImpAnswers; nia++){
                                auxQtNiA=niaStr.substring(niaStr.indexOf('{')+1, niaStr.indexOf('}'));
                                StringTokenizer tokAnswerReceived= new StringTokenizer(answerRecibed);
                                int words =tokAnswerReceived.countTokens();
                                for(int pal=0; pal<words; pal++){ //search answers not importans and quit it
                                    int theshold=0;
                                    if(auxQtNiA.length()<=3){theshold=0;}
                                    else if(auxQtNiA.length()<=6){theshold=1;}
                                    else if(auxQtNiA.length()<=9){theshold=2;}
                                    else if(auxQtNiA.length()<=15 && auxQtNiA.length()>15){theshold=3;}
                                    String anstok = tokAnswerReceived.nextToken();
                                    if(lv.calculate(auxQtNiA,anstok)<=theshold){
                                        answerRecibed=answerRecibed.replace(auxQtNiA+" ","");
                                        answerRecibed=answerRecibed.replace(" "+auxQtNiA,"");
                                        answerRecibed=answerRecibed.replace(auxQtNiA,"");
                                        niaStr=niaStr.replace("{"+auxQtNiA+"} ","");
                                        niaStr=niaStr.replace(" {"+auxQtNiA+"}","");
                                        niaStr=niaStr.replace("{"+auxQtNiA+"}","");
                                    }
                                }
                            }
                            for(int nia=0; nia<noImpAnswers; nia++){
                                String aux = qtAnswer.substring(qtAnswer.indexOf('{'), qtAnswer.indexOf('}')+1);
                                qtAnswer=qtAnswer.replace(" "+aux,"");
                                qtAnswer=qtAnswer.replace(aux+" ","");
                                qtAnswer=qtAnswer.replace(aux,"");
                            }
                        }

                        StringTokenizer tokqAnswer= new StringTokenizer(qtAnswer);
                        StringTokenizer tokAnswerReceived= new StringTokenizer(qtAnswer);
                        String auxa="", auxb="";
                        if(tokqAnswer.countTokens()==tokqAnswer.countTokens()){
                            editTextSetColor(getResources().getColor(R.color.green_backgroud),drawable,text);
                            while(tokqAnswer.countTokens()>0){
                                int theshold=0;
                                auxa = tokqAnswer.nextToken();
                                auxb = tokAnswerReceived.nextToken();
                                if(auxa.length()<=3){theshold=0;}
                                else if(auxa.length()<=6){theshold=1;}
                                else if(auxa.length()<=9){theshold=2;}
                                else if(auxa.length()<=15 && auxa.length()>15){theshold=3;}
                                if(lv.calculate(auxa,auxb)>theshold){
                                    correct=false;
                                    editTextSetColor(Color.RED,drawable,text);
                                }
                            }

                        }else {
                            correct=false;
                            editTextSetColor(Color.RED,drawable,text);
                        }
                    }else{
                        correct=false;
                        editTextSetColor(Color.RED,drawable,text);
                    }
                }
            }else{
                editTextSetColor(Color.RED,drawable,text);
                correct=false;
            }

        }
        return correct;
    }

    private boolean multipleoptions(){
        boolean correct=true;
//        relativeLmp = (RelativeLayout)findViewById(R.id.content);
        String answerRecibed,qtAnswer;
        int correcto=0;
        correcto = Integer.parseInt(exercises.get(exercisePosition).getAnswerok());

        if (correcto == rbcheked){
            if (rbcheked==1){
                rb1.setTextColor(getResources().getColor(R.color.green_backgroud));
                // rb1.setBackgroundColor(getResources().getColor(R.color.red_backgroud));

            }
            else if (rbcheked==2){
                rb2.setTextColor(getResources().getColor(R.color.green_backgroud));
                // rb2.setBackgroundColor(getResources().getColor(R.color.red_backgroud));


            }
            else if (rbcheked==3){
                rb3.setTextColor(getResources().getColor(R.color.green_backgroud));
                // rb3.setBackgroundColor(getResources().getColor(R.color.red_backgroud));
            }
            correct=true;

        }else{

            if (rbcheked==1){
                rb1.setTextColor(getResources().getColor(R.color.red));

            }
            else if (rbcheked==2){
                rb2.setTextColor(getResources().getColor(R.color.red));

            }
            else if (rbcheked==3){
                rb3.setTextColor(getResources().getColor(R.color.red));

            }
//            relativeLmp.setBackgroundColor(getResources().getColor(R.color.red_backgroud));
            correct=false;
        }

        return correct;
    }


    private boolean presentationmultipleoptions(){
        boolean correct=true;
//        relativeLmp = (RelativeLayout)findViewById(R.id.content);
        String answerRecibed,qtAnswer;
        int correcto=0;
        correcto = Integer.parseInt(exercises.get(exercisePosition).getAnswerok());

        if (correcto == rbcheked){
            if (rbcheked==1){
                rbm1.setTextColor(getResources().getColor(R.color.green_backgroud));
                // rb1.setBackgroundColor(getResources().getColor(R.color.red_backgroud));

            }
            else if (rbcheked==2){
                rbm2.setTextColor(getResources().getColor(R.color.green_backgroud));
                // rb2.setBackgroundColor(getResources().getColor(R.color.red_backgroud));


            }
            else if (rbcheked==3){
                rbm3.setTextColor(getResources().getColor(R.color.green_backgroud));
                // rb3.setBackgroundColor(getResources().getColor(R.color.red_backgroud));
            }
            correct=true;

        }else{

            if (rbcheked==1){
                rbm1.setTextColor(getResources().getColor(R.color.red));

            }
            else if (rbcheked==2){
                rbm2.setTextColor(getResources().getColor(R.color.red));

            }
            else if (rbcheked==3){
                rbm3.setTextColor(getResources().getColor(R.color.red));

            }
//            relativeLmp.setBackgroundColor(getResources().getColor(R.color.red_backgroud));
            correct=false;
        }

        return correct;
    }


    //
    public void onRBmultipleClicked(View view) {

        //volver a colorear negro los text
        rb1.setTextColor(getResources().getColor(R.color.black));
        rb2.setTextColor(getResources().getColor(R.color.black));
        rb3.setTextColor(getResources().getColor(R.color.black));
//       relativeLmp.setBackgroundColor(getResources().getColor(R.color.iron));
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.ranswer1:
                if (checked){
                    rbcheked = 1;
                }
                //
                break;
            case R.id.ranswer2:
                if (checked){
                    rbcheked = 2;
                }
                //
                break;
            case R.id.ranswer3:
                if (checked){
                    rbcheked = 3;
                }
                //
                break;


        }
    }

    //
    public void onRBpresentationClicked(View view) {

        //volver a colorear negro los text
        rbm1.setTextColor(getResources().getColor(R.color.black));
        rbm2.setTextColor(getResources().getColor(R.color.black));
        rbm3.setTextColor(getResources().getColor(R.color.black));
//        relativeLmp.setBackgroundColor(getResources().getColor(R.color.iron));
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.pranswer1:
                if (checked){
                    rbcheked = 1;
                }
                //
                break;
            case R.id.pranswer2:
                if (checked){
                    rbcheked = 2;
                }
                //
                break;

            case R.id.pranswer3:
                if (checked){
                    rbcheked = 3;
                }
                //
                break;
        }
    }



    //funtion
    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }



}// fin class ExerciseActivity



