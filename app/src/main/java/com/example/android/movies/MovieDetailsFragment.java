package com.example.android.movies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.android.movies.tasks.FetchReviewsTask;
import com.example.android.movies.tasks.FetchTrailersTask;
import com.linearlistview.LinearListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailsFragment
        extends Fragment
        implements
        FetchTrailersTask.FetchTrailersTaskInterfaces,
        FetchReviewsTask.FetchReviewsTaskInterfaces {
    public static final String TAG = MovieDetailsFragment.class.getSimpleName();

    static final String MOVIE_DETAILS = "MOVIE_DETAILS";

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
    private LinearLayout mMovieDetailsLayoutContainer;

    private Toast mToast;

    private ShareActionProvider mShareActionProvider;

    private MovieVideo movieVideo;

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);  // If true, the fragment has menu items to contribute.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mMovie != null) {
            inflater.inflate(R.menu.menu_fragment_detail, menu);

            final MenuItem action_favorite = menu.findItem(R.id.action_favorite);
            MenuItem action_share = menu.findItem(R.id.action_share);

            action_favorite.setIcon(Utilities.isFavorite(getActivity(), mMovie.getId()) == 1 ?
                    R.drawable.ic_star_white_24dp :
                    R.drawable.ic_star_border_white_24dp);

            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return Utilities.isFavorite(getActivity(), mMovie.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavorite) {
                    action_favorite.setIcon(isFavorite == 1 ?
                            R.drawable.ic_star_white_24dp :
                            R.drawable.ic_star_border_white_24dp);
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
                                        item.setIcon(R.drawable.ic_star_border_white_24dp);
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
                                        item.setIcon(R.drawable.ic_star_white_24dp);
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
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovie = bundle.getParcelable(MovieDetailsFragment.MOVIE_DETAILS);
        }

        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDetailLayout = (ScrollView) view.findViewById(R.id.movie_details_layout);
        mMovieDetailsLayoutContainer = (LinearLayout) view.findViewById(R.id.ll_movie_details_layout);

        if (mMovie != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
            mMovieDetailsLayoutContainer.setVisibility(View.VISIBLE);
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
            mMovieDetailsLayoutContainer.setVisibility(View.INVISIBLE);
        }

        mImageView = (ImageView) view.findViewById(R.id.iv_movie_detail_image);

        mTitleView = (TextView) view.findViewById(R.id.tv_movie_detail_title);
        mOverviewView = (TextView) view.findViewById(R.id.tv_movie_detail_overview);
        mDateView = (TextView) view.findViewById(R.id.tv_movie_detail_date);
        mVoteAverageView = (TextView) view.findViewById(R.id.tv_movie_detail_vote_average);

        mTrailersView = (LinearListView) view.findViewById(R.id.lll_movie_detail_trailers);
        mReviewsView = (LinearListView) view.findViewById(R.id.lll_movie_detail_reviews);

        cardViewReview = (CardView) view.findViewById(R.id.cv_movie_detail_reviews);
        cardViewVideo = (CardView) view.findViewById(R.id.cv_movie_detail_trailers);

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
                        formatter.parse(movie_date).getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                mDateView.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mVoteAverageView.setText(Double.toString(mMovie.getVoteAverage()));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMovie != null) {
            new FetchTrailersTask(this).execute(Integer.toString(mMovie.getId()));
            new FetchReviewsTask(this).execute(Integer.toString(mMovie.getId()));
        }
    }


    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            // The `FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET` constant was deprecated in API level 21.
            // https://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
            // https://source.android.com/source/build-numbers.html
            // https://medium.com/google-developers/picking-your-compilesdkversion-minsdkversion-targetsdkversion-a098a0341ebd
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "See `" + mMovie.getTitle() + "` you must!\n" +
                "http://www.youtube.com/watch?v=" + movieVideo.getKey() );
        return shareIntent;
    }


    @Override
    public void onFetchTrailersTaskPostExecute(List<MovieVideo> trailers) {
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


    @Override
    public void onFetchReviewsTaskPostExecute(List<MovieReview> reviews) {
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