package com.example.android.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityFragment
        extends Fragment
        implements
        FetchMoviesTask.FetchMoviesTaskInterfaces,
        FetchFavoriteMoviesTask.FetchFavoriteMoviesTaskInterfaces {
    static final String TAG = MainActivityFragment.class.getSimpleName();

    @BindView(R.id.pb_query) ProgressBar progressBarQuery;
    private GridView gridView;
    private MovieAdapter movieAdapter;

    private MovieCategories selectedMovieCategories = MovieCategories.NOW_PLAYING;

    private ArrayList<Movie> mMovies = null;

    private Toast toast;

    public MainActivityFragment() {

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        void onItemSelected(Movie movie);
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

        switch (selectedMovieCategories) {
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
        switch (item.getItemId()) {
            case R.id.action_query_now_playing_movies:
                item.setChecked(true);
                selectedMovieCategories = MovieCategories.NOW_PLAYING;
                updateMovies(selectedMovieCategories);
                return true;
            case R.id.action_query_popular_movies:
                item.setChecked(true);
                selectedMovieCategories = MovieCategories.POPULAR;
                updateMovies(selectedMovieCategories);
                return true;
            case R.id.action_query_top_rated_movies:
                item.setChecked(true);
                selectedMovieCategories = MovieCategories.TOP_RATED;
                updateMovies(selectedMovieCategories);
                return true;
            case R.id.action_query_upcoming_movies:
                item.setChecked(true);
                selectedMovieCategories = MovieCategories.UPCOMING;
                updateMovies(selectedMovieCategories);
                return true;
            case R.id.action_query_favorite_movies:
                item.setChecked(true);
                selectedMovieCategories = MovieCategories.FAVORITES;
                updateMovies(selectedMovieCategories);
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

        ButterKnife.bind(this, view);

        gridView = (GridView) view.findViewById(R.id.movie_gridview);

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(
                    getString(R.string.saved_instance_movie_category))) {
                selectedMovieCategories = MovieCategories.valueOf(
                        savedInstanceState.getString(
                                getString(R.string.saved_instance_movie_category)));
            }

            if (savedInstanceState.containsKey(
                    getString(R.string.saved_instance_movie))) {
                mMovies = savedInstanceState.getParcelableArrayList(
                        getString(R.string.saved_instance_movie));
                movieAdapter.setData(mMovies);
            } else {
                updateMovies(selectedMovieCategories);
            }
        } else {
            updateMovies(selectedMovieCategories);
        }
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
        if (selectedMovieCategories != MovieCategories.NOW_PLAYING) {
            outState.putString(
                    getString(R.string.saved_instance_movie_category),
                    MovieCategories.NOW_PLAYING.toString());
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(getString(R.string.saved_instance_movie), mMovies);
        }
        super.onSaveInstanceState(outState);
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
            mMovies = new ArrayList<>();
            mMovies.addAll(movies);
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
            mMovies = new ArrayList<>();
            mMovies.addAll(movies);
        }
    }
}

