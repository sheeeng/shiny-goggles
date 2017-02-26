package com.example.android.movies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.movies.adapters.MovieReviewAdapter;
import com.example.android.movies.adapters.MovieVideoAdapter;
import com.example.android.movies.databases.MovieContract;
import com.example.android.movies.models.Movie;
import com.example.android.movies.models.MovieReview;
import com.example.android.movies.models.MovieVideo;
import com.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailsFragment
        extends Fragment {
    public static final String TAG = MovieDetailsFragment.class.getSimpleName();

    static final String DETAIL_MOVIE = "MOVIE_DETAILS";

    private Movie mMovie;

    private ImageView mImageView;

    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mDateView;
    private TextView mVoteAverageView;

    private LinearListView mTrailersView;
    private LinearListView mReviewsView;

    private CardView cardViewReview;
    private CardView cardViewVideo;

    private MovieVideoAdapter movieVideoAdapter;
    private MovieReviewAdapter movieReviewAdapter;

    private ScrollView mDetailLayout;

    private Toast mToast;

    private ShareActionProvider mShareActionProvider;

    private MovieVideo movieVideo;

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mMovie != null) {
            inflater.inflate(R.menu.menu_fragment_detail, menu);

            final MenuItem action_favorite = menu.findItem(R.id.action_favorite);
            MenuItem action_share = menu.findItem(R.id.action_share);

            action_favorite.setIcon(Utilities.isFavorite(getActivity(), mMovie.getId()) == 1 ?
                    R.drawable.ic_star_black_24dp :
                    R.drawable.ic_star_border_black_24dp);

            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return Utilities.isFavorite(getActivity(), mMovie.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavorite) {
                    action_favorite.setIcon(isFavorite == 1 ?
                            R.drawable.ic_star_black_24dp :
                            R.drawable.ic_star_border_black_24dp);
                }
            }.execute();

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

            if (movieVideo != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite:
                if (mMovie != null) {
                    // check if movie is in favorites or not
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return Utilities.isFavorite(getActivity(), mMovie.getId());
                        }

                        @Override
                        protected void onPostExecute(Integer isFavorited) {
                            // if it is in favorites
                            if (isFavorited == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                MovieContract.MovieEntry.CONTENT_URI,
                                                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TMDB_ID + " = ?",
                                                new String[]{Integer.toString(mMovie.getId())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.ic_star_border_black_24dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            // if it is not in favorites
                            else {
                                // add to favorites
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_POSTER_PATH, mMovie.getPosterPath());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ADULT, mMovie.getAdult());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_OVERVIEW, mMovie.getOverview());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_RELEASE_DATE, mMovie.getReleaseDate());
                                        //values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_GENRE_IDS, mMovie.getGenreIds());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TMDB_ID, mMovie.getId());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ORIGINAL_TITLE, mMovie.getOriginalTitle());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ORIGINAL_LANGUAGE, mMovie.getOriginalLanguage());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TITLE, mMovie.getTitle());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_BACKDROP_PATH, mMovie.getBackdropPath());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_POPULARITY, mMovie.getPopularity());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VOTE_COUNT, mMovie.getVoteCount());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VIDEO, mMovie.getVideo());
                                        values.put(Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VOTE_AVERAGE, mMovie.getVoteAverage());

                                        return getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                                                values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.ic_star_black_24dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(MovieDetailsFragment.DETAIL_MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        mDetailLayout = (ScrollView) rootView.findViewById(R.id.movie_details_layout);

        if (mMovie != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
        }

        mImageView = (ImageView) rootView.findViewById(R.id.detail_image);

        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.detail_overview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date);
        mVoteAverageView = (TextView) rootView.findViewById(R.id.detail_vote_average);

        mTrailersView = (LinearListView) rootView.findViewById(R.id.detail_trailers);
        mReviewsView = (LinearListView) rootView.findViewById(R.id.detail_reviews);

        cardViewReview = (CardView) rootView.findViewById(R.id.detail_reviews_cardview);
        cardViewVideo = (CardView) rootView.findViewById(R.id.detail_trailers_cardview);

        movieVideoAdapter = new MovieVideoAdapter(getActivity(), new ArrayList<MovieVideo>());
        mTrailersView.setAdapter(movieVideoAdapter);

        mTrailersView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view,
                                    int position, long id) {
                MovieVideo trailer = movieVideoAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intent);
            }
        });

        movieReviewAdapter = new MovieReviewAdapter(getActivity(), new ArrayList<MovieReview>());
        mReviewsView.setAdapter(movieReviewAdapter);

        if (mMovie != null) {

            String image_url = Utilities.buildImageUrl(342, mMovie.getBackdropPath());

            Glide.with(this)
                    .load(image_url)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(mImageView);

            mTitleView.setText(mMovie.getTitle());
            mOverviewView.setText(mMovie.getOverview());

            String movie_date = mMovie.getReleaseDate();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                String date = DateUtils.formatDateTime(getActivity(),
                        formatter.parse(movie_date).getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                mDateView.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mVoteAverageView.setText(Double.toString(mMovie.getVoteAverage()));
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMovie != null) {
            new FetchTrailersTask().execute(Integer.toString(mMovie.getId()));
            new FetchReviewsTask().execute(Integer.toString(mMovie.getId()));
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getTitle() + " " +
                "http://www.youtube.com/watch?v=" + movieVideo.getKey());
        return shareIntent;
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, List<MovieVideo>> {

        private final String TAG = FetchTrailersTask.class.getSimpleName();

        private List<MovieVideo> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<MovieVideo> results = new ArrayList<>();

            for(int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                // Only show Trailers which are on Youtube
                if (trailer.getString("site").contentEquals("YouTube")) {
                    MovieVideo trailerModel = new MovieVideo(trailer);
                    results.add(trailerModel);
                }
            }

            return results;
        }

        @Override
        protected List<MovieVideo> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
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
                Log.e(TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieVideo> trailers) {
            if (trailers != null) {
                if (trailers.size() > 0) {
                    cardViewVideo.setVisibility(View.VISIBLE);
                    if (movieVideoAdapter != null) {
                        movieVideoAdapter.clear();
                        for (MovieVideo trailer : trailers) {
                            movieVideoAdapter.add(trailer);
                        }
                    }

                    movieVideo = trailers.get(0);
                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareMovieIntent());
                    }
                }
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, List<MovieReview>> {

        private final String TAG = FetchReviewsTask.class.getSimpleName();

        private List<MovieReview> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<MovieReview> results = new ArrayList<>();

            for(int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new MovieReview(review));
            }

            return results;
        }

        @Override
        protected List<MovieReview> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
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
                Log.e(TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieReview> reviews) {
            if (reviews != null) {
                if (reviews.size() > 0) {
                    cardViewReview.setVisibility(View.VISIBLE);
                    if (movieReviewAdapter != null) {
                        movieReviewAdapter.clear();
                        for (MovieReview review : reviews) {
                            movieReviewAdapter.add(review);
                        }
                    }
                }
            }
        }
    }
}