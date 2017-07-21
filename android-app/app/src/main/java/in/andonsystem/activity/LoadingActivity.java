package in.andonsystem.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

public class LoadingActivity extends AppCompatActivity {
    private final String TAG = LoadingActivity.class.getSimpleName();

    private ProgressBar progress;
    private SharedPreferences appPref;
    private SharedPreferences syncPref;
    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "056dd13f");
        setContentView(R.layout.activity_loading);
        Log.d(TAG, "onCreate()");

        appPref = getSharedPreferences(Constants.APP_PREF,0);
        syncPref = getSharedPreferences(Constants.SYNC_PREF,0);
        userPref = getSharedPreferences(Constants.USER_PREF,0);
        progress = (ProgressBar) findViewById(R.id.loading_progress);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        checkAppUpdate();
    }

    private void checkAppUpdate(){
        progress.setVisibility(View.VISIBLE);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,"config response: " + response.toString());
                progress.setVisibility(View.INVISIBLE);
                try {
                    boolean update = response.getBoolean("update");
                    long appSync = response.getLong("appSync");
                    if (update) {
                        getDialog().show();
                    }else {
                        long lastAppSync = syncPref.getLong(Constants.LAST_APP_SYNC,0);
                        Log.d(TAG, "last app sync = " + lastAppSync + ", current value = " + appSync);
                        if (lastAppSync < appSync) {
                            Log.d(TAG,"Re-Initialize signal");
                            appPref.edit()
                                    .putBoolean(Constants.APP1_FIRST_LAUNCH,true)
                                    .putBoolean(Constants.APP2_FIRST_LAUNCH,true)
                                    .apply();
                            syncPref.edit().putLong(Constants.LAST_APP_SYNC,appSync).apply();
                        }
                        login();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ErrorListener errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {

            }

            @Override
            protected void onError(VolleyError error) {
                progress.setVisibility(View.INVISIBLE);
            }
        };
        String url = Constants.API2_BASE_URL + "/misc/config?version=" + getString(R.string.version2);

        RestUtility restUtility = new RestUtility(this) {
            @Override
            protected void handleInternetConnRetry() {
                onStart();
            }

            @Override
            protected void handleInternetConnExit() {
                finish();
            }
        };
        restUtility.setProtected(false);
        restUtility.setIsloginRequest(false);
        restUtility.get(url,listener,errorListener);
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
        }else {
            redirectToLogin();
        }
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

    private void redirectToLogin(){
        //progress.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://andonsystem.in/download"));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    @Override
    public void finish() {
        super.finish();
        Log.i(TAG,"finish()");
    }

}
