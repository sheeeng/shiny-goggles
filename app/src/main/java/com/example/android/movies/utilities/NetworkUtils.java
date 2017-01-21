package com.example.android.movies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    static final String TAG = NetworkUtils.class.getSimpleName();
    final static String MOVIEDB_BASE_URL =
            "http://api.themoviedb.org/3/movie/popular?";
    final static String PARAM_API_KEY_QUERY = "api_key";
    final static String API_KEY =
            "";  // TODO: Replace with your own API key.

    /**
     * Builds the URL used to query MovieDb.
     *
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl() {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d(TAG, url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}