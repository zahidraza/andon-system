package in.andonsystem;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;
import in.andonsystem.v2.activity.ForgotPasswordActivity;
import in.andonsystem.v2.activity.HomeActivity;

public class LoginActivity extends AccountAuthenticatorActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    private EditText username;
    private EditText password;

    private String mAccountUsername;
    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        Log.d(TAG,"onCreate()");
        AppClose.activity2 = this;

        userPref = getSharedPreferences(Constants.USER_PREF,0);

        username = (EditText) findViewById(R.id.userId);
        password = (EditText) findViewById(R.id.password);

    }

    @Override
    protected void onStart() {
        super.onStart();
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

        String url = Constants.AUTH_BASE_URL + "?grant_type=password&username=" + email + "&password=" + passwd;

        final Bundle result = new Bundle();

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
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,error.toString());
                NetworkResponse resp = error.networkResponse;
                //Log.i(TAG, "response status: " + resp.statusCode);
                if(resp != null && resp.statusCode == 400){
                    showMessage("Incorrect credentials. Try again");
                }
                else if(resp != null && resp.statusCode == 401){
                    showMessage("Client not authorized");
                }
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,listener,errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Basic " + Base64.encodeToString("client-android:super-secret".getBytes(),0));
                params.put("Accept", "application/json; charset=utf-8");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
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

        ErrorListener errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                showMessage("Session Expired");
            }
        };
        RestUtility restUtility = new RestUtility(this);
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
