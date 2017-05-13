package in.andonsystem.v2.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

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

import in.andonsystem.AppController;
import in.andonsystem.v2.authenticator.AuthConstants;

/**
 * Created by razamd on 4/2/2017.
 */

public class LoginUtil {
    private static final String TAG = LoginUtil.class.getSimpleName();

    private static Boolean reqProgress;
    private static Boolean invalidateProgress;
    private static AccountManager mAccountManager;
    private static String authToken;
    private static SharedPreferences userPref;

    public static Bundle authenticate(String username, String password){
        Log.d(TAG,"authenticate()");
        String url = Constants.AUTH_BASE_URL + "?grant_type=password&username=" + username + "&password=" + password;
        return login(url);
    }

    public static Bundle authenticateWithRefreshToken(final String refreshToken){
        Log.d(TAG,"authenticateWithRefreshToken()");
        String url = Constants.AUTH_BASE_URL + "?grant_type=refresh_token&refresh_token=" + refreshToken;
        return login(url);
    }

    private static Bundle login(String url){
        Log.d(TAG,"login()");
        Log.d(TAG,"loginUrl: " + url);
        reqProgress = true;
        final Bundle result = new Bundle();
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,response.toString());
                try {
                    result.putString(AuthConstants.ARG_ACCESS_TOKEN, response.getString("access_token"));
                    result.putString(AuthConstants.ARG_REFRESH_TOKEN, response.getString("refresh_token"));
                }catch (JSONException e){
                    e.printStackTrace();
                }

                reqProgress = false;
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse resp = error.networkResponse;
                String data = new String(resp.data);
                Log.i(TAG, "response status: " + data);
                Log.i(TAG, "response status: " + resp.statusCode);
                if(resp.statusCode == 400){
                    result.putString(AuthConstants.ARG_AUTHENTICATION_ERROR,"Incorrect credentials. Try again");
                }
                if(resp.statusCode == 401){
                    result.putString(AuthConstants.ARG_AUTHENTICATION_ERROR,"Client is not authorized.");
                }
                reqProgress = false;
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


        while (reqProgress){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "result: " + result);

        return result;
    }

//    public static String getNewToken(Context context){
//        mAccountManager = AccountManager.get(context);
//        userPref = context.getSharedPreferences(Constants.USER_PREF,0);
//        invalidateProgress = true;
//
//        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN,null);
//        mAccountManager.invalidateAuthToken(AuthConstants.VALUE_ACCOUNT_TYPE,accessToken);
//
//        Account[] accounts = mAccountManager.getAccounts();
//        String email = userPref.getString(Constants.USER_EMAIL, null);
//        Account account = null;
//        if(email != null){
//            for (Account a: accounts){
//                if(a.name.equals(email)){
//                    account = a;
//                    break;
//                }
//            }
//        }else{
//            Log.i(TAG, "email not saved in userPref");
//            return null;
//        }
//
//        final AccountManagerFuture<Bundle> future2 = mAccountManager.getAuthToken(account, AuthConstants.AUTH_TOKEN_TYPE_FULL_ACCESS, null, context, null, null);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Bundle bnd = future2.getResult();
//                    authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                    userPref.edit().putString(Constants.USER_ACCESS_TOKEN,authToken).commit();
//                    invalidateProgress = false;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e(TAG,e.getMessage());
//                    //showMessage(e.getMessage());
//                }
//            }
//        }).start();
//
//        while (invalidateProgress){
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return authToken;
//    }

}
