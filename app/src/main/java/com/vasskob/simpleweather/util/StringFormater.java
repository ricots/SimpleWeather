package com.vasskob.simpleweather.util;

public class StringFormater {


    /**
     * Capitalize each word in the initial  param string
     *
     * @param string , string that need to be capitalize (first char toUpperCase of each word)
     * @return string in capitalize format
     */
    public static String capitalize(String string) {

        String[] words = string.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0))).append(words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        return sb.toString();
    }
}
