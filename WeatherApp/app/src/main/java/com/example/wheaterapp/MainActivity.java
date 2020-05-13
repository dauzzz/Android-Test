package com.example.wheaterapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.Edits;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {
                    Manifest.permission.INTERNET
            },200);
        }
        Https https = new Https(getApplicationContext());
        List<JSONObject> myJSONList = new ArrayList<>();
        try {
            JSONObject jsonObject = https.execute().get();
            JSONObject cwbdata = jsonObject.getJSONObject("cwbdata");
            JSONObject resources = cwbdata.getJSONObject("resources");
            JSONObject resource = resources.getJSONObject("resource");
            JSONObject data = resource.getJSONObject("data");
            JSONObject agrWeather = data.getJSONObject("agrWeatherForecasts");
            JSONObject weatherForecasts = agrWeather.getJSONObject("weatherForecasts");
            JSONArray location = weatherForecasts.getJSONArray("location");
            HashMap<String,JSONObject> map = new HashMap<>();
            for(int i=0;i<location.length();i++){
                myJSONList.add(location.getJSONObject(i));
                Log.i("mainThread",location.getString(i));
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }


    }




}
