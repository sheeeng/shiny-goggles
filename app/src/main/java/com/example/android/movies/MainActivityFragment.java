package com.example.android.movies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.example.android.movies.adapters.MovieAdapter;
import com.example.android.movies.databases.MovieContract;
import com.example.android.movies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {
    static final String TAG = NetworkUtilities.class.getSimpleName();

    private GridView gridView;
    private MovieAdapter movieAdapter;

    private static final String SORT_SETTING_KEY = "sort_setting";

    private static final String POPULAR_DESC = "popularity.desc";
    private static final String TOP_RATED_DESC = "vote_average.desc";

    private static final String FAVORITE = "favorite";
    private static final String MOVIES_KEY = "movies";

    private String mSortBy = POPULAR_DESC;

    private ArrayList<Movie> mMovies = null;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_POSTER_PATH,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ADULT,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_OVERVIEW,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_RELEASE_DATE,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_GENRE_IDS,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TMDB_ID,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ORIGINAL_TITLE,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ORIGINAL_LANGUAGE,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TITLE,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_BACKDROP_PATH,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_POPULARITY,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VOTE_COUNT,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VIDEO,
            Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VOTE_AVERAGE
    };

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
        inflater.inflate(R.menu.menu_fragment_main, menu);

        MenuItem action_sort_by_popular = menu.findItem(R.id.action_sort_by_popular);
        MenuItem action_sort_by_top_rated = menu.findItem(R.id.action_sort_by_top_rated);
        MenuItem action_sort_by_favorite = menu.findItem(R.id.action_sort_by_favorite);

        switch (mSortBy) {
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
            new FetchFavoriteMoviesTask(getActivity()).execute();
        } else {
            new FetchMoviesTask().execute(sort_by);
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





    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private List<Movie> getMoviesDataFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<Movie> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                Movie movieModel = new Movie(movie);
                results.add(movieModel);
            }

            return results;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                if (movieAdapter != null) {
                    movieAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }





    public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        private Context mContext;

        public FetchFavoriteMoviesTask(Context context) {
            mContext = context;
        }

        private List<Movie> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            List<Movie> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,  //uri
                    MOVIE_COLUMNS,  // projection
                    null,  // selection
                    null,  // selectionArgs
                    null  // sortOrder
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                if (movieAdapter != null) {
                    movieAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }
}
