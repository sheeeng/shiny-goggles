package com.example.android.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.movies.models.Movie;

public class MainActivity
        extends AppCompatActivity
        implements MainActivityFragment.Callback {
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean needTwoPanes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_details_container) != null) {
            needTwoPanes = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailsFragment(),
                                MovieDetailsFragment.TAG)
                        .commit();
            }
        } else {
            needTwoPanes = false;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (needTwoPanes) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.DETAIL_MOVIE, movie);

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment, MovieDetailsFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class)
                    .putExtra(MovieDetailsFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }
}