package com.vasskob.simpleweather.util;

import com.google.maps.GeoApiContext;
import com.google.maps.TimeZoneApi;
import com.google.maps.model.LatLng;

public class CityTimeZone {

    /**
     * Return corrected timestamp for sunrise or sunset
     *
     * @param lat            city latitude
     * @param lon            city longitude
     * @param sunrise_sunset city sunrise or sunset
     * @param api_key        google map api key
     * @return long, corrected time stamp
     */
    public static long getCorrectTime(double lat, double lon, long sunrise_sunset, String api_key) {

        GeoApiContext context1 = new GeoApiContext().setApiKey(api_key);

        LatLng latLng = new LatLng(lat, lon);
        java.util.TimeZone results = TimeZoneApi.getTimeZone(context1, latLng).awaitIgnoreError();
        results.getRawOffset();

        return sunrise_sunset * 1000 +
                results.getRawOffset() - java.util.TimeZone.getDefault().getRawOffset();

    }
}
