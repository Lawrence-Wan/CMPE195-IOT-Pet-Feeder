package com.example.petfeederfirstapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PetViewAdapter extends RecyclerView.Adapter<PetViewAdapter.PetViewHolder> {

    public class PetViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public PetViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.pet_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClickRecyclerView(view, this.getAdapterPosition(), R.id.pet_recycler_view);
            }
        }

        public TextView textView;
    }

    public PetViewAdapter(Context ctx, JSONObject[] dataset) {
        this.inflater = LayoutInflater.from(ctx);
        this.data = dataset;
    }

    @Override
    public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pet_view, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PetViewHolder holder, int position) {
        JSONObject pet = data[position];
        String name = "null";
        try {
             name = pet.getString("name");
        } catch (JSONException e) {
            Log.d("error", e.getMessage());
        }
        holder.textView.setText(name);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public void setData(JSONObject[] data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public JSONObject getItem(int position) {
        return data[position];
    }

    void setClickListener(RecyclerViewClickListener listener) {
        clickListener = listener;
    }

    private JSONObject[] data;
    private RecyclerViewClickListener clickListener;
    private LayoutInflater inflater;
}
