package com.example.android.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private ImageView imageViewMoviePoster;
    private ImageView imageViewMovieBackdrop;
    private TextView textViewMovieTitle;
    private TextView textViewMovieReleaseDate;
    private TextView textViewMovieVoteAverage;
    private TextView textViewMovieOverview;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        imageViewMoviePoster = (ImageView) findViewById(R.id.iv_item_details_movie_poster);
        imageViewMovieBackdrop = (ImageView) findViewById(R.id.iv_item_details_movie_backdrop);

        textViewMovieTitle = (TextView) findViewById(R.id.tv_item_details_movie_title);
        textViewMovieReleaseDate = (TextView) findViewById(R.id.tv_item_details_movie_release_date);
        textViewMovieVoteAverage = (TextView) findViewById(R.id.tv_item_details_movie_vote_average);
        textViewMovieOverview = (TextView) findViewById(R.id.tv_item_details_movie_overview);

        Intent intentSource = getIntent();

        if (intentSource.hasExtra("MOVIE_DETAILS")) {
            movie = intentSource.getParcelableExtra("MOVIE_DETAILS");

            Glide.with(this)
                    .load("http://image.tmdb.org/t/p/w500/" +
                            movie.getBackdropPath())
                    .into(imageViewMovieBackdrop);

            Glide.with(this)
                    .load("http://image.tmdb.org/t/p/original/" +
                            movie.getPosterPath())
                    .into(imageViewMoviePoster);

            textViewMovieTitle.setText(movie.getTitle());
            textViewMovieReleaseDate.setText(movie.getReleaseDate());
            textViewMovieVoteAverage.setText(movie.getVoteAverage().toString());
            textViewMovieOverview.setText(movie.getOverview());
        }
    }
}
