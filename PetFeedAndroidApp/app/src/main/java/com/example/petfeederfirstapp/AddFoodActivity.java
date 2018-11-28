package com.example.petfeederfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

public class AddFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);


        try {
            String serialized = getIntent().getStringExtra("food");
            if (serialized != null && serialized.length() > 0) {
                this.editFood = new JSONObject(serialized);
                this.editFoodId = editFood.getString("id");
            }
        } catch (JSONException e) {
            Log.d("feeder json", "feeder not found; " + e.getMessage());
        }

        if (this.editFood != null) {
            setupEdit();
        }
    }

    public void onClickCancel(View view) {
        finish();
    }

    public void onClickAdd(View view) {
        String user_id = preferences.getString(getString(R.string.user_id), "");

        String name = ((EditText)findViewById(R.id.pet_food_name)).getText().toString();
        String calories_serving = ((EditText)findViewById(R.id.calories_serving)).getText().toString();
        String cups_serving = ((EditText)findViewById(R.id.cups_serving)).getText().toString();
        String density = ((EditText)findViewById(R.id.density)).getText().toString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("calories_serving", calories_serving);
        params.put("cups_serving", cups_serving);
        params.put("density", density);
        params.put("user", user_id);

        String url = getString(R.string.server_url) + "/petfood/";
        try {
            int requestType = Request.Method.POST;
            if (this.editFood != null) {
                requestType = Request.Method.PUT;
                url += editFoodId + "/";
            }

            JSONObject jsonBody = new JSONObject(params);
            JsonObjectRequest request = new JsonObjectRequest(requestType, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.e("Error: ", error.getMessage());
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
        } catch (Exception e) {
            Log.d("add food error", e.getMessage());
        }
        finish();
    }

    private void setupEdit() {
        try {
            String name = this.editFood.getString("name");
            String calories_serving = this.editFood.getString("calories_serving");
            String cups_serving = this.editFood.getString("cups_serving");
            String density = this.editFood.getString("density");

            ((EditText)findViewById(R.id.pet_food_name)).setText(name);
            ((EditText)findViewById(R.id.calories_serving)).setText(calories_serving);
            ((EditText)findViewById(R.id.cups_serving)).setText(cups_serving);
            ((EditText)findViewById(R.id.density)).setText(density);
        } catch (JSONException e) {
            Log.d("feeder error", "edit setup json error; " + e.getMessage());
        }
    }

    private SharedPreferences preferences;
    private JSONObject editFood;
    private String editFoodId;
}
