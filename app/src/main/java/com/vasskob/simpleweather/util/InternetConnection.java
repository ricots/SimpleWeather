package com.vasskob.simpleweather.util;

import java.net.InetAddress;


public class InternetConnection {
    /**
     * Check internet connection
     *
     * @return true if is and false if is`nt
     */
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddress = InetAddress.getByName("google.com");

            return !ipAddress.toString().equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
