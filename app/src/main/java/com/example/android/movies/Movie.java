package com.example.android.movies;

public class Movie {
    String title;
    String release_date;
    String poster_path;
    String vote_average;
    String overview;

    public Movie(String title,
            String release_date,
            String poster_path,
            String vote_average,
            String overview)
    {
        this.title = title;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.overview = overview;
    }
}