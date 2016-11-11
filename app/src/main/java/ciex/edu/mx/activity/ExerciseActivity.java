package ciex.edu.mx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.StringTokenizer;

import ciex.edu.mx.R;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.app.StringComparation;
import ciex.edu.mx.handlesXML.exerciseXML;
import ciex.edu.mx.model.Exercise;
import ciex.edu.mx.model.Question;

public class ExerciseActivity extends AppCompatActivity {
    private static final String TAG = ExerciseActivity.class.getSimpleName();
    private String title, level, book, unit, exerciseType;
    private ArrayList<Exercise> exercises;
    private ImageView iv1;
    private TextView tv1,nov1, tv2, rb1,rb2,rb3;
    private int exercisePosition = 0, exercise, rbcheked=1;
    private ViewFlipper vf;
    private Boolean finish;
    private RelativeLayout relativeLmp;
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

        exercises=loadXml();
        finish = false;

        setContentView(R.layout.activity_exercise);
        vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        //image = (ImageView) findViewById(R.id.imagePresentation);
        //information = (TextView) findViewById(R.id.infoPresentation);

        showExercise();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.evaluate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finish==true){

                    Intent intent = new Intent(ExerciseActivity.this, UnitActivity.class);
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
                    }
                    if(!correct){
                        Snackbar.make(view, "Respuesta incorrecta, Intentalo de nuevo", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{
                        cleanText();
                        Snackbar.make(view, "Respuesta  correcta", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        exercisePosition++;
                        showExercise();
                        /////////////
                    }
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
        Intent intent = new Intent(ExerciseActivity.this, LoginActivity.class);
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
                    tv1 = (TextView) findViewById(R.id.infoPresentation);
                    iv1.setImageBitmap(exercises.get(exercisePosition).getImage());
                    tv1.setText(exercises.get(exercisePosition).getInformation().replace("\\n", System.getProperty("line.separator")));

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
                    break;

                case "questionary":
                    exerciseType = "questionary";
                    //vf.setDisplayedChild(R.id.questionaryView);
                    //vf.showNext();
                    vf.setDisplayedChild(1);
                    //iv1 = (ImageView) findViewById(R.id.imagePresentation);
                    nov1 = (TextView) findViewById(R.id.noQuestionary);
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
                    break;

                case "multipleoptions":
                    exerciseType = "multipleoptions";
                    //vf.setDisplayedChild(R.id.questionaryView);
                    //vf.showNext();
                    vf.setDisplayedChild(2);
                    //iv1 = (ImageView) findViewById(R.id.imagePresentation);
                    nov1 = (TextView) findViewById(R.id.nomultipleoptions);
                    exercise = exercisePosition + 1;
                    nov1.setText(Integer.toString(exercise)+ " de " + exercises.size() );
                    tv1 = (TextView) findViewById(R.id.infomultipleoptions);
                    // iv1.setImageBitmap(exercises.get(exercisePosition).getImage());
                    tv1.setText(exercises.get(exercisePosition).getInformation().replace("\\n", System.getProperty("line.separator")));
                    tv2 = (TextView) findViewById(R.id.mtv1);
                    tv2.setText(exercises.get(exercisePosition).getQuestion());

                    rb1 =(RadioButton) findViewById(R.id.ranswer1);
                    rb1.setText(exercises.get(exercisePosition).getAnswer1());

                    rb2 =(RadioButton) findViewById(R.id.ranswer2);
                    rb2.setText(exercises.get(exercisePosition).getAnswer2());

                    rb3 =(RadioButton) findViewById(R.id.ranswer3);
                    rb3.setText(exercises.get(exercisePosition).getAnswer3());

                    break;
            }
        }else{

            //if (vf.getDisplayedChild() == 0)

            // Next screen comes in from left.
            //vf.setInAnimation(this, R.anim.slide_in_from_left);

            // Current screen goes out from right.
            //vf.setOutAnimation(this, R.anim.slide_out_to_right);

            finish = true;
            vf.setDisplayedChild(3);

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
                        editTextSetColor(Color.GREEN,drawable,text);
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
                                editTextSetColor(Color.GREEN,drawable,text);
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
                    editTextSetColor(Color.BLUE,drawable,text);
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
                            editTextSetColor(Color.BLUE,drawable,text);
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
        relativeLmp = (RelativeLayout)findViewById(R.id.content);
        String answerRecibed,qtAnswer;
        int correcto=0;
        correcto = Integer.parseInt(exercises.get(exercisePosition).getAnswerok());

        if (correcto == rbcheked){
            correct=true;

        }else{

            if (rbcheked==1)
                rb1.setTextColor(getResources().getColor(R.color.red));
            else if (rbcheked==2)
                rb2.setTextColor(getResources().getColor(R.color.red));
            else if (rbcheked==3)
                rb3.setTextColor(getResources().getColor(R.color.red));
//            relativeLmp.setBackgroundColor(getResources().getColor(R.color.red_backgroud));
            correct=false;
        }

        return correct;
    }


    //
    public void onRadioButtonClicked(View view) {

        //volver a colorear negro los text
        rb1.setTextColor(getResources().getColor(R.color.black));
        rb2.setTextColor(getResources().getColor(R.color.black));
        rb3.setTextColor(getResources().getColor(R.color.black));
//        relativeLmp.setBackgroundColor(getResources().getColor(R.color.iron));
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



}// fin class ExerciseActivity