package ciex.edu.mx.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ciex.edu.mx.R;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.app.StringComparation;
import ciex.edu.mx.handlesXML.exerciseXML;
import ciex.edu.mx.handlesXML.resourceXML;
import ciex.edu.mx.model.Exercise;
import ciex.edu.mx.model.Question;
import ciex.edu.mx.model.Resource;


public class ResourceActivity extends AppCompatActivity {
    private static final String TAG = ExerciseActivity.class.getSimpleName();
    private String title, level, book, unit, exerciseType;
    private ArrayList<Resource> exercises;
    private ImageView iv1;
    private TextView tv1,nov1, tv2, rb1,rb2,rb3;
    private int exercisePosition = 0, exercise, rbcheked=1;
    private ViewFlipper vf;
    private Boolean finish;

    private ImageView imageview;
    private VideoView videoview;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        finish=false;
        exercises=loadXml();

        setContentView(R.layout.activity_resource);
        vf = (ViewFlipper) findViewById(R.id.resourceviewFlipper);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        showExercise();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finish==true){

                    Intent intent = new Intent(ResourceActivity.this, UnitActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("level",level);
                    intent.putExtra("book",book);
                    startActivity(intent);

                } else {

                    Snackbar.make(view, "Siguiente recurso multimedia", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    exercisePosition++;
                    showExercise();
                    /////////////
                }

            }
        });
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
        Intent intent = new Intent(ResourceActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public ArrayList<Resource> loadXml(){
        resourceXML obj;
        String url = EndPoints.UNITS_CONTENT_URL.replace("level?","level"+level)
                .replace("book?",title.replace(" ", ""));
        obj = new resourceXML(url, unit);
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
                case "image":
                    exerciseType = "image";
                    vf.setDisplayedChild(0);
                    nov1 = (TextView) findViewById(R.id.noresource);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );

                    iv1 = (ImageView) findViewById(R.id.imageexercise);
                    tv1 = (TextView) findViewById(R.id.title);
                    iv1.setImageBitmap(exercises.get(exercisePosition).getImage());
                    tv1.setText(exercises.get(exercisePosition).getTitle());

                    break;

                case "video":
                    exerciseType = "video";
                    vf.setDisplayedChild(1);
                    nov1 = (TextView) findViewById(R.id.vnoresource);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );

                   /* iv1 = (ImageView) findViewById(R.id.videoexercise);
                    iv1.setImageBitmap(exercises.get(exercisePosition).getImage());*/
                    tv1 = (TextView) findViewById(R.id.vtitle);
                    tv1.setText(exercises.get(exercisePosition).getTitle());

                    break;

            }
        }else{

            //if (vf.getDisplayedChild() == 0)

            // Next screen comes in from left.
            //vf.setInAnimation(this, R.anim.slide_in_from_left);

            // Current screen goes out from right.
            //vf.setOutAnimation(this, R.anim.slide_out_to_right);

            finish = true;
            vf.setDisplayedChild(2);

            TextView tvfinish = (TextView) findViewById(R.id.infoResults);
            tvfinish.setText("Fin de recursos Multimedia \nPulsa > para Iniciar los ejercicios");

        }
    }

    public  void clickVideoview(View view){
        vf.setDisplayedChild(3);

        videoview = (VideoView) findViewById(R.id.VideoView);
        pDialog = new ProgressDialog(ResourceActivity.this);

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
                    ResourceActivity.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(exercises.get(exercisePosition).getUrl());
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

    }

    public  void clickImageview(View view) {
        vf.setDisplayedChild(4);

        imageview = (ImageView) findViewById(R.id.ImageView);
        pDialog = new ProgressDialog(ResourceActivity.this);

        Bitmap bm = null;
        try {
            URL _url = new URL(exercises.get(exercisePosition).getUrl());
            URLConnection con = _url.openConnection();
            con.connect();
            InputStream is = con.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {

        }

        imageview.setImageBitmap(bm);
    }



}
