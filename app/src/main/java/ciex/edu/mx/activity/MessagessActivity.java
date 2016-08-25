/**
 * Created by azulyoro on 11/04/16.
 * > fetchChatThread() method fetches all the previous messages exchanged in the chat room.
 * > Broadcast receiver is registered for PUSH_NOTIFICATION in onResume() method.
 * > handlePushNotification() method handles the push message and
 *      append it to adapter array list. Upon calling the notifyDataSetChanged()
 *      the new message will be displayed in discussion thread.
 * > The self and other messages are aligned left or right by comparing
 *      the user id in the push message with the id of the user currently logged in.
 * > sendMessage() method sends a new message to server to post it in the chat room.
 * On the server the message will sent to gcm server to broadcast it
 *      to other devices subscribed to that chat room’s topic.
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
import ciex.edu.mx.adapter.ChatRoomThreadAdapter;
import ciex.edu.mx.app.Config;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.gcm.NotificationUtils;
import ciex.edu.mx.model.Message;
import ciex.edu.mx.model.User;

public class MessagessActivity extends AppCompatActivity {

    private String TAG = MessagessActivity.class.getSimpleName();

    private String courseId;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagess);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        courseId = intent.getStringExtra("course_id");
        String title = intent.getStringExtra("name");


        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if (courseId == null) {
            Toast.makeText(getApplicationContext(), "Course not found!", Toast.
                    LENGTH_SHORT).show();
            finish();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        // self user id is to identify the message owner
        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        fetchChatThread();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        MyApplication.getInstance().getPrefManager().cleanNotification();
        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlePushNotification(intent);
    }

    /**
     * Fetching all the messages of a single chat room
     * */
    private void fetchChatThread() {

        String endPoint = EndPoints.CHAT_THREAD.replace("_ID_", courseId);
        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (!obj.getBoolean("error")) {
                        JSONArray commentsObj = obj.getJSONArray("messages");

                        for (int i = 0; i < commentsObj.length(); i++) {

                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("message");
                            String createdAt = commentObj.getString("created_at");
                            String image = commentObj.getString("image");

                            JSONObject adminObj = commentObj.getJSONObject("admin");
                            String adminId = adminObj.getString("id");
                            String adminName = adminObj.getString("admin_name");
                            User user = new User(adminId, adminName, null, null, null, null,null);

                            Message message = new Message();
                            message.setId(commentId);
                            message.setMessage(commentText);
                            message.setCreatedAt(createdAt);
                            message.setUser(user);
                            message.setImage(image);

                            messageArrayList.add(message);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().scrollToPosition
                                    (mAdapter.getItemCount() - 1);
                        }

                        updateMessageCount();

                    } else {
                        Toast.makeText(getApplicationContext(), "No internet connection E.1"
                                , Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "No internet connection E.2",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "No internet connection E.3",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("course_id");

        if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
            updateMessageCount();
        }
    }

    private void updateMessageCount(){
        int count = MyApplication.getInstance().getPrefManager().getCourseCount(courseId);
        MyApplication.getInstance().getPrefManager().storeCourseCount(courseId,0);
        int total = MyApplication.getInstance().getPrefManager().getTotalUnreadMessages();
        MyApplication.getInstance().getPrefManager().storeTotalUnreadMessages(((total-count)<0)?0:total-count);
    }
}
