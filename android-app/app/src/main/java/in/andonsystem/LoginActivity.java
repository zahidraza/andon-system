package in.andonsystem;

import android.accounts.AccountAuthenticatorActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.andonsystem.activity.v2.HomeActivity;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;
import in.andonsystem.activity.ForgotPasswordActivity;

public class LoginActivity extends AccountAuthenticatorActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    private EditText username;
    private EditText password;

    private RestUtility restUtility;
    private ErrorListener errorListener;
    private SharedPreferences userPref;
    private SharedPreferences syncPref;
    private SharedPreferences appPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        Log.d(TAG,"onCreate()");
        AppClose.activity2 = this;

        userPref = getSharedPreferences(Constants.USER_PREF,0);
        syncPref = getSharedPreferences(Constants.SYNC_PREF,0);
        appPref = getSharedPreferences(Constants.APP_PREF,0);
        username = (EditText) findViewById(R.id.userId);
        password = (EditText) findViewById(R.id.password);

        restUtility = new RestUtility(this){
            @Override
            protected void handleInternetConnRetry() {
                onStart();
            }

            @Override
            protected void handleInternetConnExit() {
                AppClose.close();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check App update and re-initialization
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,"config response: " + response.toString());
                try {
                    boolean update = response.getBoolean("update");
                    long appSync = response.getLong("appSync");
                    if (update) {
                        getDialog().show();
                    }else {
                        long lastAppSync = syncPref.getLong(Constants.LAST_APP_SYNC,0);
                        if (lastAppSync < appSync) {
                            appPref.edit()
                                        .putBoolean(Constants.APP1_FIRST_LAUNCH,true)
                                        .putBoolean(Constants.APP2_FIRST_LAUNCH,true)
                                        .putLong(Constants.LAST_APP_SYNC, appSync)
                                    .commit();
                        }
                        login();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {

            }
        };
        String url = Constants.API2_BASE_URL + "/misc/config?version=" + getString(R.string.version2);
        restUtility.get(url,listener,errorListener);

    }

    private AlertDialog getDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available");
        builder.setMessage("A new version of application is available.Please update for app to work properly.");
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                appPref.edit()
                        .putBoolean(Constants.APP1_FIRST_LAUNCH,true)
                        .putBoolean(Constants.APP2_FIRST_LAUNCH,true)
                        .commit();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://andonsystem.in/download.jsp"));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                login();
            }
        });
        return builder.create();
    }


    private void login() {
        boolean isLoggedIn = userPref.getBoolean(Constants.IS_LOGGED_IN, false);
        if (isLoggedIn) {
            String userType = userPref.getString(Constants.USER_TYPE,"");
            if (userType.equalsIgnoreCase(Constants.USER_FACTORY)) {
                redirectToHome(1);
            }else {
                redirectToHome(2);
            }
        }
    }

    public void signIn(View v){
        final String email = username.getText().toString();
        String passwd = password.getText().toString();

        if(TextUtils.isEmpty(passwd) || TextUtils.isEmpty(email)){
            Toast.makeText(this,"Username or password cannot be empty",Toast.LENGTH_SHORT).show();
            return;
        }

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
        };

        String url = Constants.AUTH_BASE_URL + "?grant_type=password&username=" + email + "&password=" + passwd;
        restUtility.setIsloginRequest(true);
        restUtility.post(url, null, listener, errorListener);
    }

    private void getUserDetails(final String email) {
        String url = Constants.API2_BASE_URL + "/users/search/byEmail?email=" + email;
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
        };
        restUtility.setIsloginRequest(false);
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
        AppClose.close();
    }
}
