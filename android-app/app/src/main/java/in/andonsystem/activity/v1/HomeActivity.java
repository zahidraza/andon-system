package in.andonsystem.activity.v1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;

import in.andonsystem.App;
import in.andonsystem.Constants;
import in.andonsystem.activity.LoginActivity;
import in.andonsystem.activity.ReportActivity;
import in.andonsystem.adapter.AdapterHome;

import in.andonsystem.R;
import in.andonsystem.dto.Problem;
import in.andonsystem.entity.Designation;
import in.andonsystem.entity.ProblemDesignation;
import in.andonsystem.entity.Issue1;

import in.andonsystem.entity.User;
import in.andonsystem.service.ProblemDesignationService;
import in.andonsystem.service.DesignationService;
import in.andonsystem.service.IssueService1;
import in.andonsystem.service.ProblemService;
import in.andonsystem.service.UserService;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.util.RestUtility;
import in.andonsystem.activity.ContactActivity;
import in.andonsystem.activity.ProfileActivity;

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

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = HomeActivity.class.getSimpleName();

    private Spinner line;
    private Spinner section;
    private Spinner dept;
    private Integer lineNo;
    private String secSelected;
    private String deptSelected;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout container;
    private TextView textView;
    private NavigationView navigationView;
    private ProgressBar progress;
    private Toolbar toolbar;

    private Boolean rvViewAdded;
    private DateFormat df;
    private Context context;

    private AdapterHome rvAdapter;
    private SharedPreferences userPref;
    private SharedPreferences appPref;
    private SharedPreferences syncPref;
    private int noOfRequests = 0;
    private RestUtility restUtility;
    private ErrorListener errorListener;
    private UserService userService;
    private IssueService1 issueService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "056dd13f");
        Mint.leaveBreadcrumb("home activity created");
        setContentView(R.layout.activity_home1
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
        line = (Spinner)findViewById(R.id.home_line);
        section = (Spinner)findViewById(R.id.home_section);
        dept = (Spinner)findViewById(R.id.home_department);
        container = (RelativeLayout)findViewById(R.id.home_container);
        //Create swipe refresh layout
        refreshLayout = new SwipeRefreshLayout(context);
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        param1.addRule(RelativeLayout.BELOW,R.id.home_filter);
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
        params1.addRule(RelativeLayout.BELOW,R.id.home_filter);
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
        issueService = new IssueService1((App)getApplication());
        issueService.deleteAllOlder();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG,"back pressed");
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
//        if(desgnId == 43) {     ///SMED Executive
//            menu.removeItem(R.id.action_notification);
//        }
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
        Log.d(TAG, "firstLaunch = " + firstLaunch + ", lastAppUsed = " + lastAppUsed);
        if (firstLaunch || lastAppUsed == 2) {
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

        if (id == R.id.nav_stylechangeover) {
            Intent i = new Intent(context,StyleChangeOverActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_report) {
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
        IssueService1 issueService = new IssueService1((App)getApplication());

        List<Issue1> issueList = issueService.findAllWithFilter(lineNo,secSelected,deptSelected);
        
        TreeSet<Problem> issues = getIssue(issueList);
        
        if(!issues.isEmpty()){
            Log.d(TAG,"No. of ssues found = " + issues.size());
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
            Log.d(TAG,"No issues found");
            //Remove both views first if exist
            container.removeView(textView);
            refreshLayout.removeView(recyclerView);
            container.removeView(refreshLayout);
            rvViewAdded = false;
            //Add textView
            container.addView(textView);
        }
    }

    private TreeSet<Problem> getIssue(List<Issue1> list){
        Log.d(TAG,"getIssue2");
        df = new SimpleDateFormat("hh:mm aa");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        TreeSet<in.andonsystem.dto.Problem> issues = new TreeSet<>();
        
        Log.d(TAG, "Issue2 size = " + list.size());
        for (Issue1 i : list) {
            Log.d(TAG,"issue: " + i.toString());
            issues.add(getProblem(i));
        }

        return issues;
    }

    private Problem getProblem(Issue1 issue){
        String raiseTime = df.format(issue.getRaisedAt());
        long downtime = (issue.getFixAt() != null) ? (issue.getFixAt().getTime() - issue.getRaisedAt().getTime() ): -1L;
        int flag = (issue.getFixAt() != null) ? 2 : ( (issue.getAckAt() != null) ? 1: 0);
        return new Problem(issue.getId(), "Line " + issue.getLine(), issue.getProblem().getDepartment(), issue.getProblem().getName(),raiseTime,downtime,flag,1, issue.getCritical());
    }

    public void syncIssues(){
        Log.i(TAG,"syncIssues()");
        refreshLayout.setRefreshing(true);
        final IssueService1 issueService = new IssueService1((App)getApplication());

        Long syncTime = syncPref.getLong(Constants.LAST_ISSUE1_SYNC,0);

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
                        List<Issue1> issueList = new ArrayList<>();
                        for (int i = 0; i < issues.length(); i++) {
                            issueList.add(getIssueEntity(issues.getJSONObject(i)));
                        }
                        issueService.saveOrUpdate(issueList);
                        Issue1 i;
                        for (Issue1 issue : issueList) {
                            i = issueService.findOne(issue.getId());
                            //If Issue2 belongs to applied filter then add or update rvAdapter
                            Log.d(TAG,"lineNo = " + lineNo + ", sec = " + secSelected + ", dept = " + deptSelected);
                            Log.d(TAG,"lineNo = " + i.getLine() + ", sec = " + i.getSection() + ", dept = " + i.getProblem().getDepartment());

                            if (lineNo == null || secSelected == null || deptSelected == null || lineNo == 0 || secSelected.contains("All") ||
                                    deptSelected.contains("All") || i.getLine().equals(lineNo) || i.getSection().equalsIgnoreCase(secSelected) ||
                                    i.getProblem().getDepartment().equalsIgnoreCase(deptSelected)) {

                                if (issue.getFixAt() == null && issue.getAckAt() == null) {
                                    Log.i(TAG, "Adapter : add Issue2");
                                    rvAdapter.insert(getProblem(issue));
                                } else {
                                    Log.i(TAG, "Adapter : update Issue2");
                                    rvAdapter.update(getProblem(issue));
                                }
                            }
                        }

                    }
                    syncPref.edit().putLong(Constants.LAST_ISSUE1_SYNC, syncTime).commit();
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

        String url = Constants.API1_BASE_URL + "/issues?start=" + syncTime;
        restUtility.get(url,listener,errorListener1);
    }

    private Issue1 getIssueEntity(JSONObject i) {
        Issue1 mIssue = null;
        try {
            mIssue = new Issue1(
                    i.getLong("id"),
                    i.getInt("line"),
                    i.getString("section"),
                    i.getLong("problemId"),
                    i.getString("critical"),
                    i.getString("operatorNo"),
                    i.getString("description"),
                    i.getLong("raisedBy"),
                    new Date(i.getLong("raisedAt")),
                    i.getInt("processingAt"),
//                    i.getInt("seekHelp"),
                    null,
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
        userService = new UserService((App)getApplication());
        progress.setVisibility(View.VISIBLE);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "users Response :" + response.toString());
                try {
                    JSONArray jsonUsers = response.getJSONArray("users");
                    Long userSync = response.getLong("userSync");
                    List<User> users = new ArrayList<>();
                    JSONObject u;
                    User user;
                    for (int i = 0; i < jsonUsers.length(); i++) {

                        u = jsonUsers.getJSONObject(i);
                        user = new User(
                                u.getLong("id"),
                                u.getString("name"),
                                u.getString("email"),
                                u.getString("mobile"),
                                u.getString("role"),
                                u.getString("userType"),
                                u.getString("level")
                        );

                        if (! u.getString("desgnId").equals("null")) {
                            user.setDesgnId(u.getLong("desgnId"));
                        }
                        users.add(user);

                    }
                    userService.saveOrUpdateBatch(users);
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
        if(!level.equalsIgnoreCase(Constants.USER_LEVEL0)){
            Menu menu = navigationView.getMenu();
            menu.removeItem(R.id.nav_stylechangeover);
        }

//        //Filters
        int noOfLines = Constants.NO_OF_LINES;
        String[] lineArray = new String[noOfLines+1];
        lineArray[0] = "All Lines";
        for(int i = 1; i < lineArray.length; i++){
            lineArray[i] = "Line " + i;
        }
        /*//// Line Filter //////*/
        ArrayAdapter<String> lineAdapter = new ArrayAdapter<>(this,R.layout.spinner_list_item,R.id.spinner_item,lineArray);
        lineAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        line.setAdapter(lineAdapter);
        /*//// Section Filter //////*/
        String[] sections = appPref.getString(Constants.APP_SECTIONS, "").split(";");
        final List<String> sectionList = new ArrayList<>();
        sectionList.add("All Sections");
        for (String s: sections) {
            sectionList.add(s);
        }
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sectionList);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        section.setAdapter(sectionAdapter);

        /*//// Department Filter //////*/
        String[] departments = appPref.getString(Constants.APP_DEPARTMENTS, "").split(";");
        final List<String> deptList = new ArrayList<>();
        deptList.add("All Departments");
        for (String d: departments) {
            deptList.add(d);
        }
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, deptList);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dept.setAdapter(deptAdapter);

        line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"onItemSelect() : line");
                if(line == null || view == null){
                    return;
                }

                String lineStr = parent.getItemAtPosition(position).toString();
                if(lineStr.contains("All")){
                    lineNo = null;
                }else{
                    lineNo = Integer.parseInt(lineStr.split(" ")[1]);
                }
                showIssues();
//                if(flagLine == false){
//                    flagLine = true;
//                }else {
//                    //showIssues();
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"onItemSelect() : section");
                if(section == null || view == null){
                    return;
                }
                secSelected = parent.getItemAtPosition(position).toString();
                if(secSelected.contains("All")){
                    secSelected = null;
                }
                showIssues();
                
