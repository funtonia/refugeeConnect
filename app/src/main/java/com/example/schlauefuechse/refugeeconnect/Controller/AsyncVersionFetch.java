package com.example.schlauefuechse.refugeeconnect.Controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.VersionControll;
import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Eric on 10.06.16.
 */
public class AsyncVersionFetch extends AsyncTask {
    MainActivity acty;
    Context conty;

    public AsyncVersionFetch (MainActivity acty, Context conty){
        this.acty = acty;
        this.conty = conty;
    }
    @Override
    protected Void doInBackground(Object... params) {
        RequestQueue queue = Volley.newRequestQueue(conty);
        String url ="http://refugeeconnect.mi.hdm-stuttgart.de:8080/jtdserver2/versionNumber";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type versionType = new TypeToken<ArrayList<VersionControll>>() {}.getType();
                        ArrayList<VersionControll> arry = gson.fromJson(response, versionType);
                        Model.model.newVersionNumber = arry.get(0);
                        Model.model.versionNumberFetched = true;

                        Log.d("async", "Es wurde geladen");
                        Log.d("async", Model.model.newVersionNumber.getVersionNumber());

                        Model.model.checkVersionNumber();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
        return null;
    }
}
