package in.andonsystem.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import in.andonsystem.Constants;

/**
 * Created by mdzahidraza on 18/06/17.
 */

public abstract class ErrorListener implements Response.ErrorListener {

    private final String TAG = ErrorListener.class.getSimpleName();

    private final Context mContext;

    public ErrorListener(Context context){
        this.mContext = context;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error instanceof TimeoutError) {
            showMessage("Slow internet connection.");
        }else {
            NetworkResponse resp = error.networkResponse;
            if (resp != null) {
                if (resp.statusCode == 401) {
                    SharedPreferences userPref = mContext.getSharedPreferences(Constants.USER_PREF,0);
                    userPref.edit().putString(Constants.USER_ACCESS_TOKEN,null).commit();
                    handleTokenExpiry();
                }else if (resp.statusCode == 400) {
                    handleBadRequest();
                }else if (resp.statusCode == 404) {
                    showMessage("Resource not found.Contact developer.");
                }
            }
            onError(error);
        }
    }

    /**
     * User should override this method to handle complex errors
     * @param error
     */
    protected void onError(VolleyError error) {}

    /**
     * Handle token exipiry. Simply perform the action again which user was trying to perform
     */
    protected abstract void handleTokenExpiry();

    protected void handleBadRequest(){}

    private void showMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
