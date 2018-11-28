package com.example.petfeederfirstapp;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void onClickSubmit(View view) {
        Log.d("submit","submit clicked");

        String url = "http://ec2-13-57-38-126.us-west-1.compute.amazonaws.com:8000/register/";

        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String firstname = ((EditText)findViewById(R.id.firstname)).getText().toString();
        String lastname = ((EditText)findViewById(R.id.lastname)).getText().toString();
        String email = ((EditText)findViewById(R.id.email)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("firstname", firstname);
        params.put("lastname", lastname);
        params.put("email", email);
        params.put("password", password);

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
            };
            RequestSingleton.getInstance(this).addRequest(request);
        } catch (Exception e) {
            Log.d("add pet error", e.getMessage());
        }

        finish();
    }

    public void onClickCancel(View view) {
        finish();
    }
}
