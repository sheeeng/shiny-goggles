package com.example.android.movies.models;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * https://developers.themoviedb.org/3/movies/get-movie-reviews
 */

public class MovieReview {
    private static final String TAG = MovieReview.class.getSimpleName();

    private String id;
    private String author;
    private String content;
    private String url;

    public MovieReview() {

    }

    public MovieReview(JSONObject review) throws JSONException {
        this.id = review.getString("id");
        this.author = review.getString("author");
        this.content = review.getString("content");
        this.url = review.getString("url");
    }

    public String getId() { return id; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public String getUrl() { return url; }
}
