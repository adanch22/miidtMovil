package ciex.edu.mx.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import ciex.edu.mx.R;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.app.StringComparation;
import ciex.edu.mx.connection.ConnectivityReceiver;
import ciex.edu.mx.dialog.SimpleDialog;
import ciex.edu.mx.handlesXML.exerciseXML;
import ciex.edu.mx.model.Exercise;
import ciex.edu.mx.model.Question;

import ciex.edu.mx.R;

import static ciex.edu.mx.app.EndPoints.BASE_URL;

public class QuizActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {

    private static final String TAG = ExercisesActivity.class.getSimpleName();
    private String title, level, book, unit, exerciseType;
    private Menu menu;
    private ArrayList<Exercise> exercises;
    private VideoView iv1;
    private TextView tv1,nov1, tv2;
    private RadioButton rb1,rb2,rb3, rbm1, rbm2, rbm3;
    private int exercisePosition = 0, exercise, rbcheked=1;
    private ViewFlipper vf;
    private Boolean finish,play;
    private int error, ok, pbvalue;

    private Snackbar snackbar;


    private int iClicks,indicador;
    private ProgressDialog pDialog;


    //variables de la progressbar
    private int pbne=0,  progressStatus = 0;
    private Handler handler = new Handler();
    ProgressBar myProgressBar;

    private RelativeLayout relativeLmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        iClicks = 0;
        indicador = -1;
        play=false;
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                       TextView txtClicks = (TextView) findViewById(R.id.vqexercise);
                        // task to be done every 1000 milliseconds
                        if ((exercisePosition+1)<exercises.size()){
                            indicador = Integer.parseInt(exercises.get(exercise+1).getInformation());
                        }
                        else
                            indicador =500;

                        if (play){
                            iClicks = iClicks + 1;
                        }


