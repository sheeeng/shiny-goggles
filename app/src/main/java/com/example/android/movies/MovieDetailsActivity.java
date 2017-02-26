package com.example.android.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    @BindView(R.id.iv_item_details_movie_poster) ImageView imageViewMoviePoster;
    @BindView(R.id.iv_item_details_movie_backdrop) ImageView imageViewMovieBackdrop;
    @BindView(R.id.tv_item_details_movie_title) TextView textViewMovieTitle;
    @BindView(R.id.tv_item_details_movie_release_date) TextView textViewMovieReleaseDate;
    @BindView(R.id.tv_item_details_movie_vote_average) TextView textViewMovieVoteAverage;
    @BindView(R.id.tv_item_details_movie_overview) TextView textViewMovieOverview;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        /*
        UDACITY_REVIEW
        Since from your codes, I can see that you are a really advanced student.
        In order to learn more, you could also check a package called "butterknife".
        In the future, you can find and automatically cast the corresponding view in your layout easily.
        This will save you a lot of time. :smiley:
        http://jakewharton.github.io/butterknife/
        https://www.youtube.com/watch?v=1A4LY8gUEDs
         */
        ButterKnife.bind(this);

        Intent intentSource = getIntent();

        if (intentSource.hasExtra(Constants.INTENT.MOVIE_DETAILS)) {
            movie = intentSource.getParcelableExtra(Constants.INTENT.MOVIE_DETAILS);

            Glide.with(this)
                    .load(Constants.URLS.TMDB_IMAGE_SIZE_W500 +
                            movie.getBackdropPath())
                    .into(imageViewMovieBackdrop);

            Glide.with(this)
                    .load(Constants.URLS.TMDB_IMAGE_SIZE_W500 +
                            movie.getPosterPath())
                    .into(imageViewMoviePoster);

            textViewMovieTitle.setText(movie.getTitle());

            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dateRelease = new Date();
            try {
                dateRelease = dateFormatter.parse(movie.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("en", "US"));
            textViewMovieReleaseDate.setText(getString(R.string.movie_details_release_date) +
                    dateFormatter.format(dateRelease));

            textViewMovieVoteAverage.setText(getString(R.string.movie_details_vote_averate) +
                    movie.getVoteAverage().toString());
            textViewMovieOverview.setText(movie.getOverview());
        }
    }
}
