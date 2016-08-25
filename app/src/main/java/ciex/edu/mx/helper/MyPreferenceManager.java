/**
 * Created by azulyoro on 11/04/16.
 * This class stores data in SharedPreferences.
 * Here we temporarily stores the unread push notifications
 * in order to append them to new messages.
 */
package ciex.edu.mx.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import ciex.edu.mx.model.User;

public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "CIEX_2016";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_MATRICULA = "user_matricula";
    private static final String KEY_USER_DEVICE_KEY = "device_key";
    private static final String KEY_USER_CREATED_AT = "created_at";
    private static final String KEY_COURSE = "course_";
    private static final String KEY_COURSES = "course";
    private static final String KEY_COURSES_ID = "courses_id";
    private static final String KEY_COURSES_LEVEL = "courses_level";
    private static final String KEY_TOTAL_UNREAD_MESSAGESS="totalUnreadMessages";

    private static final String KEY_NOTIFICATIONS = "notifications";
    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public void storeCourseCount(String course, int val){
        editor.putInt(KEY_COURSE+course,val);
        editor.commit();
    }

    public int getCourseCount(String course){
        return pref.getInt(KEY_COURSE+course,0);
    }

    public int getTotalUnreadMessages(){
        return pref.getInt(KEY_TOTAL_UNREAD_MESSAGESS,0);
    }

    public void storeTotalUnreadMessages(int val){
        editor.putInt(KEY_TOTAL_UNREAD_MESSAGESS,val);
        editor.commit();
    }

    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_MATRICULA, user.getMatricula());
        editor.putString(KEY_USER_DEVICE_KEY, user.getDevice_key());
        editor.putString(KEY_USER_CREATED_AT, user.getCreated_at());
        editor.putInt(KEY_COURSES, user.getCourse_id().length);
        String [] course_id = user.getCourse_id();
        String [] course_level = user.getCourse_level();
        for(int i=0; i<course_id.length; i++){
            editor.putString(KEY_COURSE+i,course_id[i]);
            editor.putString(KEY_COURSE+i+"_level",course_level[i]);

        }
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getMatricula());
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, matricula, device_key, created_at;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            matricula = pref.getString(KEY_USER_MATRICULA, null);
            device_key = pref.getString(KEY_USER_DEVICE_KEY,null);
            created_at = pref.getString(KEY_USER_CREATED_AT,null);
            int aux = pref.getInt(KEY_COURSES,0);
            if (aux > 0) {
                String course_id[]=new String[aux];
                String course_level[]=new String[aux];
                for (int i = 0; i < aux; i++) {
                    course_id[i] = pref.getString(KEY_COURSE + i, null);
                    course_level[i] = pref.getString(KEY_COURSE + i+"_level", null);
                }
                User user = new User(id, name, matricula, device_key, created_at, course_id, course_level);
                return user;
            }else {
                User user = new User(id, name, matricula, device_key, created_at, null, null);
                return user;
            }
        }
        return null;
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public void cleanNotification() {
        editor.putString(KEY_NOTIFICATIONS, "");
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}

