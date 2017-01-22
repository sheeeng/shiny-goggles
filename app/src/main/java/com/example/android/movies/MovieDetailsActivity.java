package com.example.android.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView textViewMovieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        textViewMovieTitle = (TextView) findViewById(R.id.tv_item_details_movie_title);

        Intent intentSource = getIntent();

        if (intentSource.hasExtra(Intent.EXTRA_TEXT)) {
            textViewMovieTitle.setText(intentSource.getStringExtra(Intent.EXTRA_TEXT));
        }
    }


}
