package in.andonsystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import in.andonsystem.Constants;
import in.andonsystem.R;
import in.andonsystem.activity.v2.HomeActivity;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    private EditText username;
    private EditText password;
    private ProgressBar progress;

    private RestUtility restUtility;
    private ErrorListener errorListener;
    private SharedPreferences userPref;
    private SharedPreferences syncPref;
    private SharedPreferences appPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "056dd13f");
        setContentView(R.layout.activity_login);
        Log.d(TAG,"onCreate()");

        userPref = getSharedPreferences(Constants.USER_PREF,0);
        syncPref = getSharedPreferences(Constants.SYNC_PREF,0);
        appPref = getSharedPreferences(Constants.APP_PREF,0);
        username = (EditText) findViewById(R.id.userId);
        password = (EditText) findViewById(R.id.password);
        progress = (ProgressBar) findViewById(R.id.loading_progress);
        restUtility = new RestUtility(this){
            @Override
            protected void handleInternetConnRetry() {
                onStart();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void signIn(View v){
        final String email = username.getText().toString();
        String passwd = password.getText().toString();

        if(TextUtils.isEmpty(passwd) || TextUtils.isEmpty(email)){
            Toast.makeText(this,"Username or password cannot be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        progress.setVisibility(View.VISIBLE);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,response.toString());
                try {
                    userPref.edit()
                            .putString(Constants.USER_ACCESS_TOKEN, response.getString("access_token"))
                            .putString(Constants.USER_REFRESH_TOKEN, response.getString("refresh_token"))
                            .putBoolean(Constants.IS_LOGGED_IN,true)
                            .commit();
                    getUserDetails(email);
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        };
        errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                showMessage("Client not authorized. Contact developer.");
            }
            @Override
            protected void handleBadRequest() {
                showMessage("Incorrect credentials. Try again");
            }

            @Override
            protected void onError(VolleyError error) {
                progress.setVisibility(View.INVISIBLE);
            }
        };

        String url = Constants.AUTH_BASE_URL + "?grant_type=password&username=" + email + "&password=" + passwd;
        restUtility.setIsloginRequest(true);
        restUtility.setProtected(true);
        restUtility.post(url, null, listener, errorListener);
    }

    private void getUserDetails(final String email) {
        String url = Constants.API2_BASE_URL + "/users/search/byEmail?email=" + email;
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.setVisibility(View.INVISIBLE);
                try {
                    Long id = response.getLong("id");
                    String username = response.getString("name");
                    String role = response.getString("role");
                    String userType = response.getString("userType");
                    String level = response.getString("level");
                    userPref.edit()
                            .putLong(Constants.USER_ID,id)
                            .putString(Constants.USER_EMAIL, email)
                            .putString(Constants.USER_NAME, username)
                            .putString(Constants.USER_ROLE, role)
                            .putString(Constants.USER_TYPE, userType)
                            .putString(Constants.USER_LEVEL, level)
                            .commit();
                    if (userType.equalsIgnoreCase(Constants.USER_FACTORY)) {
                        redirectToHome(1);
                    }else {
                        redirectToHome(2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                showMessage("Session Expired. Login again.");
            }

            @Override
            protected void onError(VolleyError error) {
                progress.setVisibility(View.INVISIBLE);
            }
        };
        restUtility.setIsloginRequest(false);
        restUtility.setProtected(true);
        restUtility.get(url, listener, errorListener);
    }

    public void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void redirectToHome(int app) {
        if (app == 1) {
            Intent intent = new Intent(this, in.andonsystem.activity.v1.HomeActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    public void forgotPassword(View view){
        Intent i = new Intent(this, ForgotPasswordActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG,"Back Pressed");
        //AppClose.close();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        progress.setVisibility(View.INVISIBLE);
    }
}
