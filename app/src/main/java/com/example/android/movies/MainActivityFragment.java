package com.example.android.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.movies.adapters.MovieAdapter;
import com.example.android.movies.models.Movie;
import com.example.android.movies.tasks.FetchFavoriteMoviesTask;
import com.example.android.movies.tasks.FetchMoviesTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment
        extends Fragment
        implements
        FetchMoviesTask.FetchMoviesTaskInterfaces,
        FetchFavoriteMoviesTask.FetchFavoriteMoviesTaskInterfaces {
    static final String TAG = MainActivityFragment.class.getSimpleName();

    private ProgressBar progressBarQuery;
    private GridView gridViewMovies;
    private MovieAdapter movieAdapter;

    private SharedPreferences sharedPreferences;
    private MovieCategories movieCategory = MovieCategories.NOW_PLAYING;
    private int moviePosition = -1;

    private ArrayList<Movie> arrayListMovies = null;

    private Toast toast;

    public MainActivityFragment() {

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        void onItemSelected(Movie movie, MovieCategories movieCategory, int moviePosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);  // If true, the fragment has menu items to contribute.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_main, menu);

        MenuItem action_query_now_playing_movies = menu.findItem(R.id.action_query_now_playing_movies);
        MenuItem action_query_popular_movies = menu.findItem(R.id.action_query_popular_movies);
        MenuItem action_query_top_rated_movies = menu.findItem(R.id.action_query_top_rated_movies);
        MenuItem action_query_upcoming_movies = menu.findItem(R.id.action_query_upcoming_movies);
        MenuItem action_query_favorite_movies = menu.findItem(R.id.action_query_favorite_movies);

        switch (movieCategory) {
            case NOW_PLAYING:
                action_query_now_playing_movies.setChecked(true);
                break;
            case POPULAR:
                action_query_popular_movies.setChecked(true);
                break;
            case TOP_RATED:
                action_query_top_rated_movies.setChecked(true);
                break;
            case UPCOMING:
                action_query_upcoming_movies.setChecked(true);
                break;
            case FAVORITES:
                action_query_favorite_movies.setChecked(true);
                break;

            default:
                Log.w(TAG, "Unknown movie categories.");
                action_query_now_playing_movies.setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isConnectivityAvailable()) {
            Log.d(TAG, getString(R.string.error_message_query_fail));
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    showToastNow(getString(R.string.error_message_query_fail));
                }
            });
        }

        moviePosition = -1;

        switch (item.getItemId()) {
            case R.id.action_query_now_playing_movies:
                item.setChecked(true);
                movieCategory = MovieCategories.NOW_PLAYING;
                updateMovies(movieCategory);
                return true;
            case R.id.action_query_popular_movies:
                item.setChecked(true);
                movieCategory = MovieCategories.POPULAR;
                updateMovies(movieCategory);
                return true;
            case R.id.action_query_top_rated_movies:
                item.setChecked(true);
                movieCategory = MovieCategories.TOP_RATED;
                updateMovies(movieCategory);
                return true;
            case R.id.action_query_upcoming_movies:
                item.setChecked(true);
                movieCategory = MovieCategories.UPCOMING;
                updateMovies(movieCategory);
                return true;
            case R.id.action_query_favorite_movies:
                item.setChecked(true);
                movieCategory = MovieCategories.FAVORITES;
                updateMovies(movieCategory);
                return true;
            default:
                Log.w(TAG, "Unknown item selected.");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBarQuery = (ProgressBar) view.findViewById(R.id.pb_query);

        gridViewMovies = (GridView) view.findViewById(R.id.movie_gridview);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        gridViewMovies.setAdapter(movieAdapter);

        gridViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
                moviePosition = position;
                ((Callback) getActivity()).onItemSelected(movie, movieCategory, moviePosition);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(
                    getString(R.string.saved_instance_movies))) {
                arrayListMovies = savedInstanceState.getParcelableArrayList(
                        getString(R.string.saved_instance_movies));
                movieAdapter.setData(arrayListMovies);
            }
        }

        movieCategory = MovieCategories.valueOf(
                sharedPreferences.getString(
                        getString(R.string.saved_instance_movie_category),
                        MovieCategories.NOW_PLAYING.toString()));

        moviePosition = sharedPreferences.getInt(
                getString(R.string.saved_instance_movie_position),
                -1);

        updateMovies(movieCategory);
    }


    public boolean isConnectivityAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void showToastNow(String message) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.show();
    }


    private void updateMovies(MovieCategories movieCategories) {
        if (!isConnectivityAvailable()) {
            Log.d(TAG, getString(R.string.error_message_query_fail));
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    showToastNow(getString(R.string.error_message_query_fail));
                }
            });
        }

        if (movieCategories == MovieCategories.FAVORITES) {
            new FetchFavoriteMoviesTask(getActivity(), this).execute();
        } else {
            new FetchMoviesTask(this).execute(movieCategories);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "The onSaveInstanceState() called.");

        if (arrayListMovies != null) {
            outState.putParcelableArrayList(
                    getString(R.string.saved_instance_movies),
                    arrayListMovies);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(
                getString(R.string.saved_instance_movie_category),
                movieCategory.toString());

        editor.putInt(
                getString(R.string.saved_instance_movie_position),
                moviePosition);

        editor.apply();
    }


    public void selectFirstOrPreviousMovieItem() {
        if (moviePosition != GridView.INVALID_POSITION) {
            gridViewMovies.smoothScrollToPosition(moviePosition);
            gridViewMovies.performItemClick(
                    gridViewMovies.getAdapter().getView(moviePosition, null, null),
                    moviePosition,
                    gridViewMovies.getAdapter().getItemId(moviePosition));
        } else {
            if (getActivity().getSupportFragmentManager().findFragmentByTag(
                    MovieDetailsFragment.TAG) != null) {
                new Handler().post(new Runnable() {
                    public void run() {
                        gridViewMovies.smoothScrollToPosition(0);
                        gridViewMovies.performItemClick(
                                gridViewMovies.getAdapter().getView(0, null, null),
                                0,
                                gridViewMovies.getAdapter().getItemId(0));
                    }});
            }
        }
    }


    @Override
    public void onFetchMoviesTaskPreExecute() {
        Log.d(TAG, "onFetchMoviesTaskPreExecute() completed.");
        if (progressBarQuery != null) {
            progressBarQuery.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFetchMoviesTaskPostExecute(List<Movie> movies) {
        Log.d(TAG, "onFetchMoviesTaskPostExecute() completed.");
        if (progressBarQuery != null) {
            progressBarQuery.setVisibility(View.INVISIBLE);
        }

        if (movies != null) {
            if (movieAdapter != null) {
                movieAdapter.setData(movies);
            }
            arrayListMovies = new ArrayList<>();
            arrayListMovies.addAll(movies);
            selectFirstOrPreviousMovieItem();
        }
    }


    @Override
    public void onFetchFavoriteMoviesTaskPreExecute() {
        Log.d(TAG, "onFetchFavoriteMoviesTaskPreExecute() completed.");
        if (progressBarQuery != null) {
            progressBarQuery.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFetchFavoriteMoviesTaskPostExecute(List<Movie> movies) {
        Log.d(TAG, "onFetchFavoriteMoviesTaskPostExecute() completed.");
        if (progressBarQuery != null) {
            progressBarQuery.setVisibility(View.INVISIBLE);
        }

        if (movies != null) {
            if (movieAdapter != null) {
                movieAdapter.setData(movies);
            }
            arrayListMovies = new ArrayList<>();
            arrayListMovies.addAll(movies);
            selectFirstOrPreviousMovieItem();
        }
    }
}

