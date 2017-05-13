package in.andonsystem.v2.util;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by razamd on 4/2/2017.
 */

public class MyJsonRequest extends JsonObjectRequest {
    private String accessToken;
    public MyJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken) {
        super(method, url, jsonRequest, listener, errorListener);
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        //headers.put("Accept", "application/json; charset=utf-8");
        return headers;
    }
}
