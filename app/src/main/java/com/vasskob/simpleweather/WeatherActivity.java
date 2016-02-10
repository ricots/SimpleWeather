package com.vasskob.simpleweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.vasskob.simpleweather.util.Preferences;

public class WeatherActivity extends AppCompatActivity {

    private final String LOG_TAG = WeatherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.change_city) {
            showInputDialog();
            return true;
        }
        if (id == R.id.map_location) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * show city in google maps
     */
    private void openPreferredLocationInMap() {

        String location = Preferences.getStringPreferences(getApplicationContext(),
                "CITY_LOCATION");

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geolocation = Uri.parse(String.format("geo:%s?z=%s", location, 13)).buildUpon().build();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(geolocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    /**
     * show input dialog for changing city
     */
    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.change_city));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // replace all empty spaces
                changeCity(input.getText().toString().replaceAll("\\s+", ""));
            }
        });
        builder.show();
    }

    /**
     * Change city for daily weather
     *
     * @param city name of city to show daily weather
     */
    private void changeCity(String city) {
        if (city != null && !city.isEmpty()) {
            WeatherFragment wf = (WeatherFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
            wf.updateWeatherData(city);
            Preferences.setStringPreferences(getApplicationContext(),
                    "CITY_NAME", city);
        }
    }
}