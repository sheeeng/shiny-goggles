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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mTextViewQueryResults;
    private TextView mTextViewErrorMessage;
    private ProgressBar mProgressBarQuery;
    private List<Movie> listMovies = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewQueryResults = (TextView) findViewById(R.id.tv_query_results);
        mTextViewErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mProgressBarQuery = (ProgressBar) findViewById(R.id.pb_query);
        queryMoviesDb(MovieCategories.POPULAR);
    }

    public boolean isOnline() {
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
                mTextViewQueryResults.setText("");
                queryMoviesDb(MovieCategories.NOW_PLAYING);
                return true;
            case R.id.action_query_popular_movies:
                mTextViewQueryResults.setText("");
                queryMoviesDb(MovieCategories.POPULAR);
                return true;
            case R.id.action_query_top_rated_movies:
                mTextViewQueryResults.setText("");
                queryMoviesDb(MovieCategories.TOP_RATED);
                return true;
            case R.id.action_query_upcoming_movies:
                mTextViewQueryResults.setText("");
                queryMoviesDb(MovieCategories.UPCOMING);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showJsonDataView() {
        mTextViewQueryResults.setVisibility(View.VISIBLE);
        mTextViewErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mTextViewErrorMessage.setVisibility(View.VISIBLE);
        mTextViewQueryResults.setVisibility(View.INVISIBLE);
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
            if (isOnline()) {
                Log.d(TAG, "Query working while online.");
                try {
                    moviesQueryResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "Query will not work while offline.");
            }
            return moviesQueryResults;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressBarQuery.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                mTextViewQueryResults.setText(s);
                showJsonDataView();
                analyzeJson(s);
            } else {
                showErrorMessage();
            }
        }

        void analyzeJson(String stringJson) {
            if (!listMovies.isEmpty()) {
                listMovies.clear();
            }

            try {
                JSONObject jsonObjectResults = new JSONObject(stringJson);
                JSONArray jsonArrayMovies = jsonObjectResults.getJSONArray("results");

                for(int i=0; i<jsonArrayMovies.length(); i++){
                    JSONObject jsonObjectMovie = jsonArrayMovies.getJSONObject(i);

                    Movie movie = new Movie(
                            jsonObjectMovie.getString("title"),
                            jsonObjectMovie.getString("release_date"),
                            jsonObjectMovie.getString("poster_path"),
                            jsonObjectMovie.getString("vote_average"),
                            jsonObjectMovie.getString("overview"));
                    listMovies.add(movie);
                }

                for (int i = 0; i< listMovies.size(); i++) {
                    Log.d(TAG, listMovies.get(i).title);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


