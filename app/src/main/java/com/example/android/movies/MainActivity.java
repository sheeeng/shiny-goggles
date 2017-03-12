package com.example.android.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
                        .replace(
                                R.id.movie_details_container,
                                new MovieDetailsFragment(),
                                MovieDetailsFragment.TAG)
                        .commit();
            }
        } else {
            needTwoPanes = false;
        }
    }

    @Override
    public void onItemSelected(Movie movie, MovieCategories movieCategory, int moviePosition) {
        if (needTwoPanes) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.MOVIE_DETAILS, movie);

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

            editor.putString(
                    getString(R.string.saved_instance_movie_category),
                    movieCategory.toString());

            editor.putInt(
                    getString(R.string.saved_instance_movie_position),
                    moviePosition);

            editor.apply();

            getSupportFragmentManager().beginTransaction()
                    .replace(
                            R.id.movie_details_container,
                            fragment,
                            MovieDetailsFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class)
                    .putExtra(MovieDetailsFragment.MOVIE_DETAILS, movie);
            startActivity(intent);
        }
    }
}