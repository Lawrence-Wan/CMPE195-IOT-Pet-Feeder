package com.example.petfeederfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class RequestSingleton {
    private RequestSingleton(Context ctx) {
        this.ctx = ctx;
    }

    public RequestQueue getQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(ctx);
        }
        return queue;
    }

    public <T> void addRequest(Request<T> req) {
        getQueue().add(req);
    }

    public static synchronized RequestSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RequestSingleton(context);
        }
        return instance;
    }

    private RequestQueue queue;
    private static Context ctx;
    private static RequestSingleton instance;
}
