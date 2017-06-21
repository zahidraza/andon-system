package in.andonsystem.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.andonsystem.App;
import in.andonsystem.R;
import in.andonsystem.adapter.AdapterContact;
import in.andonsystem.dto.Contact;
import in.andonsystem.entity.User;
import in.andonsystem.service.UserService;
import in.andonsystem.Constants;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;
import in.andonsystem.view.DividerItemDecoration;

public class ContactActivity extends AppCompatActivity {

    private final String TAG = ContactActivity.class.getSimpleName();

    private Context mContext;
    private App app;
    private RestUtility restUtility;
    private UserService userService;
    private SharedPreferences userPref;
    private SharedPreferences syncPref;
    private User user;
    private AdapterContact adapterContact;
    private boolean rvAdded = false;

    private RelativeLayout container;
    private RecyclerView recyclerView;
    private TextView emptyMessage;
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "056dd13f");
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        app = (App) getApplication();
        userService = new UserService(app);
        userPref = getSharedPreferences(Constants.USER_PREF,0);
        syncPref = getSharedPreferences(Constants.SYNC_PREF,0);
        container = (RelativeLayout) findViewById(R.id.content_contact_layout);
        progress = (ProgressBar) findViewById(R.id.loading_progress);

        String email = userPref.getString(Constants.USER_EMAIL,"");
        if (email == "") {
            Log.d(TAG, "User email  not found in userPref file. handle inconsistency");
        }
        user = userService.findByEmail(email);
        restUtility = new RestUtility(this){
            @Override
            protected void handleInternetConnRetry() {
                syncUsers();
            }
        };
        prepareScreen();
        syncUsers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showContacts();
    }

    private void prepareScreen(){
        Log.d(TAG,"prepareScreen");
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(param1);
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext,R.drawable.divider));

        emptyMessage = new TextView(this);
        emptyMessage.setLayoutParams(param1);
        emptyMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyMessage.setTextColor(ContextCompat.getColor(this, R.color.limeGreen));
        emptyMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        emptyMessage.setText("No Contacts found");

    }

    private void showContacts(){
        List<Contact> contacts = new ArrayList<>();
        if (user.getUserType().equalsIgnoreCase(Constants.USER_FACTORY)){
            List<User> users = userService.findAllFactory(Constants.USER_FACTORY);
            for (User user: users){
                contacts.add(new Contact(user.getName(), "+91 " + user.getMobile()));
            }
        }else {
            List<User> users = userService.findAllCity(Constants.USER_FACTORY);
            for (User user: users){
                contacts.add(new Contact(user.getName(),"+91 " + user.getMobile()));
            }
        }
        if(contacts.size() > 0){
            adapterContact = new AdapterContact(this, contacts);
            if(rvAdded){
                recyclerView.swapAdapter(adapterContact, false);
            }else {
                container.removeAllViews();
                container.addView(recyclerView);
                recyclerView.setAdapter(adapterContact);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                rvAdded = true;
            }
        }else {
            container.removeAllViews();
            container.addView(emptyMessage);
            rvAdded =false;
        }
    }

    private void syncUsers() {
        final UserService userService = new UserService((App)getApplication());
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
                    syncPref.edit().putLong(Constants.LAST_USER_SYNC, userSync).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progress.setVisibility(View.INVISIBLE);
            }
        };
        ErrorListener errorListener = new ErrorListener(mContext) {
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
        long lastUserSync = syncPref.getLong(Constants.LAST_USER_SYNC,0);
        String url = Constants.API2_BASE_URL + "/users?after=" + lastUserSync;
        restUtility.get(url, listener, errorListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        progress.setVisibility(View.INVISIBLE);
    }

}
