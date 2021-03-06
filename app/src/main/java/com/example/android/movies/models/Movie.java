package com.example.android.movies.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.movies.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
UDACITY_REVIEW
Awesome job implementing Parcelable!
By doing so, you can optimize your app to save dynamic data/state efficiently. Well done! :clap:
*/

/*
 * https://developers.themoviedb.org/3/movies/get-now-playing
 * https://developers.themoviedb.org/3/movies/get-popular-movies
 * https://developers.themoviedb.org/3/movies/get-top-rated-movies
 * https://developers.themoviedb.org/3/movies/get-upcoming
 */

public class Movie implements Parcelable {
    private static final String TAG = Movie.class.getSimpleName();

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
        //Log.d(TAG, movie.toString());

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

    public Movie(Cursor cursor) {
        this.poster_path = cursor.getString(Constants.MOVIE_DATABASE_COLUMNS.COL_POSTER_PATH);
        this.adult = ( cursor.getInt(Constants.MOVIE_DATABASE_COLUMNS.COL_ADULT) != 0 );
        this.overview = cursor.getString(Constants.MOVIE_DATABASE_COLUMNS.COL_OVERVIEW);
        this.release_date = cursor.getString(Constants.MOVIE_DATABASE_COLUMNS.COL_RELEASE_DATE);

//        JSONArray jsonArray = cursor.get(Constants.MOVIE_DATABASE_COLUMNS.COL_GENRE_IDS);
//        if (jsonArray == null) {
//            this.genre_ids = new int[0];
//        } else {
//            this.genre_ids = new int[jsonArray.length()];
//            for (int i = 0; i < jsonArray.length(); ++i) {
//                this.genre_ids[i] = jsonArray.getInt(i);
//            }
//        }

        this.id = cursor.getInt(Constants.MOVIE_DATABASE_COLUMNS.COL_MOVIE_ID);
        this.original_title = cursor.getString(Constants.MOVIE_DATABASE_COLUMNS.COL_ORIGINAL_TITLE);
        this.original_language = cursor.getString(Constants.MOVIE_DATABASE_COLUMNS.COL_ORIGINAL_LANGUAGE);
        this.title = cursor.getString(Constants.MOVIE_DATABASE_COLUMNS.COL_TITLE);
        this.backdrop_path = cursor.getString(Constants.MOVIE_DATABASE_COLUMNS.COL_BACKDROP_PATH);
        this.popularity = cursor.getDouble(Constants.MOVIE_DATABASE_COLUMNS.COL_POPULARITY);
        this.vote_count = cursor.getInt(Constants.MOVIE_DATABASE_COLUMNS.COL_VOTE_COUNT);
        this.video = ( cursor.getInt(Constants.MOVIE_DATABASE_COLUMNS.COL_VIDEO) != 0 );
        this.vote_average = cursor.getDouble(Constants.MOVIE_DATABASE_COLUMNS.COL_VOTE_AVERAGE);
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


    /**
     * Parcelable
     * Describe the kinds of special objects contained in this Parcelable instance's
     * marshaled representation.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcelable
     * Flatten this object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel parcelOut, int flags) {
        parcelOut.writeString(poster_path);
        parcelOut.writeByte((byte)(adult ? 1 : 0));
        parcelOut.writeString(overview);
        parcelOut.writeString(release_date);
        parcelOut.writeIntArray(genre_ids);
        parcelOut.writeInt(id);
        parcelOut.writeString(original_title);
        parcelOut.writeString(original_language);
        parcelOut.writeString(title);
        parcelOut.writeString(backdrop_path);
        parcelOut.writeDouble(popularity);
        parcelOut.writeInt(vote_count);
        parcelOut.writeByte((byte)(video ? 1 : 0));
        parcelOut.writeDouble(vote_average);
    }

    /**
     * Parcelable
     * Classes implementing the Parcelable interface must also have a non-null static field
     * called CREATOR of a type that implements the Parcelable.Creator interface.
     */
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel parcelIn) {
            return new Movie(parcelIn);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * Parcelable
     * Classes implementing the Parcelable interface must also have a non-null static field
     * called CREATOR of a type that implements the Parcelable.Creator interface.
     */
    private Movie(Parcel parcelIn) {
        poster_path = parcelIn.readString();
        adult = parcelIn.readByte() != 0;
        overview = parcelIn.readString();
        release_date = parcelIn.readString();
        genre_ids = parcelIn.createIntArray();
        id = parcelIn.readInt();
        original_title = parcelIn.readString();
        original_language = parcelIn.readString();
        title = parcelIn.readString();
        backdrop_path = parcelIn.readString();
        popularity = parcelIn.readDouble();
        vote_count = parcelIn.readInt();
        video = parcelIn.readByte() != 0;
        vote_average = parcelIn.readDouble();
    }
}