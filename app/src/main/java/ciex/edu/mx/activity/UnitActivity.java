package ciex.edu.mx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;

import ciex.edu.mx.R;
import ciex.edu.mx.adapter.unitAdapter;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.connection.ConnectivityReceiver;
import ciex.edu.mx.dialog.ListDialog;
import ciex.edu.mx.handlesXML.unitsXML;
import ciex.edu.mx.model.Unit;

public class UnitActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    private String TAG = UnitActivity.class.getSimpleName();
    private Menu menu;
    private RecyclerView recyclerView;
    private unitAdapter mAdapter;
    private ArrayList<Unit> unitsArrayList;
    private String title, level, book, type;
    private TextView nameStudent;

    private CoordinatorLayout coordinatorLayout;
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
        MyApplication.getInstance().authenticate();

        title=getIntent().getStringExtra("title");
        title = cleanString(title);
        level=getIntent().getStringExtra("level");
        book=getIntent().getStringExtra("book");
        book = cleanString(book);

        setContentView(R.layout.activity_unit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        //reciclerview
        recyclerView = (RecyclerView) findViewById(R.id.unit_recycler_view);
        unitsArrayList = new ArrayList<>();
        mAdapter = new unitAdapter(this, unitsArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new unitAdapter.RecyclerTouchListener
                (getApplicationContext(), recyclerView, new unitAdapter.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                       // when chat is clicked, launch full chat thread activity
                       /* Intent intent = new Intent(UnitActivity.this, ExerciseActivity.class);
                        intent.putExtra("title",title);
                        intent.putExtra("level",level);
                        intent.putExtra("book",book);
                        intent.putExtra("unit",unitsArrayList.get(position).getTitle());
                        startActivity(intent);*/
                        new ListDialog(title, unitsArrayList.get(position).getType(), level, book , unitsArrayList.get(position).getTitle()).show(getSupportFragmentManager(), "ListDialog");
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        fetchUnits();

        String nombre =  MyApplication.getInstance().getPrefManager().getUser().getName();
        nameStudent = (TextView)findViewById(R.id.namestudent);
        nameStudent.setText("Bienvenid@ " + nombre);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Pulsa en las tarjetas para ir al contenido", Snackbar.LENGTH_INDEFINITE)
                .setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_logout:
                MyApplication.getInstance().logout();//logout and show Login
                break;
            case R.id.action_web:
//                startActivity(new Intent(UnitActivity.this, WebActivity.class));//Show web
                break;
            case R.id.action_message:
                startActivity(new Intent(UnitActivity.this, CoursesActivity.class));//Show messages
                break;

        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(UnitActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.getInstance().getPrefManager().getTotalUnreadMessages() > 0 && menu!=null) {
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getTitle().equals("message"))
                    menu.getItem(i).setIcon(R.drawable.ic_box_notify);
            }
        }else if(menu!=null){
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getTitle().equals("message"))
                    menu.getItem(i).setIcon(R.drawable.ic_box);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Fetching all the units of a single book
     * */
    private void fetchUnits() {
        unitsXML obj;
       /* String url = EndPoints.UNITS_CONTENT_URL.replace("level?","level"+level)
                .replace("book?","book"+book);*/


        String url = EndPoints.UNITS_CONTENT_URL.replace("level?","level"+level)
                .replace("book?",title.replace(" ",""));

        obj = new unitsXML(url);
        obj.fetchXML();
        while(obj.parsingComplete);
        if (obj.isReadcorrect()) {
            for (int i=0; i<obj.getCountUnits(); i++ ){
                unitsArrayList.add(obj.getUnits(i));
            }

        }else{
            Log.e(TAG, "Conection Error");
        }
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getItemCount() > 1) {
            recyclerView.getLayoutManager().scrollToPosition
                    (mAdapter.getItemCount() - 1);
        }
    }


    //funtion
    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
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
                    .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }else{
            /**
             * Always check for google play services availability before
             * proceeding further with GCM
             * */
           /* if (checkPlayServices()) {
                registerGCM();
            }*/
        }
    }

}
