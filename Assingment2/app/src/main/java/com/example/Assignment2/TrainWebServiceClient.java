package com.example.hack2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class TrainWebServiceClient {
    public List<Station> getStationByLocation(double lat, double lng) {
        try {
            URL url = new URL("http://10.0.2.2:8080/stations?lat=" + lat + "&lng=" + lng);
            return getStationByURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new ArrayList<Station>();
    }
    public List<Station> getStationByURL(URL url)
    {
        List<Station> list = new ArrayList<>();
        try {
            URLConnection connection = url.openConnection();
            InputStreamReader ins = new InputStreamReader(connection.getInputStream());
            BufferedReader in = new BufferedReader(ins);

            String json = "", line = "";
            while ((line = in.readLine()) != null) {
                json = json +line;
            }
            in.close();

            JSONArray ja = new JSONArray(json);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = (JSONObject) ja.get(i);
                Station s = new Station();
                s.setName(jo.getString("StationName"));
                s.setLat(jo.getDouble("Latitude"));
                s.setLong(jo.getDouble("Longitude"));
                list.add(s);
            }
            return list;
        } catch (JSONException | IOException e) {
            return new ArrayList<Station>();
        }
    }

    public URL buildURLfromLocation(double lat, double lng){
        try {
            URL url = new URL("http://10.0.2.2:8080/stations?lat=" + lat + "&lng=" + lng);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


