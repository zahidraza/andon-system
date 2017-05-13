package in.andonsystem.v2.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
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

import in.andonsystem.AppClose;
import in.andonsystem.AppController;
import in.andonsystem.R;
import in.andonsystem.v2.activity.ForgotPasswordActivity;
import in.andonsystem.v2.util.Constants;
import in.andonsystem.v2.util.LoginUtil;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    private final String TAG = AuthenticatorActivity.class.getSimpleName();

    private EditText username;
    private EditText password;

    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String mAccountUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        Log.d(TAG,"onCreate()");
        AppClose.activity2 = this;

        username = (EditText) findViewById(R.id.userId);
        password = (EditText) findViewById(R.id.password);

        mAccountManager = AccountManager.get(this);

        final Intent intent = getIntent();
        mAccountUsername = intent.getStringExtra(AuthConstants.ARG_ACCOUNT_USERNAME);
        mAuthTokenType = intent.getStringExtra(AuthConstants.ARG_AUTH_TOKEN_TYPE);

        if(mAuthTokenType == null){
            mAuthTokenType = AuthConstants.AUTH_TOKEN_TYPE_FULL_ACCESS;
        }

        if(mAccountUsername != null){
            username.setText(mAccountUsername);
        }

    }

    public void signIn(View v){
        mAccountUsername = username.getText().toString();
        String passwd = password.getText().toString();

        if(TextUtils.isEmpty(passwd) || TextUtils.isEmpty(mAccountUsername)){
            Toast.makeText(this,"Username or password cannot be empty",Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Constants.AUTH_BASE_URL + "?grant_type=password&username=" + mAccountUsername + "&password=" + passwd;

        final Bundle result = new Bundle();

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,response.toString());
                try {
                    result.putString(AuthConstants.ARG_ACCESS_TOKEN, response.getString("access_token"));
                    result.putString(AuthConstants.ARG_REFRESH_TOKEN, response.getString("refresh_token"));
                    finishLogin(result);
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
                Log.i(TAG, "response status: " + resp.statusCode);
                if(resp != null && resp.statusCode == 400){
                    result.putString(AuthConstants.ARG_AUTHENTICATION_ERROR,"Incorrect credentials. Try again");
                }
                if(resp != null && resp.statusCode == 401){
                    result.putString(AuthConstants.ARG_AUTHENTICATION_ERROR,"Client is not authorized.");
                }
                finishLogin(result);
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

    private void finishLogin(Bundle bnd){
        mAccountUsername = username.getText().toString();
        String accountType = getIntent().getStringExtra(AuthConstants.ARG_ACCOUNT_TYPE);

        if (bnd.getString(AuthConstants.ARG_AUTHENTICATION_ERROR) != null){
            Toast.makeText(this,bnd.getString(AuthConstants.ARG_AUTHENTICATION_ERROR),Toast.LENGTH_LONG).show();
            return;
        }

        String authToken = bnd.getString(AuthConstants.ARG_ACCESS_TOKEN);
        String refreshToken = bnd.getString(AuthConstants.ARG_REFRESH_TOKEN);
        Log.i(TAG,"refreshToken = " + refreshToken);

        final Account account = new Account(mAccountUsername, AuthConstants.VALUE_ACCOUNT_TYPE);

        if(getIntent().getBooleanExtra(AuthConstants.ARG_IS_ADDING_NEW_ACCOUNT, false)){
            mAccountManager.addAccountExplicitly(account,null,null);
            mAccountManager.setAuthToken(account,AuthConstants.AUTH_TOKEN_TYPE_FULL_ACCESS,authToken);
        }
        mAccountManager.setUserData(account,AuthConstants.ARG_REFRESH_TOKEN, bnd.getString(AuthConstants.ARG_REFRESH_TOKEN));

        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, mAccountUsername);
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        data.putString(AccountManager.KEY_AUTHTOKEN, authToken);

        Intent intent = new Intent();
        intent.putExtras(data);

        setAccountAuthenticatorResult(data);
        finish();

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
