package ciex.edu.mx.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ciex.edu.mx.R;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.app.StringComparation;
import ciex.edu.mx.connection.ConnectivityReceiver;
import ciex.edu.mx.dialog.SimpleDialog;
import ciex.edu.mx.handlesXML.exerciseXML;
import ciex.edu.mx.handlesXML.resourceXML;
import ciex.edu.mx.model.Exercise;
import ciex.edu.mx.model.Question;
import ciex.edu.mx.model.Resource;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayerView;


public class ResourcesActivity extends AppCompatActivity   implements ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = ResourcesActivity.class.getSimpleName();
    private String title, level, book, unit, exerciseType;
    private ArrayList<Resource> exercises;
    private ImageView iv1;
    private TextView tv1,nov1, tv2, rb1,rb2,rb3;
    private int exercisePosition = 0, exercise, rbcheked=1;
    private ViewFlipper vf;
    private Boolean finish;
    private Menu menu;

    private ImageView imageview;
    private VideoView videoview;
    private YouTubePlayerView youTubeView;
    private ProgressDialog pDialog;

    private WebView web;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

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
        unit = cleanString(unit);

        finish=false;
        exercises=loadXml();

        setContentView(R.layout.activity_resources);
        vf = (ViewFlipper) findViewById(R.id.resourceviewFlipper);
        web = (WebView) findViewById(R.id.webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize("AIzaSyBiGmSIaCyEJMMYos2eeNeqHsbIpU8t3iY", this);
*/
        showExercise();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabresources);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finish==true){

                    Intent intent = new Intent(ResourcesActivity.this, UnitActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("level",level);
                    intent.putExtra("book",book);
                    startActivity(intent);

                } else {

                    Snackbar.make(view, "Siguiente recurso multimedia", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    exercisePosition++;
                    if (exerciseType.equals("video")){
                        videoview.stopPlayback();
                        videoview = null;
                    }


                    showExercise();
                    /////////////
                }

            }
        });
    }
/*
Codigo para actualizar el reproductor de video para expandir a pantalla completa
*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video, menu);
        this.menu = menu;

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.video_full:
                createNote();
                break;


        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void createNote() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

*/

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
                    .make(findViewById(R.id.fabresources), message, Snackbar.LENGTH_LONG);

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
        Intent intent = new Intent(ResourcesActivity.this, LoginActivity.class);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void showExercise(){
        if(exercisePosition<exercises.size()) {
            switch (exercises.get(exercisePosition).getType()) {
                case "image":
                    exerciseType = "image";
                    vf.setDisplayedChild(0);
                  /*  nov1 = (TextView) findViewById(R.id.noresource);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );

                    iv1 = (ImageView) findViewById(R.id.imageresource);
                    tv1 = (TextView) findViewById(R.id.title);
                    iv1.setImageBitmap(exercises.get(exercisePosition).getImage());
                    tv1.setText(exercises.get(exercisePosition).getTitle());*/

                    //abrir imagen en webview
                    exercise = exercisePosition + 1;
                    nov1 = (TextView) findViewById(R.id.noresource);
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );
                   /* tv1 = (TextView) findViewById(R.id.title);
                    tv1.setText(exercises.get(exercisePosition).getTitle());*/
                    WebSettings settings = web.getSettings();
                    settings.setLoadWithOverviewMode(true);
                    settings.setUseWideViewPort(true);
                    settings.setJavaScriptEnabled(true);
                    settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                    settings.setBuiltInZoomControls(true);
                    web.getSettings().setUseWideViewPort(true);
                    web.getSettings().setLoadWithOverviewMode(true);
                    web.loadUrl(exercises.get(exercisePosition).getUrl());

                    web.setWebViewClient(new WebViewClient());

                    break;

                case "video":
                    exerciseType = "video";
                    /*vf.setDisplayedChild(1);
                    nov1 = (TextView) findViewById(R.id.vnoresource);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );

                   *//* iv1 = (ImageView) findViewById(R.id.videoexercise);
                    iv1.setImageBitmap(exercises.get(exercisePosition).getImage());*//*
                    tv1 = (TextView) findViewById(R.id.vtitle);
                    tv1.setText(exercises.get(exercisePosition).getTitle());*/

                    vf.setDisplayedChild(1);
                    nov1 = (TextView) findViewById(R.id.vnoresource);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );
                    tv1 = (TextView) findViewById(R.id.vtitle);
                    tv1.setText(exercises.get(exercisePosition).getTitle());

                    videoview = (VideoView) findViewById(R.id.VideoView);
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    pDialog = new ProgressDialog(ResourcesActivity.this);

                    pDialog.setTitle(exercises.get(exercisePosition).getTitle() + "Video Streaming");
                    // Set progressbar message
                    pDialog.setMessage("Cargando...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    // Show progressbar
                    pDialog.show();
                    try {
                        // Start the MediaController
                        MediaController mediacontroller = new MediaController(
                                ResourcesActivity.this);
                        mediacontroller.setAnchorView(videoview);
                        // Get the URL from String VideoURL
                       // Uri video = Uri.parse(exercises.get(exercisePosition).getUrl());
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


                    break;

                case "pdf":
                    exerciseType = "pdf";
                    vf.setDisplayedChild(0);
                 /*   //abrir imagen en webview
                    exercise = exercisePosition + 1;
                    nov1 = (TextView) findViewById(R.id.noresource);
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );
                    WebView webx = (WebView) findViewById(R.id.webview);
                    WebSettings setting = webx.getSettings();
                    setting.setLoadWithOverviewMode(true);
                    setting.setUseWideViewPort(true);
                    setting.setDomStorageEnabled(true);
                    setting.setJavaScriptEnabled(true);
                    setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                    setting.setBuiltInZoomControls(true);
                    webx.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + exercises.get(exercisePosition).getUrl());
*/
                    break;


/*
                case "youtube":
                    exerciseType = "youtube";
                    */
/*vf.setDisplayedChild(1);
                    nov1 = (TextView) findViewById(R.id.vnoresource);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );

                   *//*
*/
/* iv1 = (ImageView) findViewById(R.id.videoexercise);
                    iv1.setImageBitmap(exercises.get(exercisePosition).getImage());*//*
*/
/*
                    tv1 = (TextView) findViewById(R.id.vtitle);
                    tv1.setText(exercises.get(exercisePosition).getTitle());*//*


                    vf.setDisplayedChild(4);
                    nov1 = (TextView) findViewById(R.id.ynoresource);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );
                    tv1 = (TextView) findViewById(R.id.ytitle);
                    tv1.setText(exercises.get(exercisePosition).getTitle());
                    break;
*/



            }
        }else{

            finish = true;
            vf.setDisplayedChild(2);

            TextView tvfinish = (TextView) findViewById(R.id.infoResults);
            tvfinish.setText("Fin de recursos Multimedia \nPulsa > para Iniciar los ejercicios");

        }
    }

    public  void clickVideoview(View view){
        vf.setDisplayedChild(3);

        videoview = (VideoView) findViewById(R.id.VideoView);
        pDialog = new ProgressDialog(ResourcesActivity.this);

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
                    ResourcesActivity.this);
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

    //funtion
    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }


   /* @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(this, "error de reproducción", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            player.loadVideo("W2PO1COj7-E");

            // Hiding player controls
            //player.setPlayerStyle(PlayerStyle.CHROMELESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize("AIzaSyBiGmSIaCyEJMMYos2eeNeqHsbIpU8t3iY", this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }
*/
}

