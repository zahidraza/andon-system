package in.andonsystem.activity.v2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import in.andonsystem.App;
import in.andonsystem.Constants;
import in.andonsystem.activity.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.entity.Issue2;
import in.andonsystem.entity.User;
import in.andonsystem.service.IssueService2;
import in.andonsystem.service.UserService;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;

public class IssueDetailActivity extends AppCompatActivity {

    private final String TAG = IssueDetailActivity.class.getSimpleName();

    private Context mContext;
    private App app;
    private RestUtility restUtility;

    private TextView problem;
    private TextView team;
    private TextView buyer;
    private TextView raisedAt;
    private TextView ackAt;
    private TextView fixAt;
    private TextView raisedBy;
    private TextView ackBy;
    private TextView fixBy;
    private TextView desc;
    private TextView processingAt;
    private Button ackButton;
    private Button fixButton;
    private Button delButton;
    private LinearLayout layout;
    private ProgressBar progress;


    private IssueService2 issueService2;
    private UserService userService;
    private Long issueId;
    private SharedPreferences userPref;
    private User user;
    private Issue2 issue;
    private ErrorListener errorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "056dd13f");
        setContentView(R.layout.activity_issue_detail2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        app = (App)getApplication();
        issueService2 = new IssueService2(app);
        userService = new UserService(app);
        userPref = getSharedPreferences(Constants.USER_PREF, 0);
        user = userService.findByEmail(userPref.getString(Constants.USER_EMAIL, null));

        problem = (TextView)findViewById(R.id.detail_problem);
        team = (TextView)findViewById(R.id.detail_team);
        buyer = (TextView)findViewById(R.id.detail_buyer);
        raisedAt = (TextView)findViewById(R.id.detail_raised_at);
        ackAt = (TextView)findViewById(R.id.detail_ack_at);
        fixAt = (TextView)findViewById(R.id.detail_fix_at);
        raisedBy = (TextView)findViewById(R.id.detail_raised_by);
        ackBy = (TextView)findViewById(R.id.detail_ack_by);
        fixBy = (TextView)findViewById(R.id.detail_fix_by);
        processingAt = (TextView)findViewById(R.id.detail_processing_at);
        desc = (TextView)findViewById(R.id.detail_desc);
        layout = (LinearLayout)findViewById(R.id.issue_detail_layout);
        progress = (ProgressBar) findViewById(R.id.loading_progress);

        ackButton = getButton("ACKNOWLEDGE");
        fixButton = getButton("FIX");
        delButton = getButton("DELETE");
        ackButton.setBackgroundColor(ContextCompat.getColor(mContext,R.color.blue));
        fixButton.setBackgroundColor(ContextCompat.getColor(mContext,R.color.limeGreen));
        delButton.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color10));

        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acknowledge();
            }
        });
        fixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fix();
            }
        });
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        errorListener = new ErrorListener(mContext) {
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
        restUtility = new RestUtility(this){
            @Override
            protected void handleInternetConnRetry() {
                onStart();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        issueId = getIntent().getLongExtra("issueId",0L);
        Log.d(TAG, "issueId = " + issueId);

        DateFormat df = new SimpleDateFormat("dd MMM, hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        issue = issueService2.findOne(issueId);

        problem.setText(issue.getProblem());
        team.setText(issue.getBuyer().getTeam());
        buyer.setText(issue.getBuyer().getName());
        raisedAt.setText(df.format(issue.getRaisedAt()));
        raisedBy.setText(issue.getRaisedByUser().getName());
        ackAt.setText(( (issue.getAckAt() != null) ? df.format(issue.getAckAt()) : "-" ));
        ackBy.setText(( (issue.getAckByUser() != null) ? issue.getAckByUser().getName() : "-" ));
        fixAt.setText(( (issue.getFixAt() != null) ? df.format(issue.getFixAt()) : "-" ));
        fixBy.setText(( (issue.getFixByUser() != null) ? issue.getFixByUser().getName() : "-" ));
        if (issue.getProcessingAt() == 4 || issue.getFixAt() != null){
            processingAt.setText("Fixed");
        }else {
            processingAt.setText("Processing At Level " + issue.getProcessingAt());
        }
        
        desc.setText(issue.getDescription());

        /*////////// Adding ack or fix button ///////////////*/
        if (user.getUserType().equalsIgnoreCase(Constants.USER_MERCHANDISING)){
            if(issue.getAckAt() == null){
                if(user.getBuyers().contains(issue.getBuyer())){
                    if( issue.getProcessingAt() > 1){
                        if(user.getLevel().contains(Constants.USER_LEVEL2)){
                            layout.addView(ackButton);
                        }
                    }
                    else {
                        if(user.getLevel().contains(Constants.USER_LEVEL1)){
                            layout.addView(ackButton);
                        }
                    }
                }
            }else if (issue.getFixAt() == null){
                if(user.getBuyers().contains(issue.getBuyer())){
                    if( issue.getProcessingAt() > 1){
                        if(user.getLevel().contains(Constants.USER_LEVEL2)){
                            layout.addView(fixButton);
                        }
                    }
                    else {
                        if(user.getLevel().contains(Constants.USER_LEVEL1)){
                            layout.addView(fixButton);
                        }
                    }
                }
            }
        }else if (user.getUserType().equalsIgnoreCase(Constants.USER_SAMPLING)) {
            if (issue.getRaisedBy().equals(user.getId())) {
                layout.addView(delButton);
            }
        }
    }

    private void acknowledge(){
        progress.setVisibility(View.VISIBLE);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("status")){
                        showMessage(response.getString("message"));
                    }else if (response.has("id")){
                        showMessage("Issue acknowledged successfully");
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                progress.setVisibility(View.INVISIBLE);
                finish();
            }
        };
        String url = Constants.API2_BASE_URL + "/issues/" + issue.getId() + "?operation=OP_ACK";
        JSONObject data = new JSONObject();
        try {
            data.put("ackBy",user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        restUtility.patch(url, data, listener, errorListener);
    }

    private void fix(){
        Log.d(TAG,"fix");
        progress.setVisibility(View.VISIBLE);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("status")){
                        showMessage(response.getString("message"));
                    }else if (response.has("id")){
                        showMessage("Issue fixed successfully");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                progress.setVisibility(View.INVISIBLE);
                finish();
            }
        };
        String url = Constants.API2_BASE_URL + "/issues/" + issue.getId() + "?operation=OP_FIX";
        JSONObject data = new JSONObject();
        try {
            data.put("fixBy",user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        restUtility.patch(url, data, listener, errorListener);
    }

    private void delete(){
        Log.d(TAG,"delete");
        progress.setVisibility(View.VISIBLE);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("status")){
                        showMessage(response.getString("message"));
                    }else if (response.has("id")){
                        showMessage("Issue deleted successfully");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                progress.setVisibility(View.INVISIBLE);
                finish();
            }
        };
        String url = Constants.API2_BASE_URL + "/issues/" + issue.getId() + "?operation=OP_DEL";

        restUtility.patch(url, new JSONObject(), listener, errorListener);
    }

    private Button getButton(String name){
        Button btn = new Button(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        btn.setText(name);
        btn.setLayoutParams(params);
        return btn;
    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        layout.removeAllViews();
        progress.setVisibility(View.INVISIBLE);
    }
}
