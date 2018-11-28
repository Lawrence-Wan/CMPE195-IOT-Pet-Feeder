package com.example.petfeederfirstapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewPetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pet);

        points = new ArrayList<DataPoint>();
        setupGraph();

        try {
            this.pet = new JSONObject(getIntent().getStringExtra("pet"));
        } catch (JSONException e) {
            this.pet = new JSONObject();
            Log.d("pet json err", e.getMessage());
        }

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                getString(R.string.app_name), Context.MODE_PRIVATE);
        String pet_id = "";
        try {
            pet_id = pet.getString("id");
        } catch (JSONException e) {
            Log.d("view pet json", "error could not find pet id");
        }
        String url = String.format(getString(R.string.server_url) + "/consumption/?pet=%s", pet_id);


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response", response.toString());
                        updatePoints(response);
                        updateGraph();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
            }) {
            @Override
            public Map<String, String> getHeaders() {
                String token = preferences.getString(getString(R.string.login_token), "");
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put(getString(R.string.authorization_header), getString(R.string.token_prefix) + " " + token);
                return headers;
            }
        };
        RequestSingleton.getInstance(this).addRequest(request);


        String pet_chip_id = getField("chip_id");
        String pet_birthday = getField("birthday");
        String pet_breed = getField("pet_breed");
        String pet_name = getField("name");
        String pet_type = getField("pet_type");

        ((TextView)findViewById(R.id.pet_chip_id)).setText(pet_chip_id);
        ((TextView)findViewById(R.id.pet_birthday)).setText(pet_birthday);
        ((TextView)findViewById(R.id.pet_breed)).setText(pet_breed);
        ((TextView)findViewById(R.id.pet_name)).setText(pet_name);
        ((TextView)findViewById(R.id.pet_type)).setText(pet_type);
    }

    public void onClickEdit(View view) {
        Intent intent = new Intent(ViewPetActivity.this, EditPetActivity.class);
        intent.putExtra("pet", this.pet.toString());
        startActivity(intent);
        finish();
    }

    public void onClickBack(View view) {
        finish();
    }

    private void updatePoints(JSONArray response) {
        points.clear();
        for (int i = 0; i < response.length(); ++i) {
            try {
                JSONObject obj = response.getJSONObject(i);
                String key = obj.keys().next();
                Date d = new SimpleDateFormat(
                        getString(R.string.consumption_date_format), Locale.ENGLISH)
                        .parse(key);

                double value = Double.parseDouble(obj.getString(key));
                points.add(new DataPoint(d, value));
                Log.d("data point", points.get(i).toString());
            } catch (JSONException e) {
                Log.d("graph", "error: " + e.getMessage());
            } catch (java.text.ParseException e) {
                Log.d("graph", "error: could not parse date; " + e.getMessage());
            }
            Log.d("graph pts", points.toString());
        }
    }

    private void setupGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        //graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(90);
    }

    private void updateGraph() {

        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(
                points.toArray(new DataPoint[points.size()]));

        if (points.size() > 0) {
            double min = points.get(0).getX();
            double max = points.get(0).getX();
            for (int i = 0; i < points.size(); ++i) {
                double x = points.get(i).getX();
                if (x > max) {
                    max = x;
                } else if (x < min) {
                    min = x;
                }
            }
            graph.getViewport().setMinX(min);
            graph.getViewport().setMaxX(max);
            graph.getViewport().setXAxisBoundsManual(true);

        }

        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        graph.addSeries(series);
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
    private ArrayList<DataPoint> points;
}
