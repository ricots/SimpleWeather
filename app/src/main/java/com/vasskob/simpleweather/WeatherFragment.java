package com.vasskob.simpleweather;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vasskob.simpleweather.util.CityTimeZone;
import com.vasskob.simpleweather.util.InternetConnection;
import com.vasskob.simpleweather.util.Preferences;
import com.vasskob.simpleweather.util.RemoteFetch;
import com.vasskob.simpleweather.util.StringFormater;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView currentTemperatureType;
    private TextView cityCoordinates;
    private TextView description;
    private TextView detailsPressure;
    private TextView minMaxTemperatureField;
    private TextView sunrise;
    private TextView sunset;

    private final Handler handler;
    private ImageView im;
    private RelativeLayout rl;

    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        description = (TextView) rootView.findViewById(R.id.description_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        detailsPressure = (TextView) rootView.findViewById(R.id.details_pressure);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        currentTemperatureType = (TextView) rootView.findViewById(R.id.current_temperature_type);
        minMaxTemperatureField = (TextView) rootView.findViewById(R.id.minMax_temperature_field);
        cityCoordinates = (TextView) rootView.findViewById(R.id.city_coord);
        sunrise = (TextView) rootView.findViewById(R.id.sunrise_time);
        sunset = (TextView) rootView.findViewById(R.id.sunset_time);

        im = (ImageView) rootView.findViewById(R.id.imageView);
        rl = (RelativeLayout) rootView.findViewById(R.id.relative_layout);

        if (!InternetConnection.isInternetAvailable()) {
            rl.setBackgroundResource(R.drawable.network_error_bg);
        }

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateWeatherData(Preferences.getStringPreferences(getContext(), "CITY_NAME"));
    }

    /**
     * Updating weather data by sending request to server with
     *
     * @param city , name of city for daily weather
     */
    public void updateWeatherData(final String city) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(city, getString(R.string.open_weather_map_api_key));
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * Render json object and show all data on screen
     *
     * @param json , respond from openweathermap.org server in json format
     */
    private void renderWeather(JSONObject json) {
        try {

            long sunrise_json = json.getJSONObject("sys").getLong("sunrise");
            long sunset_json = json.getJSONObject("sys").getLong("sunset");
            double latitude_json = json.getJSONObject("coord").getDouble("lat");
            double longitude_json = json.getJSONObject("coord").getDouble("lon");

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");

            setWeatherIcon(details.getInt("id"), sunrise_json * 1000, sunset_json * 1000);

            cityField.setText(String.format("%s, %s", json.getString("name").toUpperCase(Locale.US),
                    json.getJSONObject("sys").getString("country")));

            String location = latitude_json + ", " + longitude_json;
            Preferences.setStringPreferences(getContext(), "CITY_LOCATION", location);

            cityCoordinates.setText(getString(R.string.city_location, location));
            description.setText(StringFormater.capitalize(details.getString("description")));
            detailsField.setText(String.format("Wind                                %s km/h  %s°\nHumidity                         %s%%\nPressure                         %s mBar", String.format("%.0f", wind.getDouble("speed")), String.format("%.0f", wind.getDouble("deg")), main.getString("humidity"), String.format("%.0f", main.getDouble("pressure"))));
            detailsPressure.setText(String.format("Sea level pressure                       %s mBar\nGround level pressure                 %s mBar", String.format("%.0f", main.getDouble("sea_level")), String.format("%.0f", main.getDouble("grnd_level"))));
            currentTemperatureField.setText(String.format("%.0f", main.getDouble("temp")));
            currentTemperatureType.setText(" ℃");
            minMaxTemperatureField.setText(String.format("min: %s ℃\nmax: %s ℃",
                    String.format("%.2f", main.getDouble("temp_min") - 0.5),
                    String.format("%.2f", main.getDouble("temp_max") + 0.5)));

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            updatedField.setText(updatedOn);

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa", Locale.US);
            sunrise.setText(dateFormat.format(CityTimeZone.getCorrectTime(
                    latitude_json, longitude_json, sunrise_json, getString(R.string.google_maps_api_key))));
            sunset.setText(dateFormat.format(CityTimeZone.getCorrectTime(
                    latitude_json, longitude_json, sunset_json, getString(R.string.google_maps_api_key))));


        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
            Toast.makeText(getActivity(),
                    getActivity().getString(R.string.try_again),
                    Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Set weather icon and backgrounds on layout that respond params:
     *
     * @param actualId , id from weather object of json response
     * @param sunrise  , sunrise timestamp from json response
     * @param sunset   , sunset timestamp from json response
     */
    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;

        long currentTime = new Date().getTime();
        // Conditions for day time weather
        if (currentTime >= sunrise && currentTime < sunset) {
            if (actualId == 800) {
                im.setImageResource(R.drawable.clear_d);
                rl.setBackgroundResource(R.drawable.clear_d_bg);
            } else {
                switch (id) {
                    case 2:
                        im.setImageResource(R.drawable.chanceofstorm_d);
                        rl.setBackgroundResource(R.drawable.rainy_d_n_bg);
                        break;
                    case 3:
                        im.setImageResource(R.drawable.chanceofrain_d);
                        rl.setBackgroundResource(R.drawable.partly_cloudy_d_bg);
                        break;
                    case 5:
                        im.setImageResource(R.drawable.rain_dn);
                        rl.setBackgroundResource(R.drawable.rainy_d_n_bg);
                        break;
                    case 6:
                        im.setImageResource(R.drawable.chanceofsnow_d);
                        rl.setBackgroundResource(R.drawable.snow_d_n_bg);
                        break;
                    case 7:
                        im.setImageResource(R.drawable.fog_dn);
                        rl.setBackgroundResource(R.drawable.haze_d_n_bg);
                        break;
                    case 8:
                        im.setImageResource(R.drawable.mostly_cloudy_d);
                        rl.setBackgroundResource(R.drawable.moustly_cloudy_d_bg);
                        break;
                    default:
                        break;
                }
            }
            // Conditions for night time weather
        } else {
            if (actualId == 800) {
                im.setImageResource(R.drawable.clear_n);
                rl.setBackgroundResource(R.drawable.clear_n_bg);
            } else {
                switch (id) {
                    case 2:
                        im.setImageResource(R.drawable.chanceofstorm_n);
                        rl.setBackgroundResource(R.drawable.rainy_d_n_bg);
                        break;
                    case 3:
                        im.setImageResource(R.drawable.chanceofrain_n);
                        rl.setBackgroundResource(R.drawable.partly_cloudy_n_bg);
                        break;
                    case 5:
                        im.setImageResource(R.drawable.rain_dn);
                        rl.setBackgroundResource(R.drawable.rainy_d_n_bg);
                        break;
                    case 6:
                        im.setImageResource(R.drawable.chanceofsnow_n);
                        rl.setBackgroundResource(R.drawable.snow_d_n_bg);
                        break;
                    case 7:
                        im.setImageResource(R.drawable.fog_dn);
                        rl.setBackgroundResource(R.drawable.haze_d_n_bg);
                        break;
                    case 8:
                        im.setImageResource(R.drawable.mostly_cloudy_n);
                        rl.setBackgroundResource(R.drawable.moustly_cloudy_n_bg);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        updateWeatherData(Preferences.getStringPreferences(getContext(), "CITY_NAME"));
        super.onResume();
    }
}