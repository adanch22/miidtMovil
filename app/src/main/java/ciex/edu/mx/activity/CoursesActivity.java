/**
 * Created by azulyoro on 11/04/16.
 * > In order to receive the push notifications, device has to support google play services.
 *      So checkPlayServices() method is used to check the availability of google play services.
 *      If the play services are not available, we’ll simply close the app.
 * > Register a broadcast receiver in onResume() method for both
 *      REGISTRATION_COMPLETE and PUSH_NOTIFICATION intent filters.
 * > Unregister the broadcast receiver in onPause() method.
 * > Create an instance of broadcast receiver in onCreate() method
 *      in which onReceive() method will be triggered whenever
 *      gcm registration process is completed and a new push message is received.
 *
 *
 * > First user session is checked before setting the content view.
 * > On receiving the gcm registration token, user is subscribed to `global` topic.
 *      This allows us to send a notification to all the users from the admin panel.
 * > fetchChatRooms() is called in onCreate() method which fetches all the chat room information
 *      from the server. Once the chat rooms are received,
 *      user will be automatically subscribed to all the chat rooms topics.
 *      So that he will start receiving notifications whenever there is a active
 *      discussion going on in a chatroom.
 * > Broadcast receivers are registered / unregistered in onResume() / onPause() methods.
 * > Broadcast receivers will be triggered whenever a new push message
 *      is received in which handlePushNotification() method is called.
 * > handlePushNotification() handles the push notification by updating the recycler view items
 *      by updating last message and unread message count.
 */
package ciex.edu.mx.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ciex.edu.mx.R;
import ciex.edu.mx.adapter.ChatRoomsAdapter;
import ciex.edu.mx.app.Config;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.helper.SimpleDividerItemDecoration;
import ciex.edu.mx.model.Message;
import ciex.edu.mx.model.User;
import ciex.edu.mx.model.course;

public class CoursesActivity extends AppCompatActivity {

    private String TAG = CoursesActivity.class.getSimpleName();
    public  BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<course> courseArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;


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
        User user = MyApplication.getInstance().getPrefManager().getUser();


        setContentView(R.layout.activity_courses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        courseArrayList = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(this, courseArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener
                (getApplicationContext(), recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                course course = courseArrayList.get(position);
                Intent intent = new Intent(CoursesActivity.this, MessagessActivity.class);
                intent.putExtra("course_id", course.getId());
                intent.putExtra("name", course.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        fetchCourses(user.getId());
    }




    /**
     * fetching the chat rooms by making http call
     */
    private void fetchCourses(String id) {

        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.COURSES+"/"+id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (!obj.getBoolean("error")) {
                        int tot;
                        JSONArray coursesArray = obj.getJSONArray("course");
                        for (int i = 0; i < coursesArray.length(); i++) {
                            JSONObject courseObj = (JSONObject) coursesArray.get(i);
                            course co = new course();
                            co.setId(courseObj.getString("course_id"));
                            co.setName(courseObj.getString("course_name"));
                            tot = MyApplication.getInstance().getPrefManager().getCourseCount(courseObj.getString("course_id"));
                            co.setUnreadCount(tot);
                            co.setLastMessage(tot>0?courseObj.getString("lastMessage"):"");
                            co.setTimestamp(courseObj.getString("course_created_at"));
                            courseArrayList.add(co);
                        }
                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").
                                getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " +
                            e.getMessage(), Toast.LENGTH_LONG).show();
                }

                mAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(CoursesActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
     }

    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        String courseId = intent.getStringExtra("course_id");

        if (message != null && courseId != null) {
            for (int i = 0; i < courseArrayList.size(); i++) {
                course co = courseArrayList.get(i);
                if(co.getId().equals(courseId)){
                    co.setLastMessage(message.getMessage());
                    co.setUnreadCount(MyApplication.getInstance().getPrefManager().getCourseCount(courseId));
                    //courseArrayList.add(co);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }
}
