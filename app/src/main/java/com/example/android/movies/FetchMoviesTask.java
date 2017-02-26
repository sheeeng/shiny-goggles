package com.example.android.movies;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.movies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
    private final String TAG = FetchMoviesTask.class.getSimpleName();

    private FetchMoviesTaskInterfaces fetchMoviesTaskInterfacesListener;

    public interface FetchMoviesTaskInterfaces {
        void onFetchMoviesTaskPreExecute();
        void onFetchMoviesTaskPostExecute(List<Movie> movies);
    }

    public FetchMoviesTask(FetchMoviesTaskInterfaces fetchMoviesTaskInterfacesListener) {
        this.fetchMoviesTaskInterfacesListener = fetchMoviesTaskInterfacesListener;
    }

    private List<Movie> getMoviesDataFromJson(String jsonStr) throws JSONException {
        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");

        List<Movie> results = new ArrayList<>();

        for(int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            Movie movieModel = new Movie(movie);
            results.add(movieModel);
        }

        Log.d(TAG, results.toString());
        return results;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {
            URL url;
            String categorySelected = params[0];
            if (categorySelected.contains("now_playing")) {
                url = NetworkUtilities.buildUrl(MovieCategories.NOW_PLAYING);
            } else if (categorySelected.contains("popular")) {
                url = NetworkUtilities.buildUrl(MovieCategories.POPULAR);
            } else if (categorySelected.contains("top_rated")) {
                url = NetworkUtilities.buildUrl(MovieCategories.TOP_RATED);
            } else if (categorySelected.contains("upcoming")) {
                url = NetworkUtilities.buildUrl(MovieCategories.UPCOMING);
            } else {
                url = NetworkUtilities.buildUrl(MovieCategories.POPULAR);
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMoviesDataFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (fetchMoviesTaskInterfacesListener != null) {
            fetchMoviesTaskInterfacesListener.onFetchMoviesTaskPreExecute();
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        if (fetchMoviesTaskInterfacesListener != null) {
            fetchMoviesTaskInterfacesListener.onFetchMoviesTaskPostExecute(movies);
        }
    }
}