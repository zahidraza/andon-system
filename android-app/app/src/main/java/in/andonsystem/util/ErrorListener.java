package in.andonsystem.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
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
        //Do general error handling
        NetworkResponse resp = error.networkResponse;
        if (resp != null) {
            if (resp.statusCode == 401) {
                SharedPreferences userPref = mContext.getSharedPreferences(Constants.USER_PREF,0);
                userPref.edit().putString(Constants.USER_ACCESS_TOKEN,null).commit();
                handleTokenExpiry();
            }else if (resp.statusCode == 400) {
                Log.d(TAG,"resp: " + new String(resp.data));
                showMessage("Some error occured. inform developer.");
            }
        }
        onError(error);
    }

    protected void onError(VolleyError error) {
        //let caller override this method for specific error handling
    }

    /**
     * Handle token exipiry. Simply perform the action again which user was trying to perform
     */
    protected abstract void handleTokenExpiry();

    private void showMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
