package in.andonsystem;

import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.andonsystem.v2.activity.HomeActivity;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.entity.UserBuyer;
import in.andonsystem.v2.service.BuyerService;
import in.andonsystem.v2.service.IssueService;
import in.andonsystem.v2.service.UserBuyerService;
import in.andonsystem.v2.service.UserService;
import in.andonsystem.v2.util.Constants;
import in.andonsystem.v2.util.MiscUtil;

public class LoadingActivity extends AppCompatActivity {
    private final String TAG = LoadingActivity.class.getSimpleName();

    private Context mContext;
    private App app;
    private BuyerService buyerService;
    private UserService userService;
    private UserBuyerService userBuyerService;
    private IssueService issueService;
    private ProgressBar progress;
    private SharedPreferences appPref;
    private SharedPreferences userPref;
    private SharedPreferences syncPref;
    private int noOfRequest = 0;
    private boolean firstLaunch = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_loading);
        Log.d(TAG, "onCreate()");
        AppClose.activity1 = this;

        mContext = this;
        app = (App) getApplication();
        buyerService = new BuyerService(app);
        userService = new UserService(app);
        issueService = new IssueService(app);
        userBuyerService = new UserBuyerService(app);
        progress = (ProgressBar) findViewById(R.id.loading_progress);
        appPref = getSharedPreferences(Constants.APP_PREF, 0);
        userPref = getSharedPreferences(Constants.USER_PREF, 0);
        syncPref = getSharedPreferences(Constants.SYNC_PREF, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        AlertDialog dialog = getAlertDialog();
        Boolean isConnected = MiscUtil.isConnectedToInternet(this);

        if (!isConnected) {
            dialog.show();
        } else {
            progress.setVisibility(View.VISIBLE);

            firstLaunch = appPref.getBoolean(Constants.FIRST_LAUNCH, true);
            Log.i(TAG, "first launch: " + firstLaunch);
            if (firstLaunch) {
                init();
            } else {
                //Delete older issue
                new IssueService(app).deleteAllOlder();

                String url = Constants.API2_BASE_URL + "/misc/config?version=" + getString(R.string.version2);
                Log.i(TAG, "config url: " + url);
                Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Config Response :" + response.toString());

                        try {
                            if (response.getBoolean("update")) {
                                Log.d(TAG, "Update application");
                                progress.setVisibility(View.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("Update Available");
                                builder.setMessage("A new version of application is available.Please update for app to work properly.");
                                builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        appPref.edit().putBoolean(Constants.FIRST_LAUNCH,true).commit();
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://andonsystem.in/download.jsp"));
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        syncUsers();
                                        goToHomeAfterInit();
                                    }
                                });
                                builder.create().show();
                            }

                            if (!response.getBoolean("update")) {
                                if (response.getBoolean("initialize")) {
                                    firstLaunch = true;
                                    init();
                                }else {
                                    syncUsers();
                                    goToHomeAfterInit();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e(TAG, error.getMessage());
                    }
                };

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
                request.setTag(TAG);
                AppController.getInstance().addToRequestQueue(request);
            }
        }
    }

    private void init() {
        AppController appController = AppController.getInstance();
        buyerService.deleteAll();
        userService.deleteAll();
        issueService.deleteAll();
        userBuyerService.deleteAll();
        syncPref.edit()
                    .putLong(Constants.LAST_USER_SYNC,0L)
                    .putLong(Constants.LAST_ISSUE2_SYNC,0L)
                .commit();

        /////////////////////////// get Buyers ////////////////////////
        String url1 = Constants.API2_BASE_URL + "/buyers";
        Response.Listener<JSONArray> listener1 = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG, "buyers Response :" + response.toString());
                try {
                    List<Buyer> buyers = new ArrayList<>();
                    JSONObject obj;
                    for (int i = 0; i < response.length(); i++) {
                        obj = response.getJSONObject(i);
                        buyers.add(new Buyer(obj.getLong("id"), obj.getString("name"), obj.getString("team")));
                    }

                    buyerService.saveAll(buyers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                goToHomeAfterInit();
                syncUsers();
            }
        };
        Response.ErrorListener errorListener1 = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getMessage());
            }
        };
        JsonArrayRequest request1 = new JsonArrayRequest(Request.Method.GET, url1, null, listener1, errorListener1);
        request1.setTag(TAG);
        appController.addToRequestQueue(request1);

        /////////////////////////////// get problems ////////////////////////////
        String url2 = Constants.API2_BASE_URL + "/problems";
        Response.Listener<JSONArray> listener2 = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG, "problem Response :" + response.toString());

                String[] problems = new String[response.length() + 1];
                problems[0] = "Select Problem";
                try {
                    for (int i = 0; i < response.length(); i++) {
                        problems[i + 1] = response.getString(i);
                    }
                    StringBuilder builder = new StringBuilder();
                    for (String p : problems) {
                        builder.append(p + ";");
                    }
                    builder.setLength(builder.length() - 1);
                    appPref.edit().putString(Constants.APP_PROBLEMS, builder.toString()).commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goToHomeAfterInit();
            }
        };
        Response.ErrorListener errorListener2 = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getStackTrace().toString());
            }
        };
        JsonArrayRequest request2 = new JsonArrayRequest(Request.Method.GET, url2, null, listener2, errorListener2);
        request2.setTag(TAG);
        appController.addToRequestQueue(request2);

        //get Teams
        String url3 = Constants.API2_BASE_URL + "/teams";
        Response.Listener<JSONArray> listener3 = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG, "teams Response :" + response.toString());

                String[] teams = new String[response.length()];
                try {
                    for (int i = 0; i < response.length(); i++) {
                        teams[i] = response.getString(i);
                    }
                    StringBuilder builder = new StringBuilder();
                    for (String t : teams) {
                        builder.append(t + ";");
                    }
                    builder.setLength(builder.length() - 1);
                    appPref.edit().putString(Constants.APP_TEAMS, builder.toString()).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goToHomeAfterInit();
            }
        };
        Response.ErrorListener errorListener3 = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getMessage());
            }
        };
        JsonArrayRequest request3 = new JsonArrayRequest(Request.Method.GET, url3, null, listener3, errorListener3);
        request3.setTag(TAG);
        appController.addToRequestQueue(request3);
    }

    public void goToHomeAfterInit() {
        Log.i(TAG, "goToHomeAfterInit()");
        noOfRequest++;
        if (firstLaunch && noOfRequest == 4) {
            Log.i(TAG, "setting first launch to false");
            appPref.edit().putBoolean(Constants.FIRST_LAUNCH, false).commit();
            progress.setVisibility(View.GONE);
            goToHome();
        }
        if (!firstLaunch && noOfRequest == 2){
            progress.setVisibility(View.GONE);
            goToHome();
        }
    }

    public void goToHome() {
        Intent i  = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet");
        builder.setMessage("No Internet Connection Available.Do you want to try again?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onStart();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        return builder.create();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppClose.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        Log.i(TAG,"finish()");
    }

    private void syncUsers(){
        Log.d(TAG,"syncUsers()");
        final Long lastSync = syncPref.getLong(Constants.LAST_USER_SYNC,0L);
        String url4 = Constants.API2_BASE_URL + "/users?after=" + lastSync;
        Response.Listener<JSONObject> listener4 = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "users Response :" + response.toString());
                try {
                    JSONArray jsonUsers = response.getJSONArray("users");
                    Long userSync = response.getLong("userSync");
                    List<User> users = new ArrayList<>();
                    Long userId;
                    JSONObject u, b;
                    JSONArray buyers;
                    List<UserBuyer> userBuyerList;
                    for (int i = 0; i < jsonUsers.length(); i++) {

                        u = jsonUsers.getJSONObject(i);
                        userId = u.getLong("id");
                        if (userService.exists(userId)) {
                            userBuyerService.deleteByUser(userId);
                        }
                        userService.saveOrUpdate(new User(
                                userId,
                                u.getString("name"),
                                u.getString("email"),
                                u.getString("mobile"),
                                u.getString("role"),
                                u.getString("userType"),
                                u.getString("level")
                        ));
                        buyers = u.getJSONArray("buyers");
                        if (buyers.length() > 0) {
                            userBuyerList = new ArrayList<>();
                            for (int j = 0; j < buyers.length(); j++){
                                b = buyers.getJSONObject(j);
                                userBuyerList.add(new UserBuyer(null,u.getLong("id"), b.getLong("id")));
                            }
                            userBuyerService.saveBatch(userBuyerList);
                        }
                    }
                    syncPref.edit().putLong(Constants.LAST_USER_SYNC, userSync).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                goToHomeAfterInit();
            }
        };
        Response.ErrorListener errorListener4 = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getMessage());
            }
        };
        JsonObjectRequest request4 = new JsonObjectRequest(Request.Method.GET, url4, null, listener4, errorListener4);
        request4.setTag(TAG);
        AppController.getInstance().addToRequestQueue(request4);
    }

}
