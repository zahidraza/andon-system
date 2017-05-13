package in.andonsystem.v2.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import in.andonsystem.R;
import in.andonsystem.v2.adapter.AdapterReport;
import in.andonsystem.v2.authenticator.AuthConstants;
import in.andonsystem.v2.dto.Problem;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.service.BuyerService;
import in.andonsystem.v2.util.Constants;
import in.andonsystem.v2.util.MyJsonRequest;
import in.andonsystem.v2.view.DividerItemDecoration;

public class ReportActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private final String TAG = ReportActivity.class.getSimpleName();

    private Context mContext;
    private App app;
    private SharedPreferences userPref;
    private AccountManager mAccountManager;
    private BuyerService buyerService;

    private ProgressBar progress;
    private LinearLayout container;
    private TextView dateView;
    private RecyclerView recyclerView;
    private TextView message;

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
        mAccountManager = AccountManager.get(this);
        buyerService = new BuyerService(app);

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

        String url = Constants.API2_BASE_URL + "/issues?start=" + start + "&end="+ end ;
        Log.i(TAG, "url = " + url);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Issue Response :" + response.toString());
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
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                NetworkResponse resp = error.networkResponse;
//                    String data = new String(resp.data);
//                    Log.i(TAG, "response status: " + data);
                if (resp != null && resp.statusCode == 401) {
                    invalidateAccessToken();
                    getAuthToken();
                } else {
                    Toast.makeText(mContext, "Unable to Sync. Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN, null);
        if (accessToken == null) {
            getAuthToken();
            return;
        }

        MyJsonRequest request = new MyJsonRequest(Request.Method.GET, url, null, listener, errorListener, accessToken);
        request.setTag(TAG);
        AppController.getInstance().addToRequestQueue(request);
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

    private void invalidateAccessToken(){
        Log.d(TAG,"invalidateAccessToken");
        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN,null);
        mAccountManager.invalidateAuthToken(AuthConstants.VALUE_ACCOUNT_TYPE,accessToken);
    }

    private void getAuthToken(){
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
                    //showReport();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }
        }).start();
    }

}
