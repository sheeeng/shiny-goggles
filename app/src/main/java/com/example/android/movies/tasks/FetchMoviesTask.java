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

public class FetchMoviesTask
        extends AsyncTask<MovieCategories, Void, List<Movie>> {
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
        /*
        for(Movie movie : results) {
            Log.d(TAG, movie.getTitle());
        }
        */
        return results;
    }

    @Override
    protected List<Movie> doInBackground(MovieCategories... params) {

        if (params.length == 0) {
            return null;
        }

        String jsonStr;

        try {
            URL url;
            MovieCategories selectedMovieCategories = params[0];

            switch (selectedMovieCategories) {
                case NOW_PLAYING:
                    url = Utilities.buildUrl(MovieCategories.NOW_PLAYING);
                    break;
                case POPULAR:
                    url = Utilities.buildUrl(MovieCategories.POPULAR);
                    break;
                case TOP_RATED:
                    url = Utilities.buildUrl(MovieCategories.TOP_RATED);
                    break;
                case UPCOMING:
                    url = Utilities.buildUrl(MovieCategories.UPCOMING);
                    break;

                default:
                    Log.w(TAG, "Unknown movie category.");
                    url = Utilities.buildUrl(MovieCategories.NOW_PLAYING);
                    break;
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