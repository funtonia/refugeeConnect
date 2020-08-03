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
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.BonusCard;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Contact;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.Doctor;
import com.example.schlauefuechse.refugeeconnect.Model.BasicClasses.SportCourse;
import com.example.schlauefuechse.refugeeconnect.Model.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Eric on 10.06.16.
 */
public class AsyncDataFetch extends AsyncTask {

    Context conty;

    public AsyncDataFetch (Context conty){
        this.conty = conty;
    }
    @Override
    protected Void doInBackground(Object[] params) {

        RequestQueue queue = Volley.newRequestQueue(conty);
        String url ="http://refugeeconnect.mi.hdm-stuttgart.de:8080/jtdserver2/sportCourse/" + Model.model.systemLanguage + "/Stuttgart";


// Request a string response from the provided URL.
        StringRequest sportCourseRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<SportCourse>>() {}.getType();
                        Model.model.sportCourseArrayList = gson.fromJson(response, listType);
                        Model.model.saveSportCourses();
                        Model.model.sportCourseFetched = true;
                        Model.model.checkIfLoadingIsFinished();
                        Log.d("vers", "SportCourse: " + Model.model.sportCourseArrayList.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(sportCourseRequest);

        String url2 ="http://refugeeconnect.mi.hdm-stuttgart.de:8080/jtdserver2/contact/" + Model.model.systemLanguage + "/Stuttgart";


// Request a string response from the provided URL.
        StringRequest contactRequest = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Contact>>() {}.getType();
                        Model.model.contactArrayList = gson.fromJson(response, listType);
                        Model.model.saveContacts();
                        Model.model.contactFetched = true;
                        Model.model.checkIfLoadingIsFinished();
                        Log.d("vers", "Contact: " + Model.model.contactArrayList.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(contactRequest);

        String url3 ="http://refugeeconnect.mi.hdm-stuttgart.de:8080/jtdserver2/doctor/" + Model.model.systemLanguage + "/Stuttgart";


// Request a string response from the provided URL.
        StringRequest doctorRequest = new StringRequest(Request.Method.GET, url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Doctor>>() {}.getType();
                        Model.model.doctorArrayList = gson.fromJson(response, listType);
                        Model.model.saveDoctors();
                        Model.model.doctorFetched = true;
                        Model.model.checkIfLoadingIsFinished();
                        Log.d("vers", "Doctor: " + Model.model.doctorArrayList.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(doctorRequest);

        String url4 ="http://refugeeconnect.mi.hdm-stuttgart.de:8080/jtdserver2/bonusCard/" + Model.model.systemLanguage + "/Stuttgart";


// Request a string response from the provided URL.
        StringRequest bonusCardRequest = new StringRequest(Request.Method.GET, url4,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<BonusCard>>() {}.getType();
                        Model.model.bonusCardArrayList = gson.fromJson(response, listType);
                        Model.model.saveBonusCards();
                        Model.model.bonusCardFetched = true;
                        Model.model.checkIfLoadingIsFinished();
                        Log.d("vers", "BonusCard: " + Model.model.bonusCardArrayList.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(bonusCardRequest);
        return null;
    }
}
