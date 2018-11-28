package com.example.petfeederfirstapp;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        try {
            this.pet = new JSONObject(getIntent().getStringExtra("pet"));
        } catch (JSONException e) {
            this.pet = new JSONObject();
            Log.d("pet json err", e.getMessage());
        }
        Log.d("edit pet", pet.toString());

        try {
            pet_id = pet.getString("id");
        } catch (JSONException e) {
            Log.d("pet json err", e.getMessage());
            finish();
        }

        String pet_chip_id = getField("chip_id");
        String pet_birthday = getField("birthday");
        String pet_birthday_year = "";
        String pet_birthday_month = "";
        String pet_birthday_day = "";
        String[] pet_birthday_split = pet_birthday.split("-");
        if (pet_birthday_split.length == 3) {
            pet_birthday_year = pet_birthday_split[0];
            pet_birthday_month = pet_birthday_split[1];
            pet_birthday_day = pet_birthday_split[2];
        }
        String pet_breed = getField("pet_breed");
        String pet_name = getField("name");
        String pet_type = getField("pet_type");

        ((EditText)findViewById(R.id.pet_chip_id)).setText(pet_chip_id);
        ((EditText)findViewById(R.id.pet_birthday_year)).setText(pet_birthday_year);
        ((EditText)findViewById(R.id.pet_birthday_month)).setText(pet_birthday_month);
        ((EditText)findViewById(R.id.pet_birthday_day)).setText(pet_birthday_day);
        ((EditText)findViewById(R.id.pet_breed)).setText(pet_breed);
        ((EditText)findViewById(R.id.pet_name)).setText(pet_name);
        ((EditText)findViewById(R.id.pet_type)).setText(pet_type);
    }

    public void onClickDelete(View view) {
        String url = getString(R.string.server_url) + "/pets/" + pet_id + "/";
        final SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);
        try {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("delete pet", "delete successful");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // runs this when it succeeds still?
                            VolleyLog.e("delete pet error: ", error.getMessage());
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

    public void onClickCancel(View view) {
        finish();
    }

    public void onClickSave(View view) {
        String url = getString(R.string.server_url) + "/pets/" + pet_id + "/";

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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
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
            Log.d("add pet error", e.getMessage());
        }
        finish();
    }

    private String getField(String key) {
        String val = "";
        try {
            val = pet.getString(key);
        } catch (JSONException e) {
            Log.d("edit pet", "key not found; " + e.getMessage());
        }
        return val;
    }

    private JSONObject pet;

    private String pet_id;
}
