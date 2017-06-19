package in.andonsystem.activity.v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import in.andonsystem.Constants;
import in.andonsystem.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;

import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;


public class StyleChangeOverActivity extends AppCompatActivity {

    private final String TAG = StyleChangeOverActivity.class.getSimpleName();
    private Context context;
    private SharedPreferences userPref;
    private Spinner line;
    private EditText from;
    private EditText to;
    private EditText remarks;

    private String fromStr,toStr,remarksStr,lineStr;

    private ProgressDialog pDialog;
    private String username;
    private RestUtility restUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "544df31b");
        setContentView(R.layout.activity_style_change_over);
        Log.i(TAG,"onCreate()");
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //View mapping
        line = (Spinner)findViewById(R.id.style_lines);
        from = (EditText)findViewById(R.id.style_from);
        to = (EditText)findViewById(R.id.style_to);
        remarks = (EditText)findViewById(R.id.style_remarks);

        userPref = getSharedPreferences(Constants.USER_PREF,0);
        username = userPref.getString(Constants.USER_NAME,"");
        restUtility = new RestUtility(context);


        int noOfLines = Constants.NO_OF_LINES;
        String[] lineArray = new String[noOfLines];
        for(int i = 0; i < lineArray.length; i++){
            lineArray[i] = "Line " + (i+1);
        }

        ArrayAdapter<String> lineAdapter = new ArrayAdapter<>(this,R.layout.spinner_list_item,R.id.spinner_item,lineArray);
        lineAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        line.setAdapter(lineAdapter);
    }

    public void submit(View view){
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");

        lineStr = ((TextView)line.findViewById(R.id.spinner_item)).getText().toString();
        toStr = to.getText().toString();
        fromStr = from.getText().toString();
        remarksStr = remarks.getText().toString();

        if(fromStr.equals("") || toStr.equals("") || remarksStr.equals("")){
            Toast.makeText(context,"Fields cannot be blank",Toast.LENGTH_SHORT).show();
        }else{
            pDialog.show();

            String url = "http://andonsystem.in/restapi/style_changeover"; //TODO: change url

            JSONObject data = new JSONObject();
            try {
                data.put("line", lineStr.split(" ")[1]);
                data.put("from", fromStr);
                data.put("to", toStr);
                data.put("remarks", remarksStr);
                data.put("submitBy", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "style changeover Response : " + response);
                    //TODO: modify processing of response
                }
            };

            ErrorListener errorListener = new ErrorListener(context) {
                @Override
                protected void handleTokenExpiry() {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
            };
            restUtility.post(url, data, listener,errorListener);
        }

    }



}
