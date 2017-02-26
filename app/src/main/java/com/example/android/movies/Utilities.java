package com.example.android.movies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.movies.databases.MovieContract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Utilities {
    static final String TAG = Utilities.class.getSimpleName();

    // E.g. "http://api.themoviedb.org/3/movie/now_playing?"
    final static String MOVIEDB_API_URL = "api.themoviedb.org";
    final static String NOW_PLAYING = "now_playing";
    final static String POPULAR = "popular";
    final static String TOP_RATED = "top_rated";
    final static String UPCOMING = "upcoming";
    final static String PARAM_API_KEY_QUERY = "api_key";

    /**
     * Builds the URL used to query MovieDb.
     *
     * @param movieCategories The categories of movies to be queried.
     * @return The URL to use to query the movie database server.
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
            Log.w(TAG, "Unknown option.");
            uriBuilder.appendPath(NOW_PLAYING);
        }

        uriBuilder.appendQueryParameter(PARAM_API_KEY_QUERY, BuildConfig.MOVIEDB_API_KEY);
        Log.d(TAG, uriBuilder.toString());

        Uri builtUri = uriBuilder.build();
        Log.d(TAG, builtUri.toString());

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


    public static int isFavorite(Context context, int id) {
        if (context == null) {
            Log.w(TAG, "Context is null!");
            return 0;
        }

        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,  // uri
                null,  // projection
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TMDB_ID + " = ?",  // selection
                new String[] { Integer.toString(id) },  // selectionArgs
                null  // sortOrder
        );
        int rowCount = cursor.getCount();
        cursor.close();
        return rowCount;
    }


    public static String buildImageUrl(int width, String fileName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + fileName;
    }
}
