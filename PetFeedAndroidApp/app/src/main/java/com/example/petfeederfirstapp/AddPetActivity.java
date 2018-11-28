package com.example.petfeederfirstapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddPetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
    }

    public void onClickCancel(View view) {
        finish();
    }

    public void onClickAdd(View view) {
        String url = getString(R.string.server_url) + "/pets/";

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);
        final String user_id = preferences.getString(getString(R.string.user_id), "");
        final String pet_chip_id = ((EditText)findViewById(R.id.pet_chip_id)).getText().toString();

        final String pet_birthday =
                ((EditText)findViewById(R.id.pet_birthday_year)).getText().toString() + "-" +
                ((EditText)findViewById(R.id.pet_birthday_month)).getText().toString() + "-" +
                ((EditText)findViewById(R.id.pet_birthday_day)).getText().toString();

        final String pet_breed = ((EditText)findViewById(R.id.pet_breed)).getText().toString();
        final String pet_name = ((EditText)findViewById(R.id.pet_name)).getText().toString();
        final String pet_type = ((EditText)findViewById(R.id.pet_type)).getText().toString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("chip_id", pet_chip_id);
        params.put("pet_type", pet_type);
        params.put("pet_breed", pet_breed);
        params.put("name", pet_name);
        params.put("birthday", pet_birthday);
        params.put("user", user_id);

        try {
            JSONObject jsonBody = new JSONObject(params);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
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
                        Log.d("add pet", "token: " + headers.get(getString(R.string.authorization_header)));
                        return headers;
                    }
                };
            RequestSingleton.getInstance(this).addRequest(request);
        } catch (Exception e) {
            Log.d("add pet error", e.getMessage());
        }
        finish();
    }

}
