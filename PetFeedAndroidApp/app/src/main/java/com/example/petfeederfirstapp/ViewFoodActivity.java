package com.example.petfeederfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        try {
            this.food = new JSONObject(getIntent().getStringExtra("food"));
        } catch (JSONException e) {
            this.food = new JSONObject();
            Log.d("food json err", e.getMessage());
        }

        String name = getField("name");
        String calories_serving = getField("calories_serving");
        String cups_serving = getField("cups_serving");
        String density = getField("density");

        ((TextView)findViewById(R.id.pet_food_name)).setText(name);
        ((TextView)findViewById(R.id.calories_serving)).setText(calories_serving);
        ((TextView)findViewById(R.id.cups_serving)).setText(cups_serving);
        ((TextView)findViewById(R.id.density)).setText(density);
    }

    private String getField(String key) {
        String val = "";
        try {
            val = food.getString(key);
        } catch (JSONException e) {
            Log.d("get food", "key not found; " + e.getMessage());
        }
        return val;
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickEdit(View view) {
        Intent intent = new Intent(ViewFoodActivity.this, AddFoodActivity.class);
        intent.putExtra("food", this.food.toString());
        startActivity(intent);
        finish();
    }

    private JSONObject food;
}
