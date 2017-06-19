package in.andonsystem.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.AppController;
import in.andonsystem.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.adapter.AdapterReport;
import in.andonsystem.service.ProblemService;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.MyJsonObjectRequest;
import in.andonsystem.util.RestUtility;
import in.andonsystem.v2.authenticator.AuthConstants;
import in.andonsystem.dto.Problem;
import in.andonsystem.entity.Buyer;
import in.andonsystem.service.BuyerService;
import in.andonsystem.Constants;
import in.andonsystem.view.DividerItemDecoration;

public class ReportActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private final String TAG = ReportActivity.class.getSimpleName();

    private Context mContext;
    private App app;
    private SharedPreferences userPref;
    private BuyerService buyerService;
    private ProblemService problemService;

    private ProgressBar progress;
    private LinearLayout container;
    private TextView dateView;
    private RecyclerView recyclerView;
    private TextView message;

    private String userType;
    private RestUtility restUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppClose.activity4 = this;
        mContext = this;
        app = (App) getApplication();
        userPref = getSharedPreferences(Constants.USER_PREF,0);
        buyerService = new BuyerService(app);
        problemService = new ProblemService(app);
        restUtility = new RestUtility(mContext);
        userType = userPref.getString(Constants.USER_TYPE,"");

        progress = (ProgressBar)findViewById(R.id.loading_progress);
        container = (LinearLayout)findViewById(R.id.report_container);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        String date = String.format("%02d/%02d/%04d",day,month+1,year);

        dateView = (TextView)findViewById(R.id.date_view);
        dateView.setText(date);

        recyclerView = new RecyclerView(this);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
        recyclerView.setLayoutParams(params);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,R.drawable.divider));

        message = new TextView(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.gravity = Gravity.CENTER_HORIZONTAL;
        params2.weight = 1.0f;
        message.setText("No Report for selected day found");
        message.setTextColor(ContextCompat.getColor(this,R.color.tomato));

        showReport(date);
    }

    public void selectDate(View view){
        Log.i(TAG,"selectDate()");
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showReport(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        long start=0L,end=0L;
        try {
            Date d = sdf.parse(date);
            start = d.getTime();
            end = new Date(d.getTime() + (1000*60*60*24)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String url;
        Response.Listener<JSONObject> listener;
        ErrorListener errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                Intent intent = new Intent(mContext, LoginActivity.class);
            }
        };

        if (userType.equalsIgnoreCase(Constants.USER_FACTORY)) {
            url = Constants.API1_BASE_URL + "/issues?start=" + start + "&end="+ end ;
            listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "Issue1 Response :" + response.toString());
                    try {
                        JSONArray issues = response.getJSONArray("issues");

                        if (issues.length() > 0){
                            List<Problem> problems = new ArrayList<>();
                            JSONObject issue;
                            in.andonsystem.entity.Problem problem;
                            long raisedAt,fixAt,downtime;
                            for (int i = 0; i < issues.length(); i++){
                                issue = issues.getJSONObject(i);
                                problem = problemService.findOne(issue.getLong("problemId"));
                                raisedAt = issue.getLong("raisedAt");
                                if (issue.getString("fixAt").equals("null")){
                                    downtime = -1L;
                                }else {
                                    fixAt = issue.getLong("fixAt");
                                    downtime = fixAt - raisedAt;
                                }
                                Log.d(TAG,"downtime="+ downtime);
                                problems.add(new Problem(issue.getLong("id"),"Line " + issue.getInt("line"),problem.getDepartment(),problem.getName(),downtime));
                            }
                            container.removeView(recyclerView);
                            container.removeView(message);
                            container.addView(recyclerView);
                            AdapterReport adapter = new AdapterReport(mContext,problems);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        }else {
                            container.removeView(recyclerView);
                            container.removeView(message);
                            container.addView(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
        }else {
            url = Constants.API2_BASE_URL + "/issues?start=" + start + "&end="+ end ;
            listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "Issue2 Response :" + response.toString());
                    try {
                        JSONArray issues = response.getJSONArray("issues");
                        if (issues.length() > 0){
                            List<Problem> problems = new ArrayList<>();
                            JSONObject issue;
                            Buyer buyer;
                            long raisedAt,fixAt,downtime;
                            for (int i = 0; i < issues.length(); i++){
                                issue = issues.getJSONObject(i);
                                buyer = buyerService.findOne(issue.getLong("buyerId"));
                                raisedAt = issue.getLong("raisedAt");
                                if (issue.getString("fixAt").equals("null")){
                                    downtime = -1L;
                                }else {
                                    fixAt = issue.getLong("fixAt");
                                    downtime = fixAt - raisedAt;
                                }
                                Log.d(TAG,"downtime="+ downtime);
                                problems.add(new Problem(issue.getLong("id"),buyer.getTeam(),buyer.getName(),issue.getString("problem"),downtime));
                            }
                            container.removeView(recyclerView);
                            container.removeView(message);
                            container.addView(recyclerView);
                            AdapterReport adapter = new AdapterReport(mContext,problems);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        }else {
                            container.removeView(recyclerView);
                            container.removeView(message);
                            container.addView(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
        }
        restUtility.get(url, listener, errorListener);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(view.isShown()) {
            Log.i(TAG, "onDateSet()");
            String date = String.format("%02d/%02d/%04d", day, month + 1, year);
            dateView.setText(date);
            showReport(date);
        }
    }

    public static  class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), (ReportActivity)getActivity(), year, month, day);
        }
    }

}
