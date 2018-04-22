package ciex.edu.mx.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ciex.edu.mx.R;
import ciex.edu.mx.adapter.BookAdapter;
import ciex.edu.mx.app.Config;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.connection.ConnectivityReceiver;
import ciex.edu.mx.gcm.GcmIntentService;
import ciex.edu.mx.gcm.NotificationUtils;
import ciex.edu.mx.model.Book;
import ciex.edu.mx.model.Message;
import ciex.edu.mx.model.User;

public class BookActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private String TAG = BookActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private BookAdapter mAdapter;
    private ArrayList<Book> booksArrayList;
    private Menu menu;
    private CoordinatorLayout coordinatorLayout;
    private View view ;
    private TextView nameStudent;

    public BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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

        setContentView(R.layout.activity_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Broadcast receiver calls in two scenarios
         * 1. gcm registration is completed
         * 2. when new push notification is received
         * */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    subscribeToAllTopics();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                    Log.e(TAG, "GCM registration id is sent to our server");

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.book_recycler_view);
        booksArrayList = new ArrayList<>();
        mAdapter = new BookAdapter(this, booksArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new BookAdapter.RecyclerTouchListener
                (getApplicationContext(), recyclerView, new BookAdapter.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // when chat is clicked, launch full chat thread activity
                        Book book = booksArrayList.get(position);
                        Intent intent = new Intent(BookActivity.this, UnitActivity.class);
                        intent.putExtra("title",book.getTitle());
                        intent.putExtra("level",book.getLevel());
                        intent.putExtra("book",book.getIndex());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        checkConnection();

        User user=MyApplication.getInstance().getPrefManager().getUser();
        for(String level: user.getCourse_level()){
            fetchBooks(level);
        }

       String nombre =  MyApplication.getInstance().getPrefManager().getUser().getName();
        nameStudent = (TextView)findViewById(R.id.namestudent);
        nameStudent.setText("Bienvenid@ " + nombre);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Pulsa en las tarjetas para ir a los OA", Snackbar.LENGTH_INDEFINITE)
                .setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        snackbar.show();
    }//end Oncreate
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
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
            if (checkPlayServices()) {
                registerGCM();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;
        if (MyApplication.getInstance().getPrefManager().getTotalUnreadMessages() > 0) {
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getTitle().equals("message"))
                    menu.getItem(i).setIcon(R.drawable.ic_box_notify);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_logout:
                MyApplication.getInstance().logout();//logout and show Login
                break;

            case R.id.action_web:
               // startActivity(new Intent(BookActivity.this, WebActivity.class));//Show web
                break;
            case R.id.action_message:
                startActivity(new Intent(BookActivity.this, CoursesActivity.class));//Show messages
                break;

        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(BookActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void subscribeToGlobalTopic() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
        intent.putExtra(GcmIntentService.TOPIC, Config.TOPIC_GLOBAL);
        startService(intent);
    }

    // Subscribing to all chat room topics
    // each topic name starts with `topic_` followed by the ID of the chat room
    // Ex: topic_1, topic_2
    private void subscribeToAllTopics() {
        User user = MyApplication.getInstance().getPrefManager().getUser();
        if (user.getCourse_id()!=null) {
            String[] course_id = user.getCourse_id();
            for(String co : course_id) {
                Intent intent = new Intent(this, GcmIntentService.class);
                intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
                intent.putExtra(GcmIntentService.TOPIC, "topic_" + co);
                startService(intent);
            }
        }
    }

    /**
     * Handles new push notification
     */
    private void handlePushNotification(Intent intent) {

        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == Config.PUSH_TYPE_CHATROOM) {
            Log.e(TAG,"cr");
            Message message = (Message) intent.getSerializableExtra("message");
            String course = "course_"+intent.getStringExtra("chat_room_id");
            if (message != null && course != null) {
                //updateRow(chatRoomId, message);
            }



            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getTitle().equals("message"))
                    menu.getItem(i).setIcon(R.drawable.ic_box_notify);
            }

        } else if (type == Config.PUSH_TYPE_USER) {
            Log.e(TAG,"user");
            // push belongs to user alone
            // just showing the message in a toast
            Message message = (Message) intent.getSerializableExtra("message");
            Toast.makeText(getApplicationContext(), "New push: " + message.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(),
                        "This device is not supported. Google Play Services not installed!",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
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

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    /**
     * Fetching all the units of a single book
     * */
    private void fetchBooks(final String level) {
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.BOOKS+level, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (!obj.getBoolean("error")) {
                        JSONArray tempBooks =  obj.getJSONArray("books");
                        for (int i = 0; i < tempBooks.length(); i++) {
                            JSONObject temp = (JSONObject) tempBooks.get(i);
                            Book book = new Book();
                            book.setTitle(temp.getString("title"));
                            book.setImageName(temp.getString("image"));
                            book.setLevel(level);
                            book.setIndex((i+1)+"");

                            //eliminar acentos y espacios
                            String aux = temp.getString("title").replace(" ","");
                            aux = cleanString(aux);

                            book.setUrlPortada(EndPoints.BOOKS_URL.replace("level?","level"+level).replace("book?", aux)
                                    + temp.getString("image"));
                           /* book.setUrlPortada(EndPoints.BOOKS_URL.replace("level?","level"+level)
                                    .replace("book?",temp.getString("title"))+temp.getString("image"));*/
                            booksArrayList.add(book);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().scrollToPosition
                                    (mAdapter.getItemCount() - 1);
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "catch_error", Toast.LENGTH_SHORT).show();
                    Log.e("aqui",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Conection failed. Please try in a while", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    //funtion
    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }
}


