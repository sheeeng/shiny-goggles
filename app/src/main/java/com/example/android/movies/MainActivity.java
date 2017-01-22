package com.example.android.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ProgressBar mProgressBarQuery;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mListMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBarQuery = (ProgressBar) findViewById(R.id.pb_query);
        queryMoviesDb(MovieCategories.POPULAR);
    }

    public boolean isConnectivityAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void queryMoviesDb(MovieCategories movieCategories) {
        URL moviesQueryUrl = NetworkUtilities.buildUrl(movieCategories);
        new MoviesQueryTask().execute(moviesQueryUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.query, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_query_now_playing_movies:
                queryMoviesDb(MovieCategories.NOW_PLAYING);
                return true;
            case R.id.action_query_popular_movies:
                queryMoviesDb(MovieCategories.POPULAR);
                return true;
            case R.id.action_query_top_rated_movies:
                queryMoviesDb(MovieCategories.TOP_RATED);
                return true;
            case R.id.action_query_upcoming_movies:
                queryMoviesDb(MovieCategories.UPCOMING);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBarQuery.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String moviesQueryResults = null;
            if (isConnectivityAvailable()) {
                Log.d(LOG_TAG, "Query working while online.");
                try {
                    moviesQueryResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, "Query will not work while offline.");
            }
            return moviesQueryResults;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressBarQuery.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                try {
                    mListMovies = getMovies(s);
                    for(int i=0; i<mListMovies.size(); i++) {
                        Log.d(LOG_TAG, mListMovies.get(i).title);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private List<Movie> getMovies(String jsonData) throws JSONException {
            JSONObject resultsData = new JSONObject(jsonData);
            JSONArray results = resultsData.getJSONArray("results");

            List<Movie> listMovies = new ArrayList<>();

            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonMovie = results.getJSONObject(i);
                Movie movie = new Movie(
                        jsonMovie.getString("title"),
                        jsonMovie.getString("release_date"),
                        jsonMovie.getString("poster_path"),
                        jsonMovie.getString("vote_average"),
                        jsonMovie.getString("overview"));
                listMovies.add(movie);
            }

            return listMovies;
        }
    }
}


