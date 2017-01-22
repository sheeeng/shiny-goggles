package com.example.android.movies;

public class Movie {
    private String title;
    private String release_date;
    private String poster_path;
    private String vote_average;
    private String overview;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public void setPosterPath(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}