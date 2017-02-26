package com.example.android.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailsActivity
        extends AppCompatActivity {
    static final String TAG = MovieDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.DETAIL_MOVIE,
                    getIntent().getParcelableExtra(MovieDetailsFragment.DETAIL_MOVIE));

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, fragment)
                    .commit();
        }
    }
}
