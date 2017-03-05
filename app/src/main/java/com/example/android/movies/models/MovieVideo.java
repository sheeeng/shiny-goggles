package com.example.android.movies.models;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * https://developers.themoviedb.org/3/movies/get-movie-videos
 */

public class MovieVideo {

    private String id;
    private String iso_639_1;
    private String iso_3166_1;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    public MovieVideo() {

    }

    public MovieVideo(JSONObject video) throws JSONException {
        this.id = video.getString("id");
        this.iso_639_1 = video.getString("iso_639_1");
        this.iso_3166_1 = video.getString("iso_3166_1");
        this.key = video.getString("key");
        this.name = video.getString("name");
        this.site = video.getString("site");
        this.size = video.getInt("size");

        String videoType = video.getString("type");
        if (videoType == "Trailer"
                || videoType == "Teaser"
                || videoType == "Clip"
                || videoType == "Featurette") {
            //Allowed Values: Trailer, Teaser, Clip, Featurette
            this.type = video.getString("type");
        } else {
            throw new JSONException("Invalid video type.");
        }
    }

    public String getId() {
        return id;
    }
    public String getIso6391() {
        return iso_639_1;
    }
    public String getIso31661() {
        return iso_3166_1;
    }
    public String getKey() { return key; }
    public String getName() { return name; }
    public String getSite() { return site; }
    public int getSize() { return size; }
    public String getType() { return type; }
}
