package in.andonsystem.activity.v2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeSet;

import in.andonsystem.App;
import in.andonsystem.Constants;
import in.andonsystem.activity.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.activity.ContactActivity;
import in.andonsystem.activity.ProfileActivity;
import in.andonsystem.activity.ReportActivity;
import in.andonsystem.adapter.AdapterHome;
import in.andonsystem.dto.Problem;
import in.andonsystem.entity.Buyer;
import in.andonsystem.entity.Issue2;
import in.andonsystem.entity.User;
import in.andonsystem.entity.UserBuyer;
import in.andonsystem.service.BuyerService;
import in.andonsystem.service.IssueService2;
import in.andonsystem.service.UserBuyerService;
import in.andonsystem.service.UserService;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.util.RestUtility;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = HomeActivity.class.getSimpleName();

    private Spinner teamFilter;
    private String teamSelected = "All Teams";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout container;
    private TextView textView;
    private NavigationView navigationView;
    private ProgressBar progress;
    private Toolbar toolbar;

    private Boolean rvViewAdded;
    private SQLiteDatabase db;
    private DateFormat df;
    private SharedPreferences sharedPref;
    private Context context;
    private AlertDialog exitDialog;

    private AdapterHome rvAdapter;
    private SharedPreferences userPref;
    private SharedPreferences appPref;
    private SharedPreferences syncPref;
    private int noOfRequests = 0;
    private RestUtility restUtility;
    private ErrorListener errorListener;
    private UserService userService;
    private UserBuyerService userBuyerService;
    private IssueService2 issueService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "056dd13f");
        Mint.leaveBreadcrumb("home activity created");
        setContentView(R.layout.activity_home2
        );
        Log.i(TAG,"onCreate()");
        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appPref = getSharedPreferences(Constants.APP_PREF,0);
        userPref = getSharedPreferences(Constants.USER_PREF,0);
        syncPref = getSharedPreferences(Constants.SYNC_PREF,0);

        //views mapping
        progress = (ProgressBar)findViewById(R.id.loading_progress);
        progress.setVisibility(View.INVISIBLE);
        
        teamFilter = (Spinner)findViewById(R.id.home_team_filter);
        container = (RelativeLayout)findViewById(R.id.home_container);
        //Create swipe refresh layout
        refreshLayout = new SwipeRefreshLayout(context);
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        param1.addRule(RelativeLayout.BELOW,R.id.home_team_filter);
        refreshLayout.setLayoutParams(param1);
        //create recycler view
        recyclerView = new RecyclerView(context);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
        recyclerView.setLayoutParams(params);
        //create text view
        textView = new TextView(context);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params1.addRule(RelativeLayout.BELOW,R.id.home_team_filter);
        params1.topMargin = 50;
        textView.setLayoutParams(params1);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(ContextCompat.getColor(context,R.color.limeGreen));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        textView.setText("No Open Issues Found.");

        errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                //onStart();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            protected void onError(VolleyError error) {
                progress.setVisibility(View.INVISIBLE);
                refreshLayout.setRefreshing(false);
            }
        };
        restUtility = new RestUtility(this){
            @Override
            protected void handleInternetConnRetry() {
                onStart();
            }

        };
        userService = new UserService((App)getApplication());
        userBuyerService = new UserBuyerService((App)getApplication());
        issueService = new IssueService2((App)getApplication());
        issueService.deleteAllOlder();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,"on back pressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            syncIssues();
        }
        if(id == R.id.action_notification){
            Intent i = new Intent(context,NotificationActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart()");
        noOfRequests = 0;

        boolean firstLaunch = appPref.getBoolean(Constants.APP1_FIRST_LAUNCH, true);
        int lastAppUsed = appPref.getInt(Constants.LAST_APP_USED,0);
        if (firstLaunch || lastAppUsed == 1) {
            progress.setVisibility(View.VISIBLE);
            initializeApp();
        }else {
            showScreen();
            showIssues();
            syncUsers();
            syncIssues();
        }
    }

    @Override
    protected void onDestroy() {
        //App.activity3 = null;
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        Log.i(TAG,"finish()");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_report) {
            Intent i = new Intent(context,ReportActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_contacts) {
            Intent i = new Intent(context,ContactActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_logout) {
            userPref.edit()
                    .putBoolean(Constants.IS_LOGGED_IN, false)
                    .putString(Constants.USER_ACCESS_TOKEN,null)
                    .commit();
            Intent i = new Intent(context,LoginActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void raiseIssue(){
        Log.i(TAG,"raiseIssues()");
        Intent i = new Intent(this,RaiseIssueActivity.class);
        startActivity(i);
    }

    public void showIssues(){
        Log.i(TAG,"showIssues()");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        IssueService2 issueService = new IssueService2((App)getApplication());
        List<Issue2> issueList;
        if (teamSelected == null || teamSelected.contains("All")) {
            issueList = issueService.findAll();
        }else {
            issueList = issueService.findAllByTeam(teamSelected);
        }

        TreeSet<Problem> issues = getIssue(issueList);

        if(!issues.isEmpty()){
            //Remove both views first if exist
            container.removeView(textView);
            refreshLayout.removeView(recyclerView);
            container.removeView(refreshLayout);

            //Add recyclerView
            //if(refreshLayout != null)
            container.addView(refreshLayout);
            refreshLayout.addView(recyclerView);
            rvViewAdded = true;
            rvAdapter = new AdapterHome(this,issues);
            recyclerView.setAdapter(rvAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        }else{
            //Remove both views first if exist
            container.removeView(textView);
            refreshLayout.removeView(recyclerView);
            container.removeView(refreshLayout);
            rvViewAdded = false;
            //Add textView
            container.addView(textView);
        }
    }

    private TreeSet<Problem> getIssue(List<Issue2> list){
        Log.d(TAG,"getIssue2");
        df = new SimpleDateFormat("dd MMM, hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        TreeSet<Problem> issues = new TreeSet<>();

        Log.d(TAG, "Issue2 size = " + list.size());
        for (Issue2 i : list) {
            issues.add(getProblem(i));
        }

        return issues;
    }

    private Problem getProblem(Issue2 issue){
        String raiseTime = df.format(issue.getRaisedAt());
        long downtime = (issue.getFixAt() != null) ? (issue.getFixAt().getTime() - issue.getRaisedAt().getTime() ): -1L;
        int flag = (issue.getFixAt() != null) ? 2 : ( (issue.getAckAt() != null) ? 1: 0);
        return new Problem(issue.getId(), issue.getBuyer().getTeam(), issue.getBuyer().getName(), issue.getProblem(),raiseTime,downtime,flag,2);
    }

    public void syncIssues(){
        Log.i(TAG,"syncIssues()");
        refreshLayout.setRefreshing(true);
        final IssueService2 issueService = new IssueService2((App)getApplication());

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Issue2 Response :" + response.toString());
                Long syncTime;

                try {
                    syncTime = response.getLong("issueSync");
                    JSONArray issues = response.getJSONArray("issues");
                    if (issues.length() > 0) {
                        if(!rvViewAdded){
                            container.removeView(textView);
                            refreshLayout.removeView(recyclerView);
                            container.removeView(refreshLayout);


                            container.addView(refreshLayout);
                            refreshLayout.addView(recyclerView);
                            TreeSet<Problem> issue = new TreeSet<>();
                            rvAdapter = new AdapterHome(context, issue);
                            recyclerView.setAdapter(rvAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            rvViewAdded = true;
                        }
                            /*Save or Update Issues in database*/
                        List<Issue2> issueList = new ArrayList<>();
                        for (int i = 0; i < issues.length(); i++) {
                            issueList.add(getIssueEntity(issues.getJSONObject(i)));
                        }
                        issueService.saveOrUpdate(issueList);
                        Issue2 i;
                        for (Issue2 issue : issueList) {
                            i = issueService.findOne(issue.getId());
                            //If Issue2 belongs to applied filter then add or update rvAdapter

                            if (teamSelected == null || teamSelected.contains("All") || teamSelected.equalsIgnoreCase(i.getBuyer().getTeam())) {

                                if (issue.getDeleted()) {
                                    rvAdapter.delete(getProblem(issue));
                                }
                                else if (issue.getFixAt() == null && issue.getAckAt() == null) {
                                    Log.i(TAG, "Adapter : add Issue2");
                                    rvAdapter.insert(getProblem(issue));
                                } else {
                                    Log.i(TAG, "Adapter : update Issue2");
                                    rvAdapter.update(getProblem(issue));
                                }
                            }
                        }
                        if (rvAdapter.getItemCount() == 0) {
                            //Remove both views first if exist
                            container.removeView(textView);
                            refreshLayout.removeView(recyclerView);
                            container.removeView(refreshLayout);
                            rvViewAdded = false;
                            //Add textView
                            container.addView(textView);
                        }

                    }
                    syncPref.edit().putLong(Constants.LAST_ISSUE2_SYNC, syncTime).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                refreshLayout.setRefreshing(false);
            }
        };
        ErrorListener errorListener1 = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                //syncIssues();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        };
        Long lastSync = syncPref.getLong(Constants.LAST_ISSUE2_SYNC,0);
        String url = Constants.API2_BASE_URL + "/issues?start=" + lastSync;
        restUtility.get(url,listener,errorListener1);
    }

    private Issue2 getIssueEntity(JSONObject i) {
        Issue2 mIssue = null;
        try {
            mIssue = new Issue2(
                    i.getLong("id"),
                    i.getLong("buyerId"),
                    i.getString("problem"),
                    i.getString("description"),
                    i.getLong("raisedBy"),
                    new Date(i.getLong("raisedAt")),
                    i.getInt("processingAt"),
                    i.getBoolean("deleted")
            );

            if (! i.getString("ackBy").equals("null")) {
                mIssue.setAckBy(i.getLong("ackBy"));
            }
            if (! i.getString("ackAt").equals("null")) {
                mIssue.setAckAt(new Date(i.getLong("ackAt")));
            }
            if (! i.getString("fixBy").equals("null")) {
                mIssue.setFixBy(i.getLong("fixBy"));
            }
            if (! i.getString("fixAt").equals("null")) {
                mIssue.setFixAt(new Date(i.getLong("fixAt")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIssue;
    }

    private void syncUsers() {
        final UserService userService = new UserService((App)getApplication());
        final UserBuyerService userBuyerService = new UserBuyerService((App)getApplication());
        progress.setVisibility(View.VISIBLE);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonUsers = response.getJSONArray("users");
                    Long userSync = response.getLong("userSync");
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
                    syncPref.edit()
                            .putLong(Constants.LAST_USER_SYNC, userSync)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progress.setVisibility(View.INVISIBLE);
            }
        };
        long lastUserSync = syncPref.getLong(Constants.LAST_USER_SYNC,0);
        String url = Constants.API2_BASE_URL + "/users?after=" + lastUserSync;
        restUtility.get(url, listener, errorListener);
    }

    private void showScreen() {

        String level = userPref.getString(Constants.USER_LEVEL,"");
        String user = userPref.getString(Constants.USER_NAME,"");

        //FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                raiseIssue();
            }
        });
        fab.hide();
        if(level.equalsIgnoreCase(Constants.USER_LEVEL0)){
            fab.show();
        }

        //DrawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.nav_header_username);
        username.setText(user);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ProfileActivity.class);
                startActivity(i);
            }
        });
        


        /*//// Section Filter //////*/
        String[] teams = appPref.getString(Constants.APP_TEAMS, "").split(";");
        final List<String> teamList = new ArrayList<>();
        teamList.add("All Teams");
        for (String t: teams) {
            teamList.add(t);
        }
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teamList);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamFilter.setAdapter(teamAdapter);

        
        teamFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"onItemSelect() : section");
                if(teamFilter == null || view == null){
                    return;
                }
                teamSelected = parent.getItemAtPosition(position).toString();
                if(teamSelected.contains("All")){
                    teamSelected = null;
                }
                showIssues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                syncIssues();
            }
        });
    }

    private void initializeApp(){
        AlertDialog dialog = getAlertDialog();
        Boolean isConnected = MiscUtil.isConnectedToInternet(this);
        if (!isConnected) {
            dialog.show();
        }else {
            final BuyerService buyerService = new BuyerService((App)getApplication());
            issueService.deleteAll();
            userBuyerService.deleteAll();
            buyerService.deleteAll();
            userService.deleteAll();
            syncPref.edit()
                    .putLong(Constants.LAST_USER_SYNC,0)
                    .putLong(Constants.LAST_ISSUE2_SYNC,0)
                    .apply();

            Response.Listener<JSONArray> listenerteam = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i(TAG, "Team Response :" + response.toString());

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
                    noOfRequests++;
                    if (noOfRequests == 3){
                        appInitComplete();
                    }
                }
            };

            Response.Listener<JSONArray> listenerProblem = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i(TAG, "Problem Response :" + response.toString());

                    String[] problems = new String[response.length()];
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            problems[i] = response.getString(i);
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
                    noOfRequests++;
                    if (noOfRequests == 3){
                        appInitComplete();
                    }
                }
            };
            
            Response.Listener<JSONArray> listenerBuyer = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i(TAG, "Buyer Response :" + response.toString());
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
                    noOfRequests++;
                    if (noOfRequests == 3){
                        appInitComplete();
                    }
                }
            };


            String urlteam = Constants.API2_BASE_URL + "/teams";
            String urlProblem = Constants.API2_BASE_URL + "/problems";
            String urlBuyer = Constants.API2_BASE_URL + "/buyers";
            restUtility.getJsonArray(urlteam, listenerteam, errorListener);
            restUtility.getJsonArray(urlProblem,listenerProblem,errorListener);
            restUtility.getJsonArray(urlBuyer, listenerBuyer, errorListener);


        }
    }

    private void appInitComplete(){
        appPref.edit()
                .putBoolean(Constants.APP1_FIRST_LAUNCH,false)
                .putInt(Constants.LAST_APP_USED,2)
                .commit();
        progress.setVisibility(View.INVISIBLE);
        onStart();
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
    protected void onStop() {
        super.onStop();
        progress.setVisibility(View.INVISIBLE);
    }

}
