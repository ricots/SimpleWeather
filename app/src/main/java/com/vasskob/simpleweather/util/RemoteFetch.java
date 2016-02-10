package com.vasskob.simpleweather.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetch {

    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&APPID=%s";

    /**
     * Create and sent request to openweathermap.org, save json
     *
     * @param city    string (name of the city) that used in creating weather request
     * @param api_key sting from R.string.google_maps_api_key that is g.maps api key
     *                which is need to correct current sunrise and sunset of city
     * @return JSONObject with response from openweathermap.org server
     */
    public static JSONObject getJSON(String city, String api_key) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city, api_key));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder json = new StringBuilder(1024);
            String tmp;
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not successful
            if (data.getInt("cod") != 200) {
                return null;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}