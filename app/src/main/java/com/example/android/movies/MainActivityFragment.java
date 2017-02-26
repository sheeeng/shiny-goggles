package com.example.android.movies;

import android.os.Bundle;
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

import com.example.android.movies.adapters.MovieAdapter;
import com.example.android.movies.models.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityFragment
        extends Fragment
        implements FetchMoviesTask.FetchMoviesTaskInterfaces,
            FetchFavoriteMoviesTask.FetchFavoriteMoviesTaskInterfaces {
    static final String TAG = MainActivityFragment.class.getSimpleName();

    @BindView(R.id.pb_query) ProgressBar progressBarQuery;
    private GridView gridView;
    private MovieAdapter movieAdapter;

    private static final String SORT_SETTING_KEY = "sort_setting";

    private static final String NOW_PLAYING_DESC = "now_playing.desc";
    private static final String POPULAR_DESC = "popularity.desc";
    private static final String TOP_RATED_DESC = "top_rated.desc";
    private static final String UPCOMING_DESC = "upcoming.desc";

    private static final String FAVORITE = "favorite";
    private static final String MOVIES_KEY = "movies";

    private String mSortBy = POPULAR_DESC;

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
        ButterKnife.bind(this.getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);

        MenuItem action_sort_by_now_playing = menu.findItem(R.id.action_sort_by_now_playing);
        MenuItem action_sort_by_popular = menu.findItem(R.id.action_sort_by_popular);
        MenuItem action_sort_by_top_rated = menu.findItem(R.id.action_sort_by_top_rated);
        MenuItem action_sort_by_upcoming = menu.findItem(R.id.action_sort_by_upcoming);

        MenuItem action_sort_by_favorite = menu.findItem(R.id.action_sort_by_favorite);

        switch (mSortBy) {
            case NOW_PLAYING_DESC:
                if (!action_sort_by_now_playing.isChecked()) {
                    action_sort_by_now_playing.setChecked(true);
                }
                break;
            case POPULAR_DESC:
                if (!action_sort_by_popular.isChecked()) {
                    action_sort_by_popular.setChecked(true);
                }
                break;
            case TOP_RATED_DESC:
                if (!action_sort_by_top_rated.isChecked()) {
                    action_sort_by_top_rated.setChecked(true);
                }
                break;
            case UPCOMING_DESC:
                if (!action_sort_by_upcoming.isChecked()) {
                    action_sort_by_upcoming.setChecked(true);
                }
                break;
            case FAVORITE:
                if (!action_sort_by_favorite.isChecked()) {
                    action_sort_by_favorite.setChecked(true);
                }
                break;
            default:
                Log.d(TAG, "Unknown sort option.");
                super.onCreateOptionsMenu(menu, inflater);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by_now_playing:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = NOW_PLAYING_DESC;
                updateMovies(mSortBy);
                return true;
            case R.id.action_sort_by_popular:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = POPULAR_DESC;
                updateMovies(mSortBy);
                return true;
            case R.id.action_sort_by_top_rated:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = TOP_RATED_DESC;
                updateMovies(mSortBy);
                return true;
            case R.id.action_sort_by_upcoming:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = UPCOMING_DESC;
                updateMovies(mSortBy);
                return true;
            case R.id.action_sort_by_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = FAVORITE;
                updateMovies(mSortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

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
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                mSortBy = savedInstanceState.getString(SORT_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                movieAdapter.setData(mMovies);
            } else {
                updateMovies(mSortBy);
            }
        } else {
            updateMovies(mSortBy);
        }

        return view;
    }

    private void updateMovies(String sort_by) {
        if (sort_by.contentEquals(FAVORITE)) {
            new FetchFavoriteMoviesTask(getActivity(), this).execute();
        } else {
            new FetchMoviesTask(this).execute(sort_by);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mSortBy.contentEquals(POPULAR_DESC)) {
            outState.putString(SORT_SETTING_KEY, mSortBy);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onFetchMoviesTaskPreExecute() {
        if (progressBarQuery != null) {
            progressBarQuery.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFetchMoviesTaskPostExecute(List<Movie> movies) {
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
        if (progressBarQuery != null) {
            progressBarQuery.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFetchFavoriteMoviesTaskPostExecute(List<Movie> movies) {
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

