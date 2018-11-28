package com.example.petfeederfirstapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

public class JsonSpinnerAdapter extends ArrayAdapter<JSONObject> {
    public JsonSpinnerAdapter(Context context, int textViewResourceId,
                              JSONObject[] objs, LabelSetter labelSetter) {
        super(context, textViewResourceId, objs);
        this.context = context;
        this.objects = objs;
        this.labelSetter = labelSetter;
    }

    public void setData(JSONObject[] objs) {
        this.objects = objs;
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return objects.length;
    }

    @Override
    public JSONObject getItem(int position){
        return objects[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(labelSetter.getLabel(objects[position]));

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(labelSetter.getLabel(objects[position]));
        return label;
    }

    private Context context;
    private JSONObject[] objects;
    private LabelSetter labelSetter;
}
