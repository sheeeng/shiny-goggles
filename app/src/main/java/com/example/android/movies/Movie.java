package com.example.android.movies;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {
    private String poster_path;
    private boolean adult;
    private String overview;
    private String release_date;
    private int[] genre_ids;
    private int id;
    private String original_title;
    private String original_language;
    private String title;
    private String backdrop_path;
    private float popularity;
    private int vote_count;
    private boolean video;
    private float vote_average;

    public Movie() {

    }

    public Movie(JSONObject movie) throws JSONException {
        this.id = movie.getInt("id");
        this.title = movie.getString("title");
        this.poster_path = movie.getString("poster_path");
        this.backdrop_path = movie.getString("backdrop_path");
        this.overview = movie.getString("overview");
        this.vote_average = movie.getInt("vote_average");
        this.release_date = movie.getString("release_date");
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public float getVoteAverage() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }

}