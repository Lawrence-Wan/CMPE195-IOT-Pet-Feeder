package com.example.petfeederfirstapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements RecyclerViewClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("test", "launching main activity");
        preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);
        String token = preferences.getString(getString(R.string.login_token), "");
        Log.d("token", token);
        if (token.length() == 0) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_home);

        /* Get pet information */
        getPets(); // generate asynchronous request
        getPetFeeders();
        getPetFood();

        /* Attach recycler view for the pets */
        RecyclerView petRecycler = (RecyclerView)findViewById(R.id.pet_recycler_view);
        petRecycler.setLayoutManager(new LinearLayoutManager(this));
        JSONObject[] petData = pets != null ? pets : new JSONObject[0];
        petAdapter = new PetViewAdapter(this, petData);
        petAdapter.setClickListener(this);
        petRecycler.setAdapter(petAdapter);

        /* Recycler view for feeder */
        RecyclerView feederRecycler = (RecyclerView)findViewById(R.id.pet_feeder_recycler_view);
        feederRecycler.setLayoutManager(new LinearLayoutManager(this));
        JSONObject[] feederData = feeders != null ? feeders : new JSONObject[0];
        feederAdapter = new PetFeederViewAdapter(this, feederData);
        feederAdapter.setClickListener(this);
        feederRecycler.setAdapter(feederAdapter);

        /* Recycler view for pet food */
        RecyclerView foodRecycler = (RecyclerView)findViewById(R.id.pet_food_recycler_view);
        foodRecycler.setLayoutManager(new LinearLayoutManager(this));
        JSONObject[] foodData = food != null ? food : new JSONObject[0];
        foodAdapter = new PetFoodViewAdapter(this, foodData);
        foodAdapter.setClickListener(this);
        foodRecycler.setAdapter(foodAdapter);

        /* Add divider between recycler view items */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        DividerItemDecoration petDecoration = new DividerItemDecoration(petRecycler.getContext(),
                layoutManager.getOrientation());
        petRecycler.addItemDecoration(petDecoration);

        DividerItemDecoration feederDecoration = new DividerItemDecoration(feederRecycler.getContext(),
                layoutManager.getOrientation());
        feederRecycler.addItemDecoration(feederDecoration);

        DividerItemDecoration foodDecoration = new DividerItemDecoration(foodRecycler.getContext(),
                layoutManager.getOrientation());
        foodRecycler.addItemDecoration(foodDecoration);
    }

    @Override
    public void onResume() {
        Log.d("home activity","resume");
        super.onResume();
        getPets();
        getPetFeeders();
        getPetFood();

        // Poll for update; hack to call GET after POST finishes
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPets();
                getPetFeeders();
                getPetFood();
            }
        }, 200);
    }

    @Override
    public void onClickRecyclerView(View v, int position, int type) {
        Log.d("recycler click", "id: " + Integer.toString(v.getId()) + " pos: " + Integer.toString(position));
        if (type == R.id.pet_recycler_view) {
            Log.d("item", petAdapter.getItem(position).toString());
            Intent intent = new Intent(HomeActivity.this, ViewPetActivity.class);
            intent.putExtra("pet", petAdapter.getItem(position).toString());
            startActivity(intent);
        } else if (type == R.id.pet_feeder_recycler_view) {
            Log.d("item", feederAdapter.getItem(position).toString());
            Intent intent = new Intent(HomeActivity.this, ViewFeederActivity.class);
            intent.putExtra("feeder", feederAdapter.getItem(position).toString());
            startActivity(intent);
        } else if (type == R.id.pet_food_recycler_view) {
            Log.d("item", foodAdapter.getItem(position).toString());
            Intent intent = new Intent(HomeActivity.this, ViewFoodActivity.class);
            intent.putExtra("food", foodAdapter.getItem(position).toString());
            startActivity(intent);
        } else {
            Log.d("recycler click","other recycler clicked");
        }
    }

    public void onClickAddPet(View view) {
        Intent intent = new Intent(HomeActivity.this, AddPetActivity.class);
        startActivity(intent);
    }

    public void onClickAddFeeder(View view) {
        Intent intent = new Intent(HomeActivity.this, AddFeederActivity.class);
        startActivity(intent);
    }

    public void onClickAddFood(View view) {
        Intent intent = new Intent(HomeActivity.this, AddFoodActivity.class);
        startActivity(intent);
    }

    private void getPets() {
        String url = getString(R.string.server_url) + "/pets/";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pets = new JSONObject[response.length()];
                    for (int i = 0; i < response.length(); ++i) {
                        try {
                            pets[i] = response.getJSONObject(i);
                            Log.d("pet", pets[i].toString());
                        } catch (JSONException e) {
                            Log.d("json object error", e.getMessage());
                        }
                    }
                    if (petAdapter == null) {
                        Log.d("PET ADAPTER", "NULL");
                    }
                    petAdapter.setData(pets);
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

    private void getPetFeeders() {
        String url = getString(R.string.server_url) + "/petfeeders/";

        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    feeders = new JSONObject[response.length()];
                    for (int i = 0; i < response.length(); ++i) {
                        try {
                            feeders[i] = response.getJSONObject(i);
                            Log.d("feeder", feeders[i].toString());
                        } catch (JSONException e) {
                            Log.d("json object error", e.getMessage());
                        }
                    }
                    Log.d("home activity","setting feeder data");
                    feederAdapter.setData(feeders);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("GetFeeders error", "volley error");
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

    private void getPetFood() {
        String url = getString(R.string.server_url) + "/petfood/";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        food = new JSONObject[response.length()];
                        for (int i = 0; i < response.length(); ++i) {
                            try {
                                food[i] = response.getJSONObject(i);
                                Log.d("food", food[i].toString());
                            } catch (JSONException e) {
                                Log.d("json object error", e.getMessage());
                            }
                        }
                        Log.d("home activity","setting food data");
                        foodAdapter.setData(food);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("GetFood error", "volley error");
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

    public void onClickLogout(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(getString(R.string.login_token));
        editor.commit();
        finish();
        startActivity(getIntent());
    }

    private JSONObject[] pets;
    private JSONObject[] feeders;
    private JSONObject[] food;


    private SharedPreferences preferences;

    private PetViewAdapter petAdapter;
    private PetFeederViewAdapter feederAdapter;
    private PetFoodViewAdapter foodAdapter;
}
