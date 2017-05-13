package in.andonsystem.v2.activity;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.AppController;
import in.andonsystem.R;
import in.andonsystem.v2.adapter.AdapterContact;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.entity.UserBuyer;
import in.andonsystem.v2.service.UserBuyerService;
import in.andonsystem.v2.service.UserService;
import in.andonsystem.v2.util.Constants;
import in.andonsystem.v2.view.DividerItemDecoration;

public class ContactActivity extends AppCompatActivity {

    private final String TAG = ContactActivity.class.getSimpleName();

    private Context mContext;
    private App app;
    private UserService userService;
    private UserBuyerService userBuyerService;
    private SharedPreferences userPref;
    private SharedPreferences syncPref;
    private User user;
    private AdapterContact adapterContact;
    private boolean rvAdded = false;

    private RelativeLayout container;
    private RecyclerView recyclerView;
    private TextView emptyMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppClose.activity4 = this;
        mContext = this;
        app = (App) getApplication();
        userService = new UserService(app);
        userBuyerService = new UserBuyerService(app);
        userPref = getSharedPreferences(Constants.USER_PREF,0);
        syncPref = getSharedPreferences(Constants.SYNC_PREF,0);
        container = (RelativeLayout) findViewById(R.id.content_contact_layout);

        String email = userPref.getString(Constants.USER_EMAIL,"");
        if (email == "") {
            Log.d(TAG, "User email  not found in userPref file. handle inconsistency");
        }
        user = userService.findByEmail(email);
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
        List<String> contacts = new ArrayList<>();
        if (!user.getUserType().equalsIgnoreCase(Constants.USER_FACTORY)){
            List<User> users = userService.findAll();
            for (User user: users){
                contacts.add(user.getName() + "\n +91 " + user.getMobile());
            }
        }else {

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

    private void syncUsers(){
        Log.d(TAG,"syncUsers()");
        final Long lastSync = syncPref.getLong(Constants.LAST_USER_SYNC,0L);
        String url4 = Constants.API2_BASE_URL + "/users?after=" + lastSync;
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
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
                showContacts();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getMessage());
            }
        };
        JsonObjectRequest request4 = new JsonObjectRequest(Request.Method.GET, url4, null, listener, errorListener);
        request4.setTag(TAG);
        AppController.getInstance().addToRequestQueue(request4);
    }


}
