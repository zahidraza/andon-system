package in.andonsystem.v2.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OnAccountsUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.AppController;
import in.andonsystem.LoadingActivity;
import in.andonsystem.R;
import in.andonsystem.entity.Issue2;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;
import in.andonsystem.adapter.AdapterHome;
import in.andonsystem.v2.authenticator.AuthConstants;
import in.andonsystem.dto.Problem;
import in.andonsystem.entity.User;
import in.andonsystem.entity.UserBuyer;
import in.andonsystem.service.IssueService2;
import in.andonsystem.service.UserBuyerService;
import in.andonsystem.service.UserService;
import in.andonsystem.Constants;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = HomeActivity.class.getSimpleName();
    private final long ACCOUNT_ADD = 100L;
    private final long ACCOUNT_MANAGE = 101L;

    /*View Components*/
    private RelativeLayout container;
    private SwipeRefreshLayout refreshLayout2;
    private RecyclerView recyclerView;
    private TextView emptyMessage;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private boolean rvAdded;  //Recycler view added?

    /*App2 specific*/
    private Spinner teamFilter;
    private String selectedTeam = "All Team";
    AdapterHome rvAdapter2;

    /*App Variables*/
    private int appNo;
    private int accountSelected = -1;
    private Context mContext;
    private App app;
    private IssueService2 issueService2;
    private UserService userService;
    private UserBuyerService userBuyerService;
    private SharedPreferences syncPref;
    private SharedPreferences userPref;
    private SharedPreferences appPref;
    private DateFormat df;

    private AccountManager mAccountManager;
    private Map<Integer,String> addedAccounts = new HashMap<>();  /*Accounts added in account header*/
    private AccountHeader accountHeader;
    private Drawer drawer;
    private int accountId = 0;  /* identifier (incremented) for account added to account header*/
    private boolean removedLoggedUser = false;  //Whether removed logged in user

    private List<String> andonAccounts = new ArrayList<>();
    private OnAccountsUpdateListener accountsUpdateListener = new OnAccountsUpdateListener() {
        @Override
        public void onAccountsUpdated(Account[] accounts) {
            Log.d(TAG,"account Updated");
            andonAccounts.clear();
            //Filter andon accounts
            for(Account account: accounts) {
                if (account.type.equalsIgnoreCase(AuthConstants.VALUE_ACCOUNT_TYPE)) {
                    andonAccounts.add(account.name);
                }
            }
            //Check if account is Removed
            String email = userPref.getString(Constants.USER_EMAIL,"");
            StringBuilder removedIds = new StringBuilder(" ");
            for (Map.Entry<Integer,String> entry: addedAccounts.entrySet()){
                if (!andonAccounts.contains(entry.getValue())){
                    Log.d(TAG,"removed account" + entry.getValue());
                    if (email.equalsIgnoreCase(entry.getValue())){
                        removedLoggedUser = true;
                    }
                    accountHeader.removeProfileByIdentifier(entry.getKey());
                    removedIds.append(String.valueOf(entry.getKey()) + ",");
                }
            }
            removedIds.setLength(removedIds.length()-1);
            String[] ids = removedIds.toString().split(",");
            for (String s: ids){
                if (!TextUtils.isEmpty(s.trim())) {
                    addedAccounts.remove(Integer.parseInt(s.trim()));
                }
            }
            //Check if account is Added
            Collection<String> aCollection = addedAccounts.values();
            for (String account: andonAccounts){
                if (! aCollection.contains(account)){
                    Log.d(TAG,"added account" + account);
                    accountHeader.addProfile(new ProfileDrawerItem().withEmail(account).withIcon(getResources().getDrawable(R.drawable.profile3)).withIdentifier(accountId),
                            accountHeader.getProfiles().size() - 2);
                    addedAccounts.put(accountId++,account);
                    //When first account is added
                    if (addedAccounts.size() == 1) {
                        accountSelected = (accountId -1);
                        processUserType(account);
                    }
                }
            }

            if (removedLoggedUser) {
                if (addedAccounts.size() > 0){
                    Map.Entry<Integer,String> firstEntry = addedAccounts.entrySet().iterator().next();
                    accountSelected = firstEntry.getKey();
                    processUserType(firstEntry.getValue());
                }else {
                    userPref.edit().putString(Constants.USER_ACCESS_TOKEN,null).commit();
                    accountSelected = -1;
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppClose.activity3 = this;

        mContext = this;
        app = (App)getApplication();
        mAccountManager = AccountManager.get(this);
        mAccountManager.addOnAccountsUpdatedListener(accountsUpdateListener, new Handler(),true);
        issueService2 = new IssueService2(app);
        userService = new UserService(app);
        userBuyerService = new UserBuyerService(app);
        syncPref = getSharedPreferences(Constants.SYNC_PREF,0);
        userPref = getSharedPreferences(Constants.USER_PREF,0);
        appPref = getSharedPreferences(Constants.APP_PREF,0);
        df = new SimpleDateFormat("dd MMM, hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, RaiseIssueActivity2.class);
                startActivity(i);
            }
        });
        container = (RelativeLayout) findViewById(R.id.content_home);
        prepareScreen();
        buildAccountHeader();
        buildDrawer();


        Account[] accounts = mAccountManager.getAccountsByType(AuthConstants.VALUE_ACCOUNT_TYPE);
        if(accounts.length == 0){
            getTokenForAccountCreateIfNeeded();
        }else {
            String email = userPref.getString(Constants.USER_EMAIL, null);
            if (email == null) {
                email = accounts[0].name;
                userPref.edit().putString(Constants.USER_EMAIL,email).commit();
            }

            ProfileDrawerItem profile;
            accountSelected = 0;
            for(Account a: accounts){
                if(email.equals(a.name)){
                    addedAccounts.put(accountId++,a.name);
                    break;
                }
            }
            for(Account a: accounts){
                if(!email.equals(a.name)){
                    addedAccounts.put(accountId++,a.name);
                    break;
                }
            }
            for (int i = 0; i < accountId; i++){
                profile = new ProfileDrawerItem().withEmail(addedAccounts.get(i)).withIcon(getResources().getDrawable(R.drawable.profile3)).withIdentifier(i);
                accountHeader.addProfile(profile, accountHeader.getProfiles().size() - 2);
            }
//            for (Map.Entry<Integer,String> e: addedAccounts.entrySet()){
//                profile = new ProfileDrawerItem().withEmail(e.getValue()).withIcon(getResources().getDrawable(R.drawable.profile3)).withIdentifier(e.getKey());
//                accountHeader.addProfile(profile, accountHeader.getProfiles().size() - 2);
//            }

            User user = userService.findByEmail(email);
            if (user == null) {
                syncUsers();
            }else {
                chooseScreen(user);
                onAccountChange();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showIssues();
        syncIssues();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(drawer.isDrawerOpen()){
            drawer.closeDrawer();
        }else {
            AppClose.close();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            syncIssues();
        }
        if (id == R.id.action_notification) {
            Intent i = new Intent(this, NotificationActivity2.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAccountManager.removeOnAccountsUpdatedListener(accountsUpdateListener);
    }

    private void showIssues() {
        Log.i(TAG, "showIssues(), appNo = " + appNo + ", selected team =" + selectedTeam);
        if(appNo == 2){
            TreeSet<Problem> issues = getIssue2();
            if(issues.size() > 0){
                rvAdapter2 = new AdapterHome(this, issues);
                if(rvAdded){
                    recyclerView.swapAdapter(rvAdapter2, false);
                }else {
                    container.removeView(emptyMessage);
                    container.addView(refreshLayout2);
                    refreshLayout2.addView(recyclerView);
                    recyclerView.setAdapter(rvAdapter2);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    rvAdded = true;
                }
            }else {
                if(rvAdded){
                    container.removeView(refreshLayout2);
                    refreshLayout2.removeView(recyclerView);
                }
                container.removeView(emptyMessage);
                container.addView(emptyMessage);
                rvAdded =false;
            }
        }
    }

    private void syncIssues(){
        Log.d(TAG, "synchIssues(): appNo = " + appNo);
        RestUtility restUtility = new RestUtility(this);

        if(appNo == 2) {
            refreshLayout2.setRefreshing(true);
            String url = Constants.API2_BASE_URL + "/issues?start=" + syncPref.getLong(Constants.LAST_ISSUE2_SYNC, 0);
            Log.i(TAG, "url = " + url);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "Issue2 Response :" + response.toString());
                    Long syncTime;

                    try {
                        syncTime = response.getLong("issueSync");
                        JSONArray issues = response.getJSONArray("issues");
                        if (issues.length() > 0) {
                            if(!rvAdded){
                                container.addView(refreshLayout2);
                                refreshLayout2.addView(recyclerView);
                                TreeSet<Problem> issue = new TreeSet<>();
                                rvAdapter2 = new AdapterHome(mContext, issue);
                                recyclerView.setAdapter(rvAdapter2);
                                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                                rvAdded = true;
                            }
                            /*Save or Update Issues in database*/
                            List<Issue2> issue2List = new ArrayList<>();
                            for (int i = 0; i < issues.length(); i++) {
                                issue2List.add(getIssue(issues.getJSONObject(i)));
                            }
                            issueService2.saveOrUpdate(issue2List);

                            for (Issue2 issue : issue2List) {

                                if (true) {      //If Issue2 belongs to applied filter then add or update rvAdapter

                                    if (issue.getFixAt() == null && issue.getAckAt() == null) {
                                        Log.i(TAG, "Adapter : add Issue2");
                                        rvAdapter2.insert(getProblem(issue));
                                    } else {
                                        Log.i(TAG, "Adapter : update Issue2");
                                        rvAdapter2.update(getProblem(issue));
                                    }
                                }
                            }

                        }
                        syncPref.edit().putLong(Constants.LAST_ISSUE2_SYNC, syncTime).commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    refreshLayout2.setRefreshing(false);
                }
            };
            ErrorListener errorListener = new ErrorListener(this) {
                @Override
                protected void handleTokenExpiry() {
                    syncIssues();
                }
            };
//            Response.ErrorListener errorListener = new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e(TAG, error.toString());
//                    NetworkResponse resp = error.networkResponse;
////                    String data = new String(resp.data);
////                    Log.i(TAG, "response status: " + data);
//                    if (resp != null && resp.statusCode == 401) {
//                        invalidateAccessToken();
//                        getAuthToken();
//                    } else {
//                        Toast.makeText(mContext, "Unable to Sync. Check your Internet Connection.", Toast.LENGTH_SHORT).show();
//                    }
//                    refreshLayout2.setRefreshing(false);
//                }
//            };
//
//            String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN, null);
//            if (accessToken == null) {
//                if(accountSelected != -1) {
//                    getAuthToken();
//                }else if (removedLoggedUser) {
//                    removedLoggedUser = false;
//                    getTokenForAccountCreateIfNeeded();
//                }
//                return;
//            }
//
//            MyJsonObjectRequest request = new MyJsonObjectRequest(Request.Method.GET, url, null, listener, errorListener, accessToken);
//            request.setTag(TAG);
//            AppController.getInstance().addToRequestQueue(request);
            restUtility.get(url,listener,errorListener);
        }
    }

    private TreeSet<Problem> getIssue2(){
        Log.d(TAG,"getIssue2");
        DateFormat df = new SimpleDateFormat("hh:mm aa");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        TreeSet<Problem> issues = new TreeSet<>();
        List<Issue2> list;
        if(selectedTeam.contains("All Team")){
            list = issueService2.findAll();
        }else{
            list = issueService2.findAllByTeam(selectedTeam);
        }
        Log.d(TAG, "Issue2 size = " + list.size());
        for (Issue2 i : list) {
            issues.add(getProblem(i));
        }
        return issues;
    }

    private Problem getProblem(Issue2 issue2){
        String raiseTime = df.format(issue2.getRaisedAt());
        long downtime = (issue2.getFixAt() != null) ? (issue2.getFixAt().getTime() - issue2.getRaisedAt().getTime() ): -1L;
        int flag = (issue2.getFixAt() != null) ? 2 : ( (issue2.getAckAt() != null) ? 1: 0);
        return new Problem(issue2.getId(), issue2.getBuyer().getTeam(), issue2.getBuyer().getName(), issue2.getProblem(),raiseTime,downtime,flag);
    }

    private Issue2 getIssue(JSONObject i) {
        Issue2 mIssue2 = null;
        try {
            mIssue2 = new Issue2(i.getLong("id"),i.getLong("buyerId"), i.getString("problem"), i.getString("description"), new Date(i.getLong("raisedAt")), null, null, i.getInt("processingAt"));
            mIssue2.setRaisedBy(i.getLong("raisedBy"));

            if (! i.getString("ackBy").equals("null")) {
                mIssue2.setAckBy(i.getLong("ackBy"));
            }
            if (! i.getString("ackAt").equals("null")) {
                mIssue2.setAckAt(new Date(i.getLong("ackAt")));
            }
            if (! i.getString("fixBy").equals("null")) {
                mIssue2.setFixBy(i.getLong("fixBy"));
            }
            if (! i.getString("fixAt").equals("null")) {
                mIssue2.setFixAt(new Date(i.getLong("fixAt")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIssue2;
    }

    private void prepareScreen(){
        Log.d(TAG,"prepareScreen");
        RelativeLayout.LayoutParams param0 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        RecyclerView.LayoutParams param2 = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );

        teamFilter = new Spinner(this);
        teamFilter.setLayoutParams(param0);
        teamFilter.setId(R.id.home_team_filter);

        refreshLayout2 = new SwipeRefreshLayout(this);
        param1.addRule(RelativeLayout.BELOW, R.id.home_team_filter);
        refreshLayout2.setLayoutParams(param1);

        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(param2);

        emptyMessage = new TextView(this);
        emptyMessage.setLayoutParams(param1);
        emptyMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyMessage.setTextColor(ContextCompat.getColor(this, R.color.limeGreen));
        emptyMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        emptyMessage.setText("No Open Issues Found.");

        //Set adapter for Team Filter
        String[] teams = appPref.getString(Constants.APP_TEAMS, "").split(";");
        final List<String> teamList = new ArrayList<>();
        teamList.add("All Team");
        for (String t: teams) {
            teamList.add(t);
        }
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teamList);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamFilter.setAdapter(teamAdapter);

        teamFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemSelect()");
                selectedTeam = teamList.get(position);
                showIssues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        refreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                syncIssues();
            }
        });
    }

    private void buildAccountHeader(){
        Log.d(TAG,"buildAccountHeader");
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(getResources().getDrawable(R.drawable.header))
                .addProfiles(
                        new ProfileSettingDrawerItem().withName("Add Account").withIcon(android.R.drawable.ic_input_add).withIdentifier(ACCOUNT_ADD),
                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(android.R.drawable.ic_menu_preferences).withIdentifier(ACCOUNT_MANAGE)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if (profile instanceof IDrawerItem) {
                            if(((IDrawerItem) profile).getIdentifier() == ACCOUNT_ADD){
                                Log.d(TAG, "addAccount");
                                addAccount();
                            }else if(((IDrawerItem) profile).getIdentifier() == ACCOUNT_MANAGE){
                                Log.d(TAG, "Manage Account");
                                Intent i = new Intent(Settings.ACTION_SYNC_SETTINGS);
                                startActivity(i);
                            }else {
                                int profileId = (int) ((IDrawerItem) profile).getIdentifier();
                                if (profileId != accountSelected && profileId < 100) {
                                    String user = addedAccounts.get(profileId);
                                    processUserType(user);
                                    accountSelected = profileId;
                                }
                            }
                        }
                        return false;
                    }
                })
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        Log.d(TAG, "onProfileImageClick");
                        if (profile instanceof IDrawerItem) {
                            int profileId = (int)((IDrawerItem) profile).getIdentifier();
                            if(profileId == accountSelected){
                                //start profile activity
                                Intent i = new Intent(mContext, ProfileActivity.class);
                                startActivity(i);
                            }
                            else if( profileId != accountSelected && profileId < 100){
                                //check usertype and thereby render appropriate home screen
                                String user = addedAccounts.get(profileId);
                                processUserType(user);
                                accountSelected = profileId;
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();

    }

    private void buildDrawer(){
        Log.d(TAG,"buildDrawer");
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSliderBackgroundColor(getResources().getColor(R.color.slide_background))
                .withDisplayBelowStatusBar(true)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Report")
                                .withIcon(getResources().getDrawable(R.drawable.ic_show_chart_white_36dp))
                                .withSelectedColor(getResources().getColor(R.color.slide_background))
                                .withTextColor(getResources().getColor(R.color.white))
                                .withSelectedTextColor(getResources().getColor(R.color.white))
                                .withIdentifier(1),
                        new PrimaryDrawerItem().withName("Contacts")
                                .withIcon(getResources().getDrawable(R.drawable.ic_contact_phone_white_36dp))
                                .withSelectedColor(getResources().getColor(R.color.slide_background))
                                .withTextColor(getResources().getColor(R.color.white))
                                .withSelectedTextColor(getResources().getColor(R.color.white))
                                .withIdentifier(2)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            Log.d(TAG, ((Nameable) drawerItem).getName().getText(mContext));
                            String selected = ((Nameable) drawerItem).getName().getText(mContext);
                            Intent i = null;
                            if (selected.equals("Report")) {
                                i = new Intent(mContext, ReportActivity.class);
                            }else if (selected.equals("Contacts")) {
                                i = new Intent(mContext, ContactActivity.class);
                            }
                            startActivity(i);
                        }
                        return false;
                    }
                })
                .build();
    }

    /**
     * If acoount is changed re-render entire view
     * */
    private void onAccountChange(){
        Log.d(TAG,"onAccountChange");
        refreshLayout2.removeView(recyclerView);
        container.removeAllViews();
        rvAdded = false;
        if(appNo == 2){
            container.addView(teamFilter);
            showIssues();
        }else {
            //Add app1 filters
        }
    }

    private void getAuthToken(){
        Log.d(TAG,"getAuthToken");
        Account[] accounts = mAccountManager.getAccountsByType(AuthConstants.VALUE_ACCOUNT_TYPE);
        String email = userPref.getString(Constants.USER_EMAIL, null);
        Account account = null;
        for (Account a: accounts){
            if(a.name.equals(email)){
                account = a;
                break;
            }
        }
        if (account == null) {
            if (accounts.length != 0){
                account = accounts[0];
            }else {
                getTokenForAccountCreateIfNeeded();
                return;
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
                    syncIssues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addAccount(){
        Log.i(TAG, "addAccount");
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(AuthConstants.VALUE_ACCOUNT_TYPE, AuthConstants.AUTH_TOKEN_TYPE_FULL_ACCESS, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    //String username = bnd.getString(AccountManager.KEY_ACCOUNT_NAME);
                    //updateAccountHeader(username);
                } catch (Exception e) {
                    e.printStackTrace();
                    //showMessage(e.getMessage());
                }
            }
        }, null);
    }

    /**
     * Call when Account is changed.
     * 1. Get user from database.
     * 2. Find UserType
     * 3. If UserType == Factory && appNo == 2, show factory screen
     *      else if (userType == Non-Factory && appNo == 1, show City screen
     * 4. update user details in userPref including new authToken
     * @param email
     */
    private void processUserType(String email){
        Log.d(TAG,"processUserType: email = " + email);
        userPref.edit()
                .putString(Constants.USER_EMAIL,email)
                .putString(Constants.USER_ACCESS_TOKEN, null)
                .commit();

        User user = userService.findByEmail(email);
        if(user == null){
            syncUsers();
        }else {
            chooseScreen(user);
            onAccountChange();
        }
    }

    /**
     * Decide which app to display (Factory or City) and also decide if fab is to displayed
     * @param user
     */
    private void chooseScreen(User user){
        Log.d(TAG,"chooseScreen: userType = " + user.getUserType() + ",level=" + user.getLevel());
        if(user.getUserType().equalsIgnoreCase(Constants.USER_FACTORY)){
            appNo = 1;
        }else if(user.getUserType().equalsIgnoreCase(Constants.USER_SAMPLING)){
            appNo = 2;
            if (user.getLevel().equalsIgnoreCase(Constants.USER_LEVEL4)){
                showFab(false);
            }else {
                showFab(true);
            }
        }else {
            appNo = 2;
            showFab(false);
        }

    }

    private void showFab(boolean value){
        Log.d(TAG, "showFab: value = " + value);
        if(value){
            fab.show();
        }else {
            fab.hide();
        }
    }

    private void syncUsers(){
        Log.d(TAG,"syncUsers()");
        final Long lastSync = syncPref.getLong(Constants.LAST_USER_SYNC,0L);
        String url4 = Constants.API2_BASE_URL + "/users?after=" + lastSync;
        Response.Listener<JSONObject> listener4 = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "users Response :" + response.toString());
                try {
                    JSONArray jsonUsers = response.getJSONArray("users");
                    Long userSync = response.getLong("userSync");
                    List<User> users = new ArrayList<>();
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
                    syncPref.edit().putLong(Constants.LAST_USER_SYNC, userSync).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String email = userPref.getString(Constants.USER_EMAIL, null);
                User user = userService.findByEmail(email);
                if (user == null) {
                    appPref.edit().putBoolean(Constants.APP1_FIRST_LAUNCH, true).commit();
                    Intent i = new Intent(mContext, LoadingActivity.class);
                    startActivity(i);
                }
                chooseScreen(user);
                onAccountChange();
            }
        };
        Response.ErrorListener errorListener4 = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getMessage());
            }
        };
        JsonObjectRequest request4 = new JsonObjectRequest(Request.Method.GET, url4, null, listener4, errorListener4);
        request4.setTag(TAG);
        AppController.getInstance().addToRequestQueue(request4);
    }

    private void getTokenForAccountCreateIfNeeded() {
        Log.d(TAG, "getTokenForAccountCreateIfNeeded");
        AccountManager mAccountManager = AccountManager.get(this);
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(AuthConstants.VALUE_ACCOUNT_TYPE, AuthConstants.AUTH_TOKEN_TYPE_FULL_ACCESS, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            Log.d(TAG, "bundle: " + bnd);
                            String username = bnd.getString(AccountManager.KEY_ACCOUNT_NAME);
                            String authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.putString(Constants.USER_EMAIL,username);
                            editor.putString(Constants.USER_ACCESS_TOKEN,authToken);
                            editor.putBoolean(Constants.IS_LOGGED_IN, true);
                            editor.commit();
                            //updateAppNo(username);
//                            updateAccountHeader(username);
                            onStart();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //showMessage(e.getMessage());
                        }
                    }
                }
                , null);
    }

    private void invalidateAccessToken(){
        Log.d(TAG,"invalidateAccessToken");
        String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN,null);
        mAccountManager.invalidateAuthToken(AuthConstants.VALUE_ACCOUNT_TYPE,accessToken);
    }


}
