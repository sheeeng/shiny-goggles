package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable {

    private static final String LOG_TAG = Movie.class.getSimpleName();

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
    private Double popularity;
    private int vote_count;
    private boolean video;
    private Double vote_average;

    public Movie() {

    }

    public Movie(JSONObject movie) throws JSONException {
        Log.d(LOG_TAG, movie.toString());

        this.poster_path = movie.getString("poster_path");
        this.adult = movie.getBoolean("adult");
        this.overview = movie.getString("overview");
        this.release_date = movie.getString("release_date");

        JSONArray jsonArray = movie.optJSONArray("genre_ids");
        if (jsonArray == null) {
            this.genre_ids = new int[0];
        } else {
            this.genre_ids = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i) {
                this.genre_ids[i] = jsonArray.getInt(i);
            }
        }

        this.id = movie.getInt("id");
        this.original_title = movie.getString("original_title");
        this.original_language = movie.getString("original_language");
        this.title = movie.getString("title");
        this.backdrop_path = movie.getString("backdrop_path");
        this.popularity = movie.getDouble("popularity");
        this.vote_count = movie.getInt("vote_count");
        this.video = movie.getBoolean("video");
        this.vote_average = movie.getDouble("vote_average");
    }

    public String getPosterPath() {
        return poster_path;
    }
    public Boolean getAdult() { return adult; }
    public String getOverview() {
        return overview;
    }
    public String getReleaseDate() {
        return release_date;
    }
    public int[] getGenreIds() { return genre_ids; }
    public int getId() { return id; }
    public String getOriginalTitle() { return original_title; }
    public String getOriginalLanguage() { return original_language; }
    public String getTitle() {
        return title;
    }
    public String getBackdropPath() { return backdrop_path; }
    public Double getPopularity() { return popularity; }
    public int getVoteCount() { return vote_count; }
    public Boolean getVideo() { return video; }
    public Double getVoteAverage() { return vote_average; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcelOutgoing, int flags) {
        parcelOutgoing.writeString(poster_path);
        parcelOutgoing.writeByte((byte)(adult ? 1 : 0));
        parcelOutgoing.writeString(overview);
        parcelOutgoing.writeString(release_date);
        parcelOutgoing.writeIntArray(genre_ids);
        parcelOutgoing.writeInt(id);
        parcelOutgoing.writeString(original_title);
        parcelOutgoing.writeString(original_language);
        parcelOutgoing.writeString(title);
        parcelOutgoing.writeString(backdrop_path);
        parcelOutgoing.writeDouble(popularity);
        parcelOutgoing.writeInt(vote_count);
        parcelOutgoing.writeByte((byte)(video ? 1 : 0));
        parcelOutgoing.writeDouble(vote_average);

    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel parcelReceived) {
        poster_path = parcelReceived.readString();
        adult = parcelReceived.readByte() != 0;
        overview = parcelReceived.readString();
        release_date = parcelReceived.readString();
        genre_ids = parcelReceived.createIntArray();
        id = parcelReceived.readInt();
        original_title = parcelReceived.readString();
        original_language = parcelReceived.readString();
        title = parcelReceived.readString();
        backdrop_path = parcelReceived.readString();
        popularity = parcelReceived.readDouble();
        vote_count = parcelReceived.readInt();
        video = parcelReceived.readByte() != 0;
        vote_average = parcelReceived.readDouble();
    }
}