package in.andonsystem.activity.v1;

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
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.Constants;
import in.andonsystem.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.entity.Designation;
import in.andonsystem.entity.Issue1;
import in.andonsystem.entity.User;
import in.andonsystem.service.IssueService1;
import in.andonsystem.service.UserService;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.util.RestUtility;

public class IssueDetailActivity extends AppCompatActivity {

    private final String TAG = IssueDetailActivity.class.getSimpleName();

    private Context mContext;
    private App app;

    private TextView problem;
    private TextView dept;
    private TextView section;
    private TextView line;
    private TextView opNo;
    private TextView raisedAt;
    private TextView ackAt;
    private TextView fixAt;
    private TextView raisedBy;
    private TextView ackBy;
    private TextView fixBy;
    private TextView desc;
    private TextView processingAt;
    private Button ackButton;
    private Button seekHelpBtn;
    private Button fixButton;
    private LinearLayout layout;
    private ProgressBar progress;


    private IssueService1 issueService1;
    private UserService userService;
    private Long issueId;
    private SharedPreferences userPref;
    private User user;
    private Issue1 issue;
    private RestUtility restUtility;
    private ErrorListener errorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_issue_detail1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppClose.activity4 = this;
        mContext = this;
        app = (App)getApplication();
        issueService1 = new IssueService1(app);
        userService = new UserService(app);
        userPref = getSharedPreferences(Constants.USER_PREF, 0);
        String email = userPref.getString(Constants.USER_EMAIL, null);
        if (email != null) {
            user = userService.findByEmail(email);
        }

        problem = (TextView)findViewById(R.id.detail_prob);
        dept = (TextView)findViewById(R.id.detail_dept);
        section = (TextView)findViewById(R.id.detail_section);
        line = (TextView)findViewById(R.id.detail_line);
        opNo = (TextView)findViewById(R.id.detail_op_no);
        raisedAt = (TextView)findViewById(R.id.detail_raised_at);
        ackAt = (TextView)findViewById(R.id.detail_ack_at);
        fixAt = (TextView)findViewById(R.id.detail_solved_at);
        raisedBy = (TextView)findViewById(R.id.detail_raised_by);
        ackBy = (TextView)findViewById(R.id.detail_ack_by);
        fixBy = (TextView)findViewById(R.id.detail_fix_by);
        processingAt = (TextView)findViewById(R.id.detail_processing_at);
        desc = (TextView)findViewById(R.id.detail_desc);
        layout = (LinearLayout)findViewById(R.id.issue_detail_layout);
        progress = (ProgressBar) findViewById(R.id.detail_loading);

        ackButton = getButton("ACKNOWLEDGE");
        fixButton = getButton("FIX");
        seekHelpBtn = getButton("SEEK HELP");
        ackButton.setBackgroundColor(ContextCompat.getColor(mContext,R.color.blue));
        fixButton.setBackgroundColor(ContextCompat.getColor(mContext,R.color.limeGreen));
        seekHelpBtn.setBackgroundColor(ContextCompat.getColor(mContext,R.color.yellow));

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
        seekHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekHelp();
            }
        });

        errorListener = new ErrorListener(mContext) {
            @Override
            protected void handleTokenExpiry() {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        };
        restUtility = new RestUtility(this){
            @Override
            protected void handleInternetConnRetry() {
                onStart();
            }

            @Override
            protected void handleInternetConnExit() {
                AppClose.close();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        issueId = getIntent().getLongExtra("issueId",0L);
        Log.d(TAG, "issueId = " + issueId);

        DateFormat df = new SimpleDateFormat("hh:mm aa");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        issue = issueService1.findOne(issueId);


        problem.setText(issue.getProblem().getName());
        dept.setText(issue.getProblem().getDepartment());
        section.setText(issue.getSection());
        line.setText(String.valueOf(issue.getLine()));
        opNo.setText(issue.getOperatorNo());
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

        Log.d(TAG, "issueId = " + issue.getId() + ", problem = " + issue.getProblem().getName() + ", for line = " + issue.getLine());
        Log.d(TAG, "user desgn = " + user.getDesignation().getName());

        List<Designation> designations = issue.getProblem().getDesignations();

        Log.d(TAG, "designation concerned are:" + designations.size());
        Set<User> usersConcerned = new HashSet<>();
        for (Designation designation: designations) {
            Log.d(TAG, "desgn = " + designation.getName());
            if (MiscUtil.getLines(designation.getLines()).contains(issue.getLine())){
                usersConcerned.addAll(designation.getUsers());
            }
        }
        Log.d(TAG, "concerned users are : " + usersConcerned.size());
        for (User u: usersConcerned) {
            Log.d(TAG, "user: " + u.getName());
        }

        Log.d(TAG, "is user concerned = " + usersConcerned.contains(user));


        if (user.getUserType().equalsIgnoreCase(Constants.USER_FACTORY)) {
            if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL0)){
                if (issue.getFixAt() == null) {
                    if (issue.getAckAt() == null) {
                        //add ackbutton
                        Log.d(TAG, "ADD ACK BUTTON");
                        layout.addView(ackButton);
                    }else {
                        //add Fix button
                        Log.d(TAG, "ADD FIX BUTTON");
                        layout.addView(fixButton);
                    }
                }
            }else {
                if (usersConcerned.contains(user)) {
                    if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL1)) {
                        if (issue.getAckAt() == null) {
                            //add ack button
                            Log.d(TAG, "ADD ACK BUTTON");
                            layout.addView(ackButton);
                        }else if (issue.getFixAt() == null && issue.getSeekHelp() == 0) {
                            //add seek help button
                            layout.addView(seekHelpBtn);
                            Log.d(TAG, "ADD SEEKHELP BUTTON");
                        }
                    }else if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL2)) {
                        if (issue.getProcessingAt() == 2) {
                            if (issue.getAckAt() == null) {
                                //add ack button
                                Log.d(TAG, "ADD ACK BUTTON");
                                layout.addView(ackButton);
                            }else if (issue.getSeekHelp() < 2) {
                                // add seekhelp button
                                Log.d(TAG, "ADD SEEKHELP BUTTON");
                                layout.addView(seekHelpBtn);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        layout.removeAllViews();
    }

    private void acknowledge() {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("status")){
                        showMessage(response.getString("message"));
                    }else if (response.has("id")){
                        showMessage("Issue acknowledged successfully");
                    }
                    finish();
                }catch (JSONException e){
                    e.printStackTrace();
                }
                
            }
        };
        String url = Constants.API1_BASE_URL + "/issues/" + issue.getId() + "?operation=OP_ACK";
        JSONObject data = new JSONObject();
        try {
            data.put("ackBy",user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        restUtility.patch(url, data, listener, errorListener);
    }

    private void fix() {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("status")){
                        showMessage(response.getString("message"));
                    }else if (response.has("id")){
                        showMessage("Issue fixed successfully");
                    }
                    finish();
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        };
        String url = Constants.API1_BASE_URL + "/issues/" + issue.getId() + "?operation=OP_FIX";
        JSONObject data = new JSONObject();
        try {
            data.put("fixBy",user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        restUtility.patch(url, data, listener, errorListener);
    }

    private void seekHelp() {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("status")){
                        showMessage(response.getString("message"));
                    }else if (response.has("id")){
                        showMessage("Help sought successfully");
                    }
                    finish();
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        };
        String url = Constants.API1_BASE_URL + "/issues/" + issue.getId() + "?operation=OP_SEEK_HELP";
        JSONObject data = new JSONObject();
        try {
            data.put("seekHelp",user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL1) ? 1 : 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        restUtility.patch(url, data, listener, errorListener);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
