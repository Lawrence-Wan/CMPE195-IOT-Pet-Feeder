package com.example.petfeederfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddFeederActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feeder);
        preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);


        try {
            String serialized = getIntent().getStringExtra("feeder");
            if (serialized != null && serialized.length() > 0) {
                this.editFeeder = new JSONObject(serialized);
                this.editFeederId = editFeeder.getString("id");
            }
        } catch (JSONException e) {
            Log.d("feeder json", "feeder not found; " + e.getMessage());
        }

        if (this.editFeeder != null) {
            setupEdit();
        } else {
            teardownEdit();
        }

        setupPetSpinner();
        setupFoodSpinner();

        getPets();
        getFood();
    }

    private void setupEdit() {
        try {
            String serial_id = this.editFeeder.getString("serial_id");
            String setting_cup = this.editFeeder.getString("setting_cup");
            String setting_interval = this.editFeeder.getString("setting_interval");
            ((EditText)findViewById(R.id.serial_id)).setText(serial_id);
            ((EditText)findViewById(R.id.setting_cup)).setText(setting_cup);
            ((EditText)findViewById(R.id.setting_interval)).setText(setting_interval);
        } catch (JSONException e) {
            Log.d("feeder error", "edit setup json error; " + e.getMessage());
        }
    }

    private void teardownEdit() {
        // remove delete button
        View view = findViewById(R.id.delete);
        ((ViewGroup)view.getParent()).removeView(view);
    }

    private void setupPetSpinner() {
        petSpinnerAdapter = new JsonSpinnerAdapter(this, android.R.layout.simple_spinner_item,
            new JSONObject[0], new LabelSetter() {
        @Override
        public String getLabel(JSONObject obj) {
            String label = "";
            try {
                label = obj.getString("name");
            } catch (JSONException e) {
                Log.d("spinner label", "could not get name label from pet");
            }
            return label;
            }
        });
        Spinner petSpinner = findViewById(R.id.pet_selection);
        petSpinner.setAdapter(petSpinnerAdapter);
        petSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPet = (JSONObject) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupFoodSpinner() {
        foodSpinnerAdapter = new JsonSpinnerAdapter(this, android.R.layout.simple_spinner_item,
                new JSONObject[0], new LabelSetter() {
            @Override
            public String getLabel(JSONObject obj) {
                String label = "";
                try {
                    label = obj.getString("name");
                } catch (JSONException e) {
                    Log.d("spinner label", "could not get name label from pet");
                }
                return label;
            }
        });
        Spinner foodSpinner = findViewById(R.id.food_selection);
        foodSpinner.setAdapter(foodSpinnerAdapter);
        foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFood = (JSONObject)adapterView.getItemAtPosition(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    public void onClickDelete(View view) {
        String url = getString(R.string.server_url) + "/petfeeders/" + editFeederId + "/";
        final SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);
        try {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("delete feeder", "delete successful");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // runs this when it succeeds still?
                            VolleyLog.e("delete feeder error: ", error.getMessage());
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
            Log.d("delete feeder error", e.getMessage());
        }
        finish();
    }

    public void onClickAdd(View view) {
        String user_id = preferences.getString(getString(R.string.user_id), "");
        String pet_id = getField(selectedPet, "id");
        String food_id = getField(selectedFood, "id");

        String serial_id = ((EditText)findViewById(R.id.serial_id)).getText().toString();
        String setting_cup = ((EditText)findViewById(R.id.setting_cup)).getText().toString();
        String setting_closure = "false"; // not being used currently
        String setting_interval = ((EditText)findViewById(R.id.setting_interval)).getText().toString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("pet", pet_id);
        params.put("food", food_id);
        params.put("serial_id", serial_id);
        params.put("setting_interval", setting_interval);
        params.put("setting_cup", setting_cup);
        params.put("setting_closure", setting_closure);
        params.put("user", user_id);

        Log.d("feeder params", params.toString());

        String url = getString(R.string.server_url) + "/petfeeders/";
        try {
            int requestType = Request.Method.POST;
            if (this.editFeeder != null) {
                requestType = Request.Method.PUT;
                url += editFeederId + "/";
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
            Log.d("add pet error", e.getMessage());
        }
        finish();
    }

    public void onClickCancel(View view) {
        finish();
    }

    private void getPets() {
        String url = getString(R.string.server_url) + "/pets/";

        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject[] pets = new JSONObject[response.length()];
                    for (int i = 0; i < response.length(); ++i) {
                        try {
                            pets[i] = response.getJSONObject(i);
                            Log.d("get pet spin", pets[i].toString());
                        } catch (JSONException e) {
                            Log.d("json object error", e.getMessage());
                        }
                    }
                    petSpinnerAdapter.setData(pets);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("GetPets request error", "volley error");
                }
            }
        ){
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

    private void getFood() {
        String url = getString(R.string.server_url) + "/petfood/";

        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject[] pet_food = new JSONObject[response.length()];
                    for (int i = 0; i < response.length(); ++i) {
                        try {
                            pet_food[i] = response.getJSONObject(i);
                            Log.d("get pet spin", pet_food[i].toString());
                        } catch (JSONException e) {
                            Log.d("json object error", e.getMessage());
                        }
                    }
                    foodSpinnerAdapter.setData(pet_food);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("GetPetfood error", "volley error");
                }
            }
        ){
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

    private String getField(JSONObject obj, String key) {
        String val = "";
        try {
            val = obj.getString(key);
        } catch (JSONException e) {
            Log.d("edit pet", "key not found; " + e.getMessage());
        }
        return val;
    }

    private JsonSpinnerAdapter petSpinnerAdapter;
    private JsonSpinnerAdapter foodSpinnerAdapter;

    private JSONObject editFeeder;
    private String editFeederId;

    private SharedPreferences preferences;

    JSONObject selectedPet;
    JSONObject selectedFood;
}
