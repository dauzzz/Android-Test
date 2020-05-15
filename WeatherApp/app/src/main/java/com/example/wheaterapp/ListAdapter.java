package com.example.wheaterapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    private List<JSONObject> jsonList = new ArrayList<>();
    private LayoutInflater layoutInflater;

    ListAdapter(Context context, List<JSONObject> jsonList){
        this.jsonList = jsonList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return jsonList.size();
    }

    @Override
    public Object getItem(int position) {
        return jsonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder{
        //all the view
        TextView day;
        TextView weather;
        TextView maxTemp;
        TextView minTemp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.view_adapter,parent);
            holder = new ViewHolder();
            holder.day = (TextView) convertView.findViewById(R.id.day);
            holder.weather = (TextView) convertView.findViewById(R.id.weather);
            holder.maxTemp = (TextView) convertView.findViewById(R.id.maxTemp);
            holder.minTemp = (TextView) convertView.findViewById(R.id.minTemp);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jsonObject = (JSONObject) getItem(position);
        try {
            JSONObject weatherEl = jsonObject.getJSONObject("weatherElements");
            JSONObject wx = weatherEl.getJSONObject("Wx");
            JSONObject maxT = weatherEl.getJSONObject("MaxT");
            JSONObject minT = weatherEl.getJSONObject("MinT");
            JSONArray wxDaily = wx.getJSONArray("daily");
            JSONArray maxTDaily = maxT.getJSONArray("daily");
            JSONArray minTDaily = minT.getJSONArray("daily");

            String day = jsonObject.getString("");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return null;
    }
}