//                if(flagSection == false){
//                    flagSection = true;
//                }else {
//                    //showIssues();
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"onItemSelect() : department");
                if(dept == null || view == null){
                    return;
                }
                deptSelected = parent.getItemAtPosition(position).toString();
                if(deptSelected.contains("All")){
                    deptSelected = null;
                }
                showIssues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Swipe Refresh
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                syncIssues();
            }
        });
        //showIssues();


    }

    private void initializeApp(){
        AlertDialog dialog = getAlertDialog();
        Boolean isConnected = MiscUtil.isConnectedToInternet(this);
        if (!isConnected) {
            dialog.show();
        }else {
            final ProblemService problemService = new ProblemService((App)getApplication());
            final DesignationService designationService = new DesignationService((App)getApplication());
            final ProblemDesignationService dpService = new ProblemDesignationService((App)getApplication());
            issueService.deleteAll();
            dpService.deleteAll();
            problemService.deleteAll();
            designationService.deleteAll();
            userService.deleteAll();
            syncPref.edit()
                    .putLong(Constants.LAST_USER_SYNC,0)
                    .putLong(Constants.LAST_ISSUE1_SYNC,0)
                    .apply();


            Response.Listener<JSONArray> listenerSection = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i(TAG, "section Response :" + response.toString());

                    String[] section = new String[response.length()];
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            section[i] = response.getString(i);
                        }
                        StringBuilder builder = new StringBuilder();
                        for (String t : section) {
                            builder.append(t + ";");
                        }
                        builder.setLength(builder.length() - 1);
                        appPref.edit().putString(Constants.APP_SECTIONS, builder.toString()).commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    noOfRequests++;
                    if (noOfRequests == 4){
                        appInitComplete();
                    }
                }
            };
            Response.Listener<JSONArray> listenerDepartment = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i(TAG, "Department Response :" + response.toString());

                    String[] departments = new String[response.length()];
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            departments[i] = response.getString(i);
                        }
                        StringBuilder builder = new StringBuilder();
                        for (String t : departments) {
                            builder.append(t + ";");
                        }
                        builder.setLength(builder.length() - 1);
                        appPref.edit().putString(Constants.APP_DEPARTMENTS, builder.toString()).commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    noOfRequests++;
                    if (noOfRequests == 4){
                        appInitComplete();
                    }
                }
            };
            Response.Listener<JSONArray> listenerProblem = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i(TAG, "problem Response :" + response.toString());
                    try {
                        List<in.andonsystem.entity.Problem> problems = new ArrayList<>();
                        JSONObject p, d;
                        JSONArray desgns;
                        Long probId;
                        List<ProblemDesignation> dpList;
                        for (int i = 0; i < response.length(); i++) {
                            p = response.getJSONObject(i);
                            probId = p.getLong("id");
                            if (problemService.exists(probId)) {
                                dpService.deleteByProblem(probId);
                            }
                            problemService.saveOrUpdate(new in.andonsystem.entity.Problem(p.getLong("id"), p.getString("name"), p.getString("department")));

                            desgns = p.getJSONArray("designations");
                            Log.d(TAG, "prob = " + p.getString("name") + ", designations size = " + desgns.length());
                            if (desgns.length() > 0) {

                                //Debugging
                                if (probId == 11) {
                                    Log.d(TAG,"no of designation = " + p.getJSONArray("designations"));
                                }
                                dpList = new ArrayList<>();
                                for (int j = 0; j < desgns.length(); j++){
                                    d = desgns.getJSONObject(j);
                                    dpList.add(new ProblemDesignation(null,p.getLong("id"), d.getLong("id")));
                                }
                                dpService.saveBatch(dpList);
                            }
                        }
                        problemService.saveAll(problems);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    noOfRequests++;
                    if (noOfRequests == 4){
                        appInitComplete();
                    }
                }
            };
            Response.Listener<JSONArray> listenerDesignation = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.i(TAG, "designation Response :" + response.toString());
                    try {
                        List<Designation> designations = new ArrayList<>();
                        JSONObject obj;
                        for (int i = 0; i < response.length(); i++) {
                            obj = response.getJSONObject(i);
                            designations.add(new Designation(obj.getLong("id"), obj.getString("name"), obj.getString("lines"), obj.getInt("level")));
                        }

                        designationService.saveAll(designations);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    noOfRequests++;
                    if (noOfRequests == 4){
                        appInitComplete();
                    }
                }
            };

            String urlSection = Constants.API1_BASE_URL + "/sections";
            String urldepartment = Constants.API1_BASE_URL + "/departments";
            String urlProblem = Constants.API1_BASE_URL + "/problems";
            String urlDesignation = Constants.API1_BASE_URL + "/designations";
            restUtility.getJsonArray(urlSection, listenerSection, errorListener);
            restUtility.getJsonArray(urldepartment, listenerDepartment, errorListener);
            restUtility.getJsonArray(urlProblem, listenerProblem, errorListener);
            restUtility.getJsonArray(urlDesignation, listenerDesignation, errorListener);

        }
    }

    private void appInitComplete(){
        Log.d(TAG,"appInitComplete");
        appPref.edit()
                .putBoolean(Constants.APP1_FIRST_LAUNCH,false)
                .putInt(Constants.LAST_APP_USED,1)
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
