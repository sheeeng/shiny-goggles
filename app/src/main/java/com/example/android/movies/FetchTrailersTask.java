package com.example.android.movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.android.movies.models.MovieVideo;

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

public class FetchTrailersTask extends AsyncTask<String, Void, List<MovieVideo>> {

    private final String TAG = FetchTrailersTask.class.getSimpleName();

    private FetchTrailersTaskInterfaces fetchTrailersTaskInterfacesListener;

    public interface FetchTrailersTaskInterfaces {
        void onFetchTrailersTaskPostExecute(List<MovieVideo> trailers);
    }

    public FetchTrailersTask(FetchTrailersTaskInterfaces fetchTrailersTaskInterfacesListener) {
        this.fetchTrailersTaskInterfacesListener = fetchTrailersTaskInterfacesListener;
    }

    private List<MovieVideo> getTrailersDataFromJson(String jsonStr) throws JSONException {
        JSONObject trailerJson = new JSONObject(jsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray("results");

        List<MovieVideo> results = new ArrayList<>();

        for(int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);
            // Only show Trailers which are on Youtube
            if (trailer.getString("site").contentEquals("YouTube")) {
                MovieVideo trailerModel = new MovieVideo(trailer);
                results.add(trailerModel);
            }
        }

        return results;
    }

    @Override
    protected List<MovieVideo> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

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
            return getTrailersDataFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(List<MovieVideo> trailers) {
        super.onPostExecute(trailers);
        if (fetchTrailersTaskInterfacesListener != null) {
            fetchTrailersTaskInterfacesListener.onFetchTrailersTaskPostExecute(trailers);
        }
    }
}