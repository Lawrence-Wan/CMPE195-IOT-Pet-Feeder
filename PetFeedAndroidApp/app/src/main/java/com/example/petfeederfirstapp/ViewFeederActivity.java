package com.example.petfeederfirstapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewFeederActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feeder);
        preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);

        try {
            this.feeder = new JSONObject(getIntent().getStringExtra("feeder"));
        } catch (JSONException e) {
            this.feeder = new JSONObject();
            Log.d("feeder json err", e.getMessage());
        }

        String serial_id = getField("serial_id");
        String setting_cup = getField("setting_cup");
        String setting_interval = getField("setting_interval");

        ((TextView)findViewById(R.id.serial_id)).setText(serial_id);
        ((TextView)findViewById(R.id.setting_cup)).setText(setting_cup);
        ((TextView)findViewById(R.id.setting_interval)).setText(setting_interval);
        getPetName();
        getFoodName();
    }

    private void getPetName() {
        // generate request, update id

        String pet_id = getField("pet");
        if (pet_id.length() == 0) {
            return;
        }

        String url = getString(R.string.server_url) + "/pets/" + pet_id + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("get pet", "get successful");
                        String pet_name = "";
                        try {
                            pet_name = response.getString("name");
                        } catch (JSONException e) {
                            Log.d("get pet", "failed to get name");
                        }
                        ((TextView)findViewById(R.id.pet_name)).setText(pet_name);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("get pet error: ", error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString(getString(R.string.login_token), "");
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put(getString(R.string.authorization_header), getString(R.string.token_prefix) + " " + token);
                Log.d("add pet", "token: " + headers.get(getString(R.string.authorization_header)));
                return headers;
            }
        };
        RequestSingleton.getInstance(this).addRequest(request);
    }

    private void getFoodName() {
        // generate request, update id

        String pet_food_id = getField("food");
        if (pet_food_id.length() == 0) {
            return;
        }

        String url = getString(R.string.server_url) + "/petfood/" + pet_food_id + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("get pet food", response.toString());
                        String pet_food = "";
                        try {
                            pet_food = response.getString("name");
                        } catch (JSONException e) {
                            Log.d("get pet food", "failed to get name");
                        }
                        ((TextView)findViewById(R.id.pet_food)).setText(pet_food);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("get pet error: ", error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString(getString(R.string.login_token), "");
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put(getString(R.string.authorization_header), getString(R.string.token_prefix) + " " + token);
                Log.d("add pet", "token: " + headers.get(getString(R.string.authorization_header)));
                return headers;
            }
        };
        RequestSingleton.getInstance(this).addRequest(request);
    }

    public void onClickDispense(View view) {
        String serial_id = getField("serial_id");
        String url = String.format(
                getString(R.string.server_url) + "/request/?feeder=%s&command=dispense", serial_id);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("dispense", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("dispense error: ", error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString(getString(R.string.login_token), "");
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put(getString(R.string.authorization_header), getString(R.string.token_prefix) + " " + token);
                return headers;
            }
        };
        RequestSingleton.getInstance(this).addRequest(request);
    }

    public void onClickEdit(View view) {
        Intent intent = new Intent(ViewFeederActivity.this, AddFeederActivity.class);
        intent.putExtra("feeder", this.feeder.toString());
        startActivity(intent);
        finish();
    }

    public void onClickBack(View view) {
        finish();
    }

    private String getField(String key) {
        String val = "";
        try {
            val = feeder.getString(key);
        } catch (JSONException e) {
            Log.d("edit pet", "key not found; " + e.getMessage());
        }
        return val;
    }
    private JSONObject feeder;
    private SharedPreferences preferences;
}