                        if ((indicador+1) == iClicks){
                            iv1.pause();
                            exercisePosition++;
                            showExercise();
                            play=false;
                            iClicks = iClicks -2;
                        }

//                       txtClicks.setText(String.valueOf(iClicks + " "+indicador));
                    }
                });

            }
        }, 0, 1000);

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
        ok=0;

        setContentView(R.layout.activity_quiz);
        vf = (ViewFlipper) findViewById(R.id.quizviewFlipper);
        toolbar = (Toolbar) findViewById(R.id.toolbarquiz);
        setSupportActionBar(toolbar);

        //mostrar el video para videoquiz
        showvideoquiz();

        //Barra Progresiva
        pbvalue= (100 / exercises.size()+1);
        myProgressBar=(ProgressBar)findViewById(R.id.simpleProgressBar); // initiate the progress bar
        myProgressBar.setMax(100); // 100 maximum value for the progress value
        myProgressBar.setProgress(5); // 50 default progress value for the progress bar
       // showExercise();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabquiz);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finish==true){
                    Intent intent = new Intent(QuizActivity.this, UnitActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("level",level);
                    intent.putExtra("book",book);
                    startActivity(intent);


                } else {
                    boolean correct=false;

                    switch (exerciseType){
                        case "presentation":
                           // correct=presentation();
                            correct =true;

                            break;
                        /*case "questionary" :
                            correct=questionary();
                            break;*/

                        case "multipleoptions":
                            correct=multipleoptions();
                            break;

                       /* case "presentationm":
                            correct = presentationmultipleoptions();
                            break;*/
                    }
                    if(!correct){
                        switch (exerciseType){
                            case "multipleoptions":
                                error= error+1;
                                if (error<3)
                                    Snackbar.make(view, "Respuesta incorrecta, Intentalo de nuevo",
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                else
                                    Snackbar.make(view, "La respuesta correcta es la opción: "+ exercises.get(exercisePosition).getAnswerok(),
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                break;

                        }
                    }else{
                       /* cleanText();*/
                        error=0;
                        switch (exerciseType){
                            case "presentation":
                                if ((exercisePosition+1)>=exercises.size()){
                                    exercisePosition++;
                                    iv1.stopPlayback();
                                    showExercise();
                                }
                               /* iv1.pause();
                                play=false;
                                exercisePosition++;
                                showExercise();*/

                                break;


                            case "multipleoptions":
                                if (ok==1) {
                                    exerciseType = "presentation";
                                    vf.setDisplayedChild(0);
                                    iv1.start();
                                    play=true;
                                    ok =0;

                                }else{
                                    Snackbar.make(view, "Respuesta correcta, pulsa el botón azul para continuar", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    ok = ok +1;
                                    myProgressBar.setProgress(pbvalue*exercisePosition);
                                }

                                break;


                        }


                        /////////////
                    }
                }

            }
        });
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
                    .make(findViewById(R.id.fabquiz), message, Snackbar.LENGTH_LONG);

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

    private void launchLoginActivity() {
        Intent intent = new Intent(QuizActivity.this, LoginActivity.class);
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
    public void showvideoquiz() {
        exerciseType = "presentation";
        vf.setDisplayedChild(0);
        /*nov1 = (TextView) findViewById(R.id.vqexercise);
        exercise = 0;
        nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );
*/
        iv1 = (VideoView) findViewById(R.id.vqvideoView);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pDialog = new ProgressDialog(QuizActivity.this);

        pDialog.setTitle("Video Streaming");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();
        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    QuizActivity.this);
            mediacontroller.setAnchorView(iv1);
            // Get the URL from String VideoURL
            // Uri video = Uri.parse(exercises.get(exercisePosition).getUrl());
            String url = EndPoints.UNITS_CONTENT_URL.replace("level?","level"+level).replace("book?",title.replace(" ", ""));
            Uri video = Uri.parse( url + exercises.get(exercise).getInformation());

           // iv1.setMediaController(mediacontroller);
            iv1.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        iv1.requestFocus();
        iv1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                iv1.start();
                play=true;
            }
        });

        snackbar = Snackbar
                .make(findViewById(R.id.fabquiz), "Video en reproducción, espera la pregunta", Snackbar.LENGTH_INDEFINITE)
                .setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        snackbar.show();
    }


    public void showExercise(){
        if(exercisePosition<exercises.size()) {
            switch (exercises.get(exercisePosition).getType()) {

                case "multipleoptions":
                    exerciseType = "multipleoptions";
                    //vf.setDisplayedChild(R.id.questionaryView);
                    //vf.showNext();
                    vf.setDisplayedChild(1);
                    //iv1 = (ImageView) findViewById(R.id.imagePresentation);
                    nov1 = (TextView) findViewById(R.id.nomultiplequiz);
                    exercise = exercisePosition;
                    nov1.setText(Integer.toString(exercise)+ " de " + (exercises.size()-1) );
                   // tv1 = (TextView) findViewById(R.id.infomultipleoptions);
                    // iv1.setImageBitmap(exercises.get(exercisePosition).getImage());
                   // tv1.setText(exercises.get(exercisePosition).getInformation().replace("\\n", System.getProperty("line.separator")));
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
                            .make(findViewById(R.id.fabquiz), "Elije una opción  y evalua tu respuesta con el botón azul", Snackbar.LENGTH_INDEFINITE)
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
                vf.setDisplayedChild(2);



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
               // rb1.setBackgroundColor(getResources().getColor(R.color.red_backgroud));

            }
            else if (rbcheked==2){
                rb2.setTextColor(getResources().getColor(R.color.red));
                //rb2.setBackgroundColor(getResources().getColor(R.color.red_backgroud));


            }
            else if (rbcheked==3){
                rb3.setTextColor(getResources().getColor(R.color.red));
                //rb3.setBackgroundColor(getResources().getColor(R.color.red_backgroud));
            }
//            relativeLmp.setBackgroundColor(getResources().getColor(R.color.red_backgroud));
            correct=false;
        }

        return correct;
    }

    public void onRBquizClicked(View view) {

        //volver a colorear negro los text
        rb1.setTextColor(getResources().getColor(R.color.black));
        rb2.setTextColor(getResources().getColor(R.color.black));
        rb3.setTextColor(getResources().getColor(R.color.black));
         /* rb1.setBackgroundColor(getResources().getColor(R.color.iron));
          rb2.setBackgroundColor(getResources().getColor(R.color.iron));
          rb3.setBackgroundColor(getResources().getColor(R.color.iron));*/


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

    //funtion
    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }

}
