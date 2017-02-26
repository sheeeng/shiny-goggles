package com.example.android.movies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.movies.MovieCategories;
import com.example.android.movies.Utilities;
import com.example.android.movies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

        for(Movie movie : results) {
            Log.d(TAG, movie.getTitle());
        }
        return results;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        String jsonStr = null;

        try {
            URL url;
            String categorySelected = params[0];

            if (categorySelected.contains("now_playing")) {
                url = Utilities.buildUrl(MovieCategories.NOW_PLAYING);
            } else if (categorySelected.contains("popular")) {
                url = Utilities.buildUrl(MovieCategories.POPULAR);
            } else if (categorySelected.contains("top_rated")) {
                url = Utilities.buildUrl(MovieCategories.TOP_RATED);
            } else if (categorySelected.contains("upcoming")) {
                url = Utilities.buildUrl(MovieCategories.UPCOMING);
            } else {
                url = Utilities.buildUrl(MovieCategories.POPULAR);
            }

            jsonStr = Utilities.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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