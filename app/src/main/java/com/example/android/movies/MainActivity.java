package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements MovieAdapter.ItemClickListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ArrayList<Movie> arrayListMovies;
    Spinner spinner;
    private ProgressBar progressBarQuery;
    private Toast toast;
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerViewMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null ||
                !savedInstanceState.containsKey(
                        getString(R.string.saved_instance_name))) {
            Log.d(LOG_TAG, "No previous saved instance.");
            arrayListMovies = new ArrayList<Movie>();
        } else {
            Log.d(LOG_TAG, "Previous saved instance found.");
            arrayListMovies = savedInstanceState.getParcelableArrayList(
                    getString(R.string.saved_instance_name));
        }

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

        queryMoviesDb(MovieCategories.NOW_PLAYING);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelableArrayList(
                getString(R.string.saved_instance_name),
                arrayListMovies);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private AdapterView.OnItemSelectedListener spinnerOnItemSelectedListener =
            new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

            String selectedCategory = parent.getItemAtPosition(pos).toString();
            Log.d(LOG_TAG, selectedCategory);

            String selectedValue = getResources().getStringArray(
                    R.array.movie_categories_values)[parent.getSelectedItemPosition()];
            Log.d(LOG_TAG, selectedValue);

            Log.d(LOG_TAG, "Spinner item position selected: " + String.valueOf(parent.getSelectedItemPosition()));

            switch (parent.getSelectedItemPosition()) {
                case 0:
                    queryMoviesDb(MovieCategories.NOW_PLAYING);
                    break;
                case 1:
                    queryMoviesDb(MovieCategories.POPULAR);
                    break;
                case 2:
                    queryMoviesDb(MovieCategories.TOP_RATED);
                    break;
                case 3:
                    queryMoviesDb(MovieCategories.UPCOMING);
                    break;
            }
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    };

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

        MenuItem menuItem = menu.findItem(R.id.s_movie_categories);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(menuItem);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movie_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);

        return super.onCreateOptionsMenu(menu);
    }

    public void onCompleteMoviesQueryTask (String jsonData) {
        try {
            arrayListMovies = getJsonResults(jsonData);
//            for(int i = 0; i< arrayListMovies.size(); i++) {
//                Log.d(LOG_TAG, arrayListMovies.get(i).getTitle());
//            }
            movieAdapter.setMovieList(arrayListMovies);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Movie> getJsonResults(String jsonData) throws JSONException {
        JSONObject resultsData = new JSONObject(jsonData);
        JSONArray results = resultsData.getJSONArray("results");
        ArrayList<Movie> arrayListMovies = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject jsonMovie = results.getJSONObject(i);
            arrayListMovies.add(new Movie(jsonMovie));
        }
        return arrayListMovies;
    }

    /*
    UDACITY_REVIEW TODO
    In order to make your codes reusable and structural, you can consider to refactor your codes
    and put this class in a separate Java file.
     */
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


