package in.andonsystem.activity.v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import in.andonsystem.App;
import in.andonsystem.AppController;
import in.andonsystem.Constants;
import in.andonsystem.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.adapter.CustomProblemAdapter;
import in.andonsystem.entity.Problem;
import in.andonsystem.service.ProblemService;
import in.andonsystem.util.ErrorListener;

import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RaiseIssueActivity extends AppCompatActivity {


    private final String TAG = RaiseIssueActivity.class.getSimpleName();

    private LinearLayout container;
    private Spinner lines;
    private Spinner sections;
    private Spinner depts;
    private Spinner problems;
    private EditText operatorNo;
    private EditText issueDesc;
    private RadioGroup radioGroup;
    private Button raiseBtn;

    private SharedPreferences appPref;
    private SharedPreferences userPref;
    private ProgressDialog pDialog;
    private Context context;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "544df31b");
        setContentView(R.layout.activity_raise_issue1);
        Log.i(TAG,"onCreate()");
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //View mapping
        container = (LinearLayout)findViewById(R.id.raise_container);
        lines = (Spinner)findViewById(R.id.line_dropdown);
        sections = (Spinner)findViewById(R.id.section_dropdown);
        depts = (Spinner)findViewById(R.id.dept_dropdown);
        problems = (Spinner)findViewById(R.id.problem_dropdown);
        operatorNo = (EditText)findViewById(R.id.operator_no);
        issueDesc = (EditText)findViewById(R.id.issue_desc);
        raiseBtn = (Button)findViewById(R.id.raise_btn);
        //Initialization
        appPref = getSharedPreferences(Constants.APP_PREF,0);
        userPref = getSharedPreferences(Constants.USER_PREF,0);

        //        //Filters
        int noOfLines = Constants.NO_OF_LINES;
        String[] lineArray = new String[noOfLines+1];
        lineArray[0] = "Select Line";
        for(int i = 1; i < lineArray.length; i++){
            lineArray[i] = "Line " + i;
        }
        /*//// Line Filter //////*/
        ArrayAdapter<String> lineAdapter = new ArrayAdapter<>(this,R.layout.spinner_list_item,R.id.spinner_item,lineArray);
        lineAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        lines.setAdapter(lineAdapter);
        /*//// Section Filter //////*/
        String[] section = appPref.getString(Constants.APP_SECTIONS, "").split(";");
        final List<String> sectionList = new ArrayList<>();
        sectionList.add("Select Section");
        for (String s: section) {
            sectionList.add(s);
        }
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sectionList);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sections.setAdapter(sectionAdapter);

        /*//// Department Filter //////*/
        String[] departments = appPref.getString(Constants.APP_DEPARTMENTS, "").split(";");
        final List<String> deptList = new ArrayList<>();
deptList.add("Select Department");
        for (String d: departments) {
            deptList.add(d);
        }
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, deptList);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        depts.setAdapter(deptAdapter);
        updateProblem();
        depts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateProblem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
/*
        issueDesc.setImeActionLabel("Raise",KeyEvent.KEYCODE_ENTER);
        issueDesc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    raiseBtn.performClick();
                    return true;
                }
                return false;
            }
        });
*/

    }

    public void raiseIssue(View v){
        Log.i(TAG,"raiseIssue()");

        String lineStr = lines.getSelectedItem().toString();
        String secStr = sections.getSelectedItem().toString();
        String deptStr = depts.getSelectedItem().toString();
        String probStr = ((TextView)problems.findViewById(R.id.id)).getText().toString();

        String opNo = operatorNo.getText().toString();
        String desc = issueDesc.getText().toString();

        Long probId = Long.parseLong(probStr);

        if(lineStr.contains("Select")){
            showMessage("Select Line.");
            return;
        }
        if(secStr.contains("Select")){
            showMessage("Select Section.");
            return;
        }
        if(deptStr.contains("Select")) {
            showMessage("Select Department.");
            return;
        }
        if(probId == 0){
            showMessage("Select Problem.");
            return;
        }
        if(TextUtils.isEmpty(opNo)){
            showMessage("Enter Operator number.");
            return;
        }
        if(TextUtils.isEmpty(desc)){
            showMessage("Enter problem description.");
            return;
        }

        int line = Integer.parseInt(lineStr.split(" ")[1]);
        String critical = "NO";

        if (deptStr.equalsIgnoreCase("Maintenance")) {
            Log.i(TAG, "Maintenance Section");
            radioGroup = (RadioGroup) findViewById(R.id.radio_group);
            int id = radioGroup.getCheckedRadioButtonId();
            if (id == R.id.radio_critical) {
                critical = "YES";
            }
        }

        JSONObject data = new JSONObject();
        try {
            data.put("line", line);
            data.put("section", secStr);
            data.put("department", deptStr);
            data.put("problemId", probId);
            data.put("critical", critical);
            data.put("operatorNo", opNo);
            data.put("description", desc);
            data.put("raisedBy", userPref.getLong(Constants.USER_ID, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG,"data = " + data.toString());

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        showMessage(response.getString("message"));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ErrorListener errorListener = new ErrorListener(context) {
            @Override
            protected void handleTokenExpiry() {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        };


        final String accessToken = userPref.getString(Constants.USER_ACCESS_TOKEN,null);
        if (accessToken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else {
            String url = Constants.API1_BASE_URL + "/issues";
            Log.d(TAG, "url = " + url);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,data,listener,errorListener) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    Log.d(TAG,response.toString());
                    int status = response.statusCode;
                    Log.d(TAG,"status code: " + status);
                    if (status == 201) {
                        //Toast.makeText(context,"Issue Raised Successfully", Toast.LENGTH_SHORT).show();
                        RaiseIssueActivity.this.finish();
                    }
                    return super.parseNetworkResponse(response);
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    Log.d("TAG","access token: " + accessToken);
                    headers.put("Authorization", "Bearer " + accessToken);
                    return headers;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setTag(TAG);
            AppController.getInstance().addToRequestQueue(request);
        }

    }

    private void updateProblem(){
        ProblemService pService = new ProblemService((App)getApplication());

        TextView dept = (TextView)depts.findViewById(android.R.id.text1);

        if(dept != null){
            String department = dept.getText().toString();

            List<Problem> probList;
            if(! department.contains("Select")){
                probList = pService.findByDepartment(department);
            }else {
                probList = new ArrayList<>();
                probList.add(new Problem(0L,"Select Problem",""));
            }

            CustomProblemAdapter problemAdapter = new CustomProblemAdapter(this,R.layout.spinner_list_item,probList);
            problemAdapter.setDropDownViewResource(R.layout.spinner_list_item);
            problems.setAdapter(problemAdapter);

            RadioGroup rg = (RadioGroup)findViewById(R.id.radio_group);
            if(department.contains("Maintenance")){

                RadioButton critical = new RadioButton(this);
                critical.setId(R.id.radio_critical);
                critical.setText("Critical");
                critical.setChecked(true);
                rg.addView(critical);

                RadioButton nonCritical = new RadioButton(this);
                nonCritical.setId(R.id.radio_non_critical);
                nonCritical.setText("Non Critical");
                rg.addView(nonCritical);

            }else{
                rg.removeAllViews();
            }
        }

    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
