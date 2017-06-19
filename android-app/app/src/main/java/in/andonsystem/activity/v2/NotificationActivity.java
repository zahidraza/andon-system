package in.andonsystem.activity.v2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.TreeSet;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.activity.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.adapter.AdapterNotification;
import in.andonsystem.dto.Notification;
import in.andonsystem.entity.Issue2;
import in.andonsystem.entity.User;
import in.andonsystem.service.IssueService2;
import in.andonsystem.service.UserService;
import in.andonsystem.Constants;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;

public class NotificationActivity extends AppCompatActivity {

    private final String TAG = NotificationActivity.class.getSimpleName();

    private Context mContext;
    private App app;
    private UserService userService;
    private IssueService2 issueService2;
    private SharedPreferences userPref;

    private RelativeLayout container;
    private ProgressBar progress;
    private RecyclerView recyclerView;
    private TextView emptyMessage;

    private User user;
    private Long currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_notification2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppClose.activity3 = this;
        mContext = this;
        app = (App) getApplication();
        userService = new UserService(app);
        issueService2 = new IssueService2(app);
        userPref = getSharedPreferences(Constants.USER_PREF,0);
        user = userService.findByEmail(userPref.getString(Constants.USER_EMAIL, null));

        container = (RelativeLayout) findViewById(R.id.content_nfn2);
        progress = (ProgressBar) findViewById(R.id.loading_progress);
        prepareScreen();
        getCurrentTime();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentTime == null){
            return;
        }
        TreeSet<Notification> list = new TreeSet<>();
        String message;
        long timeAt;

        if (user.getUserType().equalsIgnoreCase(Constants.USER_MERCHANDISING)) {
            List<Issue2> issue2s = issueService2.findAllByBuyers(user.getBuyers());

            if (issue2s.size() > 0){
                for ( Issue2 issue2 : issue2s){
                    Log.d(TAG, "MERCHANDISING: issue2 = " + issue2.getProblem());
                    if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL3) && issue2.getProcessingAt() != 3){
                        continue;
                    }else if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL2) && issue2.getProcessingAt() < 2){
                        continue;
                    }
                    if(issue2.getFixAt() != null){
                        message = "Problem " + issue2.getProblem() + " of " + issue2.getBuyer().getTeam() + ":" + issue2.getBuyer().getName() + " was resolved.";
                        timeAt = currentTime - issue2.getFixAt().getTime();
                        list.add(new Notification(issue2.getId(),message,timeAt, 2));
                    }
                    else if(issue2.getAckAt() != null){
                        message = "Problem " +  issue2.getProblem() + " of " + issue2.getBuyer().getTeam() + ":" + issue2.getBuyer().getName() + " was acknowledged by "
                                + (issue2.getAckBy() == user.getId() ? "you" : issue2.getAckByUser().getName());
                        timeAt = currentTime - issue2.getAckAt().getTime();
                        list.add(new Notification(issue2.getId(),message,timeAt, 1));
                    }
                    else {
                        message = "Problem " +  issue2.getProblem() + " of " + issue2.getBuyer().getTeam() + ":" + issue2.getBuyer().getName() + " was raised by " + issue2.getRaisedByUser().getName();
                        timeAt = currentTime - issue2.getRaisedAt().getTime();
                        list.add(new Notification(issue2.getId(),message,timeAt, 0));
                    }
                }
            }
        }
        else if (user.getUserType().equalsIgnoreCase(Constants.USER_SAMPLING)) {
            List<Issue2> issue2s = issueService2.findAllByUser(user);

            if (issue2s.size() > 0) {
                for (Issue2 issue2 : issue2s) {
                    Log.d(TAG, "SAMPLING: issue2 = " + issue2.getProblem());
                    if (issue2.getFixAt() != null) {
                        message = issue2.getAckByUser().getName() + " fixed " + "problem " +  issue2.getProblem() + " of " + issue2.getBuyer().getTeam() + ":" + issue2.getBuyer().getName();
                        timeAt = currentTime - issue2.getAckAt().getTime();
                        list.add(new Notification(issue2.getId(),message,timeAt, 2));
                    }else if (issue2.getAckAt() != null) {
                        message = issue2.getAckByUser().getName() + " acknowledged " + "problem " +  issue2.getProblem() + " of " + issue2.getBuyer().getTeam() + ":" + issue2.getBuyer().getName();
                        timeAt = currentTime - issue2.getAckAt().getTime();
                        list.add(new Notification(issue2.getId(),message,timeAt, 1));
                    }else {
                        message = "Problem " +  issue2.getProblem() + " of " + issue2.getBuyer().getTeam() + ":" + issue2.getBuyer().getName() + " was raised by you. ";
                        timeAt = currentTime - issue2.getRaisedAt().getTime();
                        list.add(new Notification(issue2.getId(),message,timeAt, 0));
                    }
                }
            }

        }

        ///////////////////////////
        if (list.size() > 0){
            Log.d(TAG, "No of Notifications: = " + list.size());
            container.addView(recyclerView);
            AdapterNotification adapter = new AdapterNotification(mContext,list,2);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }else {
            container.addView(emptyMessage);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        container.removeAllViews();
    }

    private void prepareScreen(){
        recyclerView = new RecyclerView(this);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
        recyclerView.setLayoutParams(params);

        emptyMessage = new TextView(this);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params2.topMargin = 50;
        emptyMessage.setLayoutParams(params2);
        emptyMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyMessage.setTextColor(Color.parseColor("#00FF00"));
        emptyMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        emptyMessage.setText("No Notification available.");
    }

    private void getCurrentTime(){
        progress.setVisibility(View.VISIBLE);
        RestUtility restUtility = new RestUtility(this){
            @Override
            protected void handleInternetConnRetry() {
                onStart();
            }

            @Override
            protected void handleInternetConnExit() {
                AppClose.close();
            }
        };
        String url = Constants.API2_BASE_URL + "/misc/current_time";

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Response :" + response.toString());
                try {
                    currentTime = response.getLong("currentTime");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.setVisibility(View.INVISIBLE);
                onStart();
            }
        };
        ErrorListener errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            protected void onError(VolleyError error) {
                progress.setVisibility(View.INVISIBLE);
            }
        };
        restUtility.get(url, listener, errorListener);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppClose.activity3 = null;
    }
}
