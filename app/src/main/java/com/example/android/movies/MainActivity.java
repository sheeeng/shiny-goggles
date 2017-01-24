package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements MovieAdapter.ItemClickListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MovieResults movieResults;
    private ProgressBar progressBarQuery;
    private Toast toast;
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerViewMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarQuery = (ProgressBar) findViewById(R.id.pb_query);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        recyclerViewMovies = (RecyclerView) findViewById(R.id.rv_movies);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewMovies.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        recyclerViewMovies.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for displaying each item in the list.
         */
        movieAdapter = new MovieAdapter(this);
        recyclerViewMovies.setAdapter(movieAdapter);

        movieResults = new MovieResults();

        queryMoviesDb(MovieCategories.NOW_PLAYING);
    }

    private void showToastNow(String message) {
        if ( toast != null ) {
            toast.cancel();
        }

        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onItemClick(int clickedItemIndex, Movie movie) {
        if (isConnectivityAvailable() == false) {
            showToastNow(getString(R.string.error_message_query_fail));
            return;
        }

        Intent intentMovieDetails = new Intent(
                MainActivity.this, MovieDetailsActivity.class);
        intentMovieDetails.putExtra(Constants.INTENT.MOVIE_DETAILS, movie);
        startActivity(intentMovieDetails);
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

    public void onCompleteMoviesQueryTask (String jsonData) {
        try {
            movieResults.setListMovies(getJsonResults(jsonData));
            for(int i = 0; i< movieResults.getListMovies().size(); i++) {
                Log.d(LOG_TAG, movieResults.getListMovies().get(i).getTitle());
            }
            movieAdapter.setMovieList(movieResults.getListMovies());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Movie> getJsonResults(String jsonData) throws JSONException {
        JSONObject resultsData = new JSONObject(jsonData);
        JSONArray results = resultsData.getJSONArray("results");
        List<Movie> listMovies = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject jsonMovie = results.getJSONObject(i);
            listMovies.add(new Movie(jsonMovie));
        }
        return listMovies;
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarQuery.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String moviesQueryResults = null;
            if (isConnectivityAvailable()) {
                try {
                    moviesQueryResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, getString(R.string.error_message_query_fail));
                runOnUiThread(new Runnable() {
                    public void run() {
                        showToastNow(getString(R.string.error_message_query_fail));
                    }
                });
            }
            return moviesQueryResults;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressBarQuery.setVisibility(View.INVISIBLE);

            if (s != null && !s.equals("")) {
                onCompleteMoviesQueryTask(s);
            }
        }
    }
}


