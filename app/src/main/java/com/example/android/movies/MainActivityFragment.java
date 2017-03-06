package com.example.android.movies;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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

    private String sortOption = "now_playing";

    private ArrayList<Movie> mMovies = null;

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
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_main, menu);

        MenuItem menuItem = menu.findItem(R.id.s_movie_categories);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(menuItem);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.movie_categories,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);
    }

    private AdapterView.OnItemSelectedListener spinnerOnItemSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (parent == null) {
                        Log.w(TAG, "AdapterView is NULL!");
                        return;
                    }

                    if (view == null) {
                        Log.w(TAG, "View is NULL!");
                        return;
                    }

                    Log.d(TAG, "POS: " + pos);
                    Log.d(TAG, "ID: " + id);

                    Log.d(TAG, parent.getClass().getName());
                    Log.d(TAG, view.getClass().getName());

                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                    String selectedCategory = parent.getItemAtPosition(pos).toString();
                    Log.d(TAG, selectedCategory);

                    String selectedValue = getResources().getStringArray(
                            R.array.movie_categories_values)[parent.getSelectedItemPosition()];
                    Log.d(TAG, selectedValue);

                    Log.d(TAG, "Spinner: " + String.valueOf(parent.getSelectedItemPosition()));

                    switch (parent.getSelectedItemPosition()) {
                        case 0:
                            sortOption = MovieCategories.NOW_PLAYING.toString().toLowerCase();
                            updateMovies(MovieCategories.NOW_PLAYING.toString().toLowerCase());
                            break;
                        case 1:
                            sortOption = MovieCategories.POPULAR.toString().toLowerCase();
                            updateMovies(MovieCategories.POPULAR.toString().toLowerCase());
                            break;
                        case 2:
                            sortOption = MovieCategories.TOP_RATED.toString().toLowerCase();
                            updateMovies(MovieCategories.TOP_RATED.toString().toLowerCase());
                            break;
                        case 3:
                            sortOption = MovieCategories.UPCOMING.toString().toLowerCase();
                            updateMovies(MovieCategories.UPCOMING.toString().toLowerCase());
                            break;
                        case 4:
                            sortOption = MovieCategories.FAVORITES.toString().toLowerCase();
                            updateMovies(MovieCategories.FAVORITES.toString().toLowerCase());
                            break;

                        default:
                            Log.w(TAG, "Unknown option.");
                            sortOption = MovieCategories.NOW_PLAYING.toString().toLowerCase();
                            updateMovies(MovieCategories.NOW_PLAYING.toString().toLowerCase());
                            break;
                    }
                }

                public void onNothingSelected(AdapterView parent) {
                    // Do nothing.
                }
            };

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
            if (savedInstanceState.containsKey("sort_setting")) {
                sortOption = savedInstanceState.getString("sort_setting");
            }

            if (savedInstanceState.containsKey("movies")) {
                mMovies = savedInstanceState.getParcelableArrayList("movies");
                movieAdapter.setData(mMovies);
            } else {
                updateMovies(sortOption);
            }
        } else {
            updateMovies(sortOption);
        }
    }

    private void updateMovies(String sort_by) {
        if (sort_by.contentEquals("favorites")) {
            new FetchFavoriteMoviesTask(getActivity(), this).execute();
        } else {
            new FetchMoviesTask(this).execute(sort_by);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!sortOption.contentEquals("popular")) {
            outState.putString("sort_setting", sortOption);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList("movies", mMovies);
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

