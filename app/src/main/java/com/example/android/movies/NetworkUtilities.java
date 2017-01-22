package com.example.android.movies;

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
public class NetworkUtilities {
    static final String LOG_TAG = NetworkUtilities.class.getSimpleName();

    final static String MOVIEDB_API_URL =
            "api.themoviedb.org";  // "http://api.themoviedb.org/3/movie/popular?";
    final static String NOW_PLAYING = "now_playing";
    final static String POPULAR = "popular";
    final static String TOP_RATED = "top_rated";
    final static String UPCOMING = "upcoming";
    final static String PARAM_API_KEY_QUERY = "api_key";
    final static String API_KEY =
            "";  // TODO: Replace with your own API key.

    /**
     * Builds the URL used to query MovieDb.
     *
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(MovieCategories movieCategories) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http");
        uriBuilder.authority(MOVIEDB_API_URL);
        uriBuilder.appendPath("3");
        uriBuilder.appendPath("movie");

        if (movieCategories.equals(MovieCategories.NOW_PLAYING)) {
            uriBuilder.appendPath(NOW_PLAYING);
        } else if (movieCategories.equals(MovieCategories.POPULAR)) {
            uriBuilder.appendPath(POPULAR);
        } else if (movieCategories.equals(MovieCategories.TOP_RATED)) {
            uriBuilder.appendPath(TOP_RATED);
        } else if (movieCategories.equals(MovieCategories.UPCOMING)) {
            uriBuilder.appendPath(UPCOMING);
        } else {
            uriBuilder.appendPath(NOW_PLAYING);
        }

        uriBuilder.appendQueryParameter(PARAM_API_KEY_QUERY, API_KEY);
        Log.d(LOG_TAG, uriBuilder.toString());

        Uri builtUri = uriBuilder.build();
        Log.d(LOG_TAG, builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d(LOG_TAG, url.toString());
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