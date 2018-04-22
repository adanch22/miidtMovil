/**
 * Created by azulyoro on 11/04/16.
 * > Before setting the contentView, we’ll check for user session in shared preferences.
 *      If the user is already logged in, the CoursesActivity will be launched.
 * > login() method makes an http request to login endpoint by passing
 *      name and email as post parameters. On the server a new user will be created
 *      and the json response will be served.
 * > After parsing the json, user session will be created by storing the user object
 *      in shared preferences and CoursesActivity will be launched.
 */

package ciex.edu.mx.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ciex.edu.mx.R;
import ciex.edu.mx.app.EndPoints;
import ciex.edu.mx.app.MyApplication;
import ciex.edu.mx.model.User;
public class LoginActivity extends AppCompatActivity {
    private EditText inputMatricula, inputPassword;
    private TextInputLayout inputLayoutMatricula, inputLayoutPassword;
    private Button btnEnter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Check for login session. It user is already logged in
         * redirect him to main activity4
         * */
        if (MyApplication.getInstance().getPrefManager().getUser() != null) {
            // start main activity
            startActivity(new Intent(getApplicationContext(), BookActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inputLayoutMatricula = (TextInputLayout) findViewById(R.id.input_layout_matricula);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_matricula);
        inputMatricula = (EditText) findViewById(R.id.input_name);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnEnter = (Button) findViewById(R.id.btn_enter);

        inputMatricula.addTextChangedListener(new MyTextWatcher(inputMatricula));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateName()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }
                login(inputMatricula.getText().toString(), inputPassword.getText().toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        this.menu = menu;

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_about:
                //MyApplication.getInstance().logout();//logout and show Login
                break;


        }
        return super.onOptionsItemSelected(menuItem);
    }
    /**
     * logging in user. Will make http post request with matricula, password
     * as parameters
     */
    private void login(final String matricula, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(this.getString(R.string.label_msg_login));
        progressDialog.show();


        //final String matricula = inputMatricula.getText().toString();
        //final String password = inputPassword.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (!obj.getBoolean("error")) {
                        // user successfully logged in

                        JSONArray objcour_id=obj.getJSONArray("course_id");
                        JSONArray objcour_level=obj.getJSONArray("level_id");
                        String[] course_id=new String [objcour_id.length()];
                        String[] course_level=new String [objcour_level.length()];
                           for(int i=0; i<1; i++){
                               course_id[i]=objcour_id.getString(i);
                               course_level[i]=objcour_level.getString(i);
                           }
                        User user = new User(
                                obj.getString("student_id"),
                                obj.getString("student_name"),
                                obj.getString("matricula"),
                                obj.getString("device_key"),
                                obj.getString("created_at"),
                                course_id,
                                course_level
                        );

                        // storing user in shared preferences
                        MyApplication.getInstance().getPrefManager().storeUser(user);

                        // start main activity
                        startActivity(new Intent(getApplicationContext(), BookActivity.class));
                        finish();

                    } else {
                        // login error - simply toast the message
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login failed. Incorrect credentials", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "catch_error", Toast.LENGTH_SHORT).show();
                    Log.e("aqui",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Conection failed. Please try in a while", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("matricula", matricula);
                params.put("password", password);
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.
                    SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating name
    private boolean validateName() {
        if (inputMatricula.getText().toString().trim().isEmpty()) {
            inputLayoutMatricula.setError(getString(R.string.err_msg_name));
            requestFocus(inputMatricula);
            return false;
        } else {
            inputLayoutMatricula.setErrorEnabled(false);
        }

        return true;
    }

    // Validating password
    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}