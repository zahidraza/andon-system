package in.andonsystem.v2.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import in.andonsystem.AppController;
import in.andonsystem.v2.util.Constants;
import in.andonsystem.v2.util.LoginUtil;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Created by razamd on 4/1/2017.
 */

public class Authenticator extends AbstractAccountAuthenticator {

    private final String TAG = Authenticator.class.getSimpleName();

    private Context mContext;

    public Authenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.i(TAG,"addAccount");
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthConstants.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthConstants.ARG_AUTH_TOKEN_TYPE, authTokenType);
        intent.putExtra(AuthConstants.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        Log.i(TAG,"confirmCredentials");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.i(TAG,"getAuthToken");

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AuthConstants.AUTH_TOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        Log.d("udinic", TAG + "> peekAuthToken returned - " + authToken);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String refreshToken = am.getUserData(account,AuthConstants.ARG_REFRESH_TOKEN);
            Log.i(TAG, "refresh_token = " + refreshToken);
            if (refreshToken != null) {
                try {
                    Log.d("udinic", TAG + "> re-authenticating with the existing refresh_token");
                    Bundle result = LoginUtil.authenticateWithRefreshToken(refreshToken);
                    authToken = result.getString(AuthConstants.ARG_ACCESS_TOKEN);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthConstants.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthConstants.ARG_AUTH_TOKEN_TYPE, authTokenType);
        intent.putExtra(AuthConstants.ARG_ACCOUNT_USERNAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.i(TAG,"getAuthTokenLabel");
        return "Full Access";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.i(TAG,"updateCredentials");
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        Log.i(TAG,"hasFeatures");
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    private String login(AccountManager am,Account account,final String username,final String password){
        Log.i(TAG,"login");
        String url = Constants.AUTH_BASE_URL + "?grant_type=password&username=zahid7292@gmail.com&password=8987525008";

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,null,future,future){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Basic " + Base64.encode("client:secret".getBytes(),0));
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("grant_type", "password");
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

        JSONObject response = null;
        try {
            response = future.get(30, TimeUnit.SECONDS);
            Log.i(TAG,response.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Log.e(TAG,"Check your internet connection");
        }
        String accessToken = null;

        if(response != null){
            try {
                accessToken = response.getString("access_token");
                String refreshToken = response.getString("refresh_token");

                am.setUserData(account,"REFRESH_TOKEN", refreshToken);
                am.setAuthToken(account,AuthConstants.AUTH_TOKEN_TYPE_FULL_ACCESS,accessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return accessToken;
    }
}
