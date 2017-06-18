package in.andonsystem.activity.v1;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.TreeSet;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.AppController;
import in.andonsystem.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.adapter.AdapterNotification;
import in.andonsystem.dto.Notification;
import in.andonsystem.entity.Issue1;
import in.andonsystem.entity.User;
import in.andonsystem.service.IssueService1;
import in.andonsystem.service.UserService;
import in.andonsystem.Constants;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;

public class NotificationActivity extends AppCompatActivity {

    private final String TAG = NotificationActivity.class.getSimpleName();

    private Context mContext;
    private App app;
    private UserService userService;
    private IssueService1 issueService;
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
        setContentView(R.layout.activity_notification1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppClose.activity4 = this;
        mContext = this;
        app = (App) getApplication();
        userService = new UserService(app);
        issueService = new IssueService1(app);
        userPref = getSharedPreferences(Constants.USER_PREF,0);
        user = userService.findByEmail(userPref.getString(Constants.USER_EMAIL, null));

        container = (RelativeLayout) findViewById(R.id.content_nfn1);
        progress = (ProgressBar) findViewById(R.id.nfn1_loading);
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

        if (user.getUserType().equalsIgnoreCase(Constants.USER_FACTORY)) {
            if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL0)) {
                List<Issue1> issues = issueService.findAllByUser(user);

                if (issues.size() > 0) {
                    for (Issue1 issue : issues) {
                        Log.d(TAG, "LEVEL0: issue = " + issue.getProblem());
                        if (issue.getFixAt() != null) {
                            message = (issue.getFixByUser().getId() == user.getId() ? "You " : issue.getAckByUser().getName() ) + " fixed " + "problem " +  issue.getProblem().getName() + " of department  " + issue.getProblem().getDepartment();
                            timeAt = currentTime - issue.getAckAt().getTime();
                            list.add(new Notification(issue.getId(),message,timeAt, 2));
                        }else if (issue.getAckAt() != null) {
                            message = (issue.getAckByUser().getId() == user.getId() ? "You " : issue.getAckByUser().getName() ) + " acknowledged " + "problem " +  issue.getProblem().getName() + " of " + issue.getProblem().getDepartment();
                            timeAt = currentTime - issue.getAckAt().getTime();
                            list.add(new Notification(issue.getId(),message,timeAt, 1));
                        }else {
                            message = "Problem " +  issue.getProblem().getName() + " of " + issue.getProblem().getDepartment() + " was raised by " + (issue.getRaisedByUser().getId() == user.getId() ? "you " : issue.getAckByUser().getName() );
                            timeAt = currentTime - issue.getRaisedAt().getTime();
                            list.add(new Notification(issue.getId(),message,timeAt, 0));
                        }
                    }
                }

            }

//            List<Issue1> issues = issueService.findAllByBuyers(user.getBuyers());
//
//            if (issues.size() > 0){
//                for ( Issue1 issue : issues){
//                    Log.d(TAG, "MERCHANDISING: issue = " + issue.getProblem());
//                    if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL3) && issue.getProcessingAt() != 3){
//                        continue;
//                    }else if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL2) && issue.getProcessingAt() < 2){
//                        continue;
//                    }
//                    if(issue.getFixAt() != null){
//                        message = "Problem " + issue.getProblem() + " of " + issue.getBuyer().getTeam() + ":" + issue.getBuyer().getName() + " was resolved.";
//                        timeAt = currentTime - issue.getFixAt().getTime();
//                        list.add(new Notification(issue.getId(),message,timeAt, 2));
//                    }
//                    else if(issue.getAckAt() != null){
//                        message = "Problem " +  issue.getProblem() + " of " + issue.getBuyer().getTeam() + ":" + issue.getBuyer().getName() + " was acknowledged by "
//                                + (issue.getAckBy() == user.getId() ? "you" : issue.getAckByUser().getName());
//                        timeAt = currentTime - issue.getAckAt().getTime();
//                        list.add(new Notification(issue.getId(),message,timeAt, 1));
//                    }
//                    else {
//                        message = "Problem " +  issue.getProblem() + " of " + issue.getBuyer().getTeam() + ":" + issue.getBuyer().getName() + " was raised by " + issue.getRaisedByUser().getName();
//                        timeAt = currentTime - issue.getRaisedAt().getTime();
//                        list.add(new Notification(issue.getId(),message,timeAt, 0));
//                    }
//                }

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
        RestUtility restUtility = new RestUtility(this);
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
                onStart();
            }
        };
        ErrorListener errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        };
        restUtility.get(url, listener, errorListener);
    }
}
