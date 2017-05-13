package in.andonsystem.v2.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.AppController;
import in.andonsystem.R;
import in.andonsystem.v2.adapter.CustomBuyerAdapter;
import in.andonsystem.v2.authenticator.AuthConstants;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.service.BuyerService;
import in.andonsystem.v2.service.UserService;
import in.andonsystem.v2.util.Constants;
import in.andonsystem.v2.util.MyJsonRequest;

public class RaiseIssueActivity2 extends AppCompatActivity {

    private final String TAG = RaiseIssueActivity2.class.getSimpleName();

    private Context mContext;
    private AccountManager mAccountManager;
    private App app;

    private SharedPreferences userPref;
    private SharedPreferences appPref;
    private UserService userService;

    private Spinner teamFilter;
    private Spinner buyerFilter;
    private Spinner problemFilter;
    private EditText description;

    private String selectedTeam = "Select Team";
    private JSONObject issue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_raise_issue2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppClose.activity4 = this;

        mContext = this;
        app = (App) getApplication();
        mAccountManager = AccountManager.get(this);
        userPref = getSharedPreferences(Constants.USER_PREF, 0);
        appPref = getSharedPreferences(Constants.APP_PREF,0);
        userService = new UserService(app);

        teamFilter = (Spinner) findViewById(R.id.ri_team_filter);
        buyerFilter = (Spinner) findViewById(R.id.ri_buyer_filter);
        problemFilter = (Spinner) findViewById(R.id.ri_problem_filter);
        description = (EditText) findViewById(R.id.ri_problem_desc);

        /*//////////////// Populating team filter //////////////////////*/
        final String[] teams = appPref.getString(Constants.APP_TEAMS,"").split(";");
        final List<String> teamList = new ArrayList<>();
        teamList.add("Select Team");
        for (String t: teams) {
            teamList.add(t);
        }
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teamList);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamFilter.setAdapter(teamAdapter);
        teamFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemSelect() : team");
                selectedTeam = teamList.get(position);
                updateBuyer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateBuyer();

         /*//////////////// Populating problem filter //////////////////////*/
        final String[] problems = appPref.getString(Constants.APP_PROBLEMS,"").split(";");
        ArrayAdapter<String> problemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, problems);
        problemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        problemFilter.setAdapter(problemAdapter);


    }

    private void updateBuyer(){
        Log.d(TAG, "updateBuyer()");

        BuyerService buyerService = new BuyerService(app);
        List<Buyer> buyers;
        if(! selectedTeam.contains("Select")){
            buyers = buyerService.findByTeam(selectedTeam);
        }else {
            buyers = new ArrayList<>();
            buyers.add(new Buyer(0L,"Select Buyer",""));
        }

        CustomBuyerAdapter buyerAdapter = new CustomBuyerAdapter(this,R.layout.spinner_list_item,buyers);
        buyerAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        buyerFilter.setAdapter(buyerAdapter);

    }

    public void raiseIssue(View v){
        Log.d(TAG,"raiseIssue");
        String email = userPref.getString(Constants.USER_EMAIL,null);
        User user = userService.findByEmail(email);

        String team = teamFilter.getSelectedItem().toString();
        String buyer = ((TextView)buyerFilter.findViewById(R.id.id)).getText().toString();
        String problem = problemFilter.getSelectedItem().toString();
        String desc = description.getText().toString();

        Long buyerId = Long.parseLong(buyer);

        if(buyerId == 0L){
            showMessage("Select Buyer.");
            return;
        }else if(problem.contains("Select")){
            showMessage("Select Problem.");
            return;
        }else if(TextUtils.isEmpty(desc)){
            showMessage("Enter problem description.");
            return;
        }
        issue = new JSONObject();
        try {
            issue.put("buyerId",buyerId);
            issue.put("problem",problem);
            issue.put("description",desc);
            issue.put("raisedBy", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        raiseIssue();
    }

    private void raiseIssue(){

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,response.toString());
                if (response.has("status")){
                    try {
                        showMessage(response.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                finish();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse resp = error.networkResponse;
//                String data = new String(resp.data != null ? resp.data : "empty body".getBytes());
//                Log.d(TAG,data);
                if (resp != null && resp.statusCode == 400){
                    showMessage("Some error occured. inform developer.");
                }
                else if (resp != null && resp.statusCode == 401){
                    invalidateAccessToken();
                    getAuthToken();
                }
                else{
                    showMessage("check your internet connection");
                }
            }
        };
        String url = Constants.API2_BASE_URL + "/issues";
        Log.d(TAG, "Issue Raise url:" + url);
        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN,null);
        if(accessToken == null){
            getAuthToken();
            return;
        }
        MyJsonRequest request = new MyJsonRequest(Request.Method.POST,url,issue,listener,errorListener,accessToken);
        request.setRetryPolicy( new DefaultRetryPolicy(20*1000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(TAG);
        AppController.getInstance().addToRequestQueue(request);
    }

    private void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
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
                    raiseIssue();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }
        }).start();
    }

}
