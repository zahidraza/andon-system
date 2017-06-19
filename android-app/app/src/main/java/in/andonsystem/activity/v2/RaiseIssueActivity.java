package in.andonsystem.activity.v2;

import android.content.Context;
import android.content.Intent;
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

import com.android.volley.Response;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.Constants;
import in.andonsystem.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.adapter.CustomBuyerAdapter;
import in.andonsystem.entity.Buyer;
import in.andonsystem.entity.User;
import in.andonsystem.service.BuyerService;
import in.andonsystem.service.UserService;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;

public class RaiseIssueActivity extends AppCompatActivity {

    private final String TAG = RaiseIssueActivity.class.getSimpleName();

    private Context mContext;
    private RestUtility restUtility;
    private App app;

    private SharedPreferences userPref;
    private SharedPreferences appPref;
    private UserService userService;

    private Spinner teamFilter;
    private Spinner buyerFilter;
    private Spinner problemFilter;
    private EditText description;

    private String selectedTeam = "Select Team";

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
        JSONObject issue = new JSONObject();
        try {
            issue.put("buyerId",buyerId);
            issue.put("problem",problem);
            issue.put("description",desc);
            issue.put("raisedBy", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        raiseIssue(issue);
    }

    private void raiseIssue(JSONObject issue){
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
        ErrorListener errorListener = new ErrorListener(mContext) {
            @Override
            protected void handleTokenExpiry() {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        };

        String url = Constants.API2_BASE_URL + "/issues";
        restUtility.post(url,issue,listener,errorListener);
    }

    private void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}
