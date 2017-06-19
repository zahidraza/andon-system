package in.andonsystem.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import in.andonsystem.AppController;
import in.andonsystem.Constants;
import in.andonsystem.LoginActivity;

/**
 * Created by mdzahidraza on 18/06/17.
 */

public class RestUtility {

    private final String TAG = RestUtility.class.getSimpleName();

    private static final AppController appController = AppController.getInstance();
    protected final Context mContext;
    private SharedPreferences userPref;

    public RestUtility(Context context) {
        this.mContext = context;
        userPref = context.getSharedPreferences(Constants.USER_PREF,0);
    }

    public void get(String url, Response.Listener<JSONObject> listener, ErrorListener errorListener) {
        Log.d(TAG, "RestUtility: get url = " + url);
        send(Request.Method.GET,url,null,listener,errorListener);
    }

    public void post(String url, JSONObject data, Response.Listener<JSONObject> listener, ErrorListener errorListener) {
        Log.d(TAG, "RestUtility: post url = " + url);
        send(Request.Method.POST,url,data,listener,errorListener);
    }

    public void patch(String url, JSONObject data, Response.Listener<JSONObject> listener, ErrorListener errorListener) {
        Log.d(TAG, "RestUtility: patch url = " + url);
        send(Request.Method.PATCH,url,data,listener,errorListener);
    }

    public void put(String url, JSONObject data, Response.Listener<JSONObject> listener, ErrorListener errorListener) {
        Log.d(TAG, "RestUtility: put url = " + url);
        send(Request.Method.PUT,url,data,listener,errorListener);
    }

    public void getJsonArray(String url, Response.Listener<JSONArray> listener, ErrorListener errorListener) {
        Log.d(TAG, "RestUtility: get url = " + url);
        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN,null);

        MyJsonArrayRequest request = new MyJsonArrayRequest(Request.Method.GET, url, null,listener,errorListener);
        request.setRetryPolicy( new DefaultRetryPolicy(20*1000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("");  //TODO:

        if (accessToken != null) {
            request.setAccessToken(accessToken);
            appController.addToRequestQueue(request);
        }else {
            redirectToLogin();
//            String refreshToken = userPref.getString(Constants.USER_REFRESH_TOKEN, null);
//            if (refreshToken != null) {
//                accessToken = getAccessToken(refreshToken);
//                if (accessToken != null) {
//                    request.setAccessToken(accessToken);
//                    appController.addToRequestQueue(request);
//                }else {
//                    redirectToLogin();
//                }
//            }else {
//                redirectToLogin();
//            }
        }

    }

    private void send(int method, String url, JSONObject data, Response.Listener<JSONObject> listener, ErrorListener errorListener){
        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN,null);

        MyJsonObjectRequest request = new MyJsonObjectRequest(method, url, data,listener,errorListener);
        request.setRetryPolicy( new DefaultRetryPolicy(20*1000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("");  //TODO:

        if (accessToken != null) {
            request.setAccessToken(accessToken);
            appController.addToRequestQueue(request);
        }else {
            redirectToLogin();
//            String refreshToken = userPref.getString(Constants.USER_REFRESH_TOKEN, null);
//            if (refreshToken != null) {
//                accessToken = getAccessToken(refreshToken);
//                if (accessToken != null) {
//                    request.setAccessToken(accessToken);
//                    appController.addToRequestQueue(request);
//                }else {
//                    redirectToLogin();
//                }
//            }else {
//                redirectToLogin();
//            }
        }
    }

    private void redirectToLogin() {
        Log.d(TAG, "RestUtility: redirectToLogin");
        userPref.edit().putBoolean(Constants.IS_LOGGED_IN,false).commit();
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    public String getAccessToken(String refreshToken) {
        Log.d(TAG, "RestUtility: getAccessToken");
        String url = Constants.AUTH_BASE_URL + "?grant_type=refresh_token&refresh_token=" + refreshToken;
        Log.d(TAG,"url = " + url);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,null,future,future){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Basic " + Base64.encodeToString("client-web:super-secret".getBytes(),0));
                params.put("Accept", "application/json; charset=utf-8");
                return params;
            }
        };
        appController.addToRequestQueue(request);


        try {
            JSONObject resp =  future.get(15, TimeUnit.SECONDS);
            Log.d(TAG, "$$$ refresh token response: " + resp.toString());

        } catch (InterruptedException e) {
            Log.e(TAG,"Retrieve cards api call interrupted.", e);
//            errorListener.onErrorResponse(new VolleyError(e));
        } catch (ExecutionException e) {
            Log.e(TAG,"Retrieve cards api call failed.", e);
//            errorListener.onErrorResponse(new VolleyError(e));
        } catch (TimeoutException e) {
            Log.e(TAG,"Retrieve cards api call timed out.", e);
//            errorListener.onErrorResponse(new VolleyError(e));
        }
        return null;
    }
}
