package in.andonsystem.v2.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.AppController;
import in.andonsystem.R;
import in.andonsystem.v2.authenticator.AuthConstants;
import in.andonsystem.v2.entity.Issue;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.service.IssueService;
import in.andonsystem.v2.service.UserService;
import in.andonsystem.v2.util.Constants;
import in.andonsystem.v2.util.MyJsonRequest;

public class IssueDetailActivity2 extends AppCompatActivity {

    private final String TAG = IssueDetailActivity2.class.getSimpleName();

    private Context mContext;
    private App app;
    private AccountManager mAccountManager;

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
    private LinearLayout layout;
    private ProgressBar progress;


    private IssueService issueService;
    private UserService userService;
    private Long issueId;
    private SharedPreferences userPref;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_issue_detail2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppClose.activity4 = this;
        mContext = this;
        app = (App)getApplication();
        mAccountManager = AccountManager.get(this);
        issueService = new IssueService(app);
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
        progress = (ProgressBar) findViewById(R.id.detail_loading);

        ackButton = getButton("ACKNOWLEDGE");
        fixButton = getButton("FIX");

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
        //get current time

    }

    @Override
    protected void onStart() {
        super.onStart();
        issueId = getIntent().getLongExtra("issueId",0L);
        Log.d(TAG, "issueId = " + issueId);

        DateFormat df = new SimpleDateFormat("hh:mm aa");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        Issue issue = issueService.findOne(issueId);

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
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        layout.removeView(ackButton);
        layout.removeView(fixButton);
    }

    private void acknowledge(){
        Log.d(TAG,"acknowldege");

        String url = Constants.API2_BASE_URL + "/issues/" + issueId + "?operation=OP_ACK";
        Log.i(TAG, "config url: " + url);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Config Response :" + response.toString());
                progress.setVisibility(View.GONE);
                finish();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                NetworkResponse resp = error.networkResponse;
                String data = new String((resp.data != null) ? resp.data : "Empty body".getBytes());
                Log.i(TAG, "response data: " + data);
                if (resp != null && resp.statusCode == 401) {
                    invalidateAccessToken();
                    getAuthToken("ACK");
                } else {
                    Toast.makeText(mContext, "Unable to Sync. Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                }
                progress.setVisibility(View.GONE);
            }
        };
        progress.setVisibility(View.VISIBLE);
        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN, null);
        if (accessToken == null) {
            getAuthToken("ACK");
            return;
        }
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("ackBy", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyJsonRequest request = new MyJsonRequest(Request.Method.PATCH, url, reqData, listener, errorListener, accessToken);
        request.setTag(TAG);
        AppController.getInstance().addToRequestQueue(request);
    }

    private void fix(){
        Log.d(TAG,"fix");

        String url = Constants.API2_BASE_URL + "/issues/" + issueId + "?operation=OP_FIX";
        Log.i(TAG, "config url: " + url);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Config Response :" + response.toString());
                progress.setVisibility(View.GONE);
                finish();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                NetworkResponse resp = error.networkResponse;
                String data = new String((resp.data != null) ? resp.data : "Empty body".getBytes());
                Log.i(TAG, "response data: " + data);
                if (resp != null && resp.statusCode == 401) {
                    invalidateAccessToken();
                    getAuthToken("FIX");
                } else {
                    Toast.makeText(mContext, "Unable to Sync. Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                }
                progress.setVisibility(View.GONE);
            }
        };
        progress.setVisibility(View.VISIBLE);
        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN, null);
        if (accessToken == null) {
            getAuthToken("FIX");
            return;
        }
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("fixBy", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyJsonRequest request = new MyJsonRequest(Request.Method.PATCH, url, reqData, listener, errorListener, accessToken);
        request.setTag(TAG);
        AppController.getInstance().addToRequestQueue(request);
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


    private void invalidateAccessToken(){
        Log.d(TAG,"invalidateAccessToken");
        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN,null);
        mAccountManager.invalidateAuthToken(AuthConstants.VALUE_ACCOUNT_TYPE,accessToken);
    }

    private void getAuthToken(final String operation){
        Log.d(TAG,"getAuthToken");
        Account[] accounts = mAccountManager.getAccounts();
        String email = userPref.getString(Constants.USER_EMAIL, null);
        Account account = null;
        for (Account a: accounts){
            if(a.name.equals(email)){
                account = a;
                break;
            }
        }

        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, AuthConstants.AUTH_TOKEN_TYPE_FULL_ACCESS, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    String authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    userPref.edit().putString(Constants.USER_ACCESS_TOKEN,authToken).commit();
                    if(operation == "ACK") {
                        acknowledge();
                    }else if(operation == "FIX") {
                        fix();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }
        }).start();
    }
}
