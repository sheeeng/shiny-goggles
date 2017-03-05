package com.example.android.movies;

import org.json.JSONArray;

public class Constants {

    // No need to use "static final" attributes because this is an interface an not a class.
    public interface URLS {
        String TMDB_IMAGE_SIZE_W92 = "http://image.tmdb.org/t/p/w92/";
        String TMDB_IMAGE_SIZE_W154 = "http://image.tmdb.org/t/p/w154/";
        String TMDB_IMAGE_SIZE_W185 = "http://image.tmdb.org/t/p/w185/";
        String TMDB_IMAGE_SIZE_W342 = "http://image.tmdb.org/t/p/w342/";
        String TMDB_IMAGE_SIZE_W500 = "http://image.tmdb.org/t/p/w500/";
        String TMDB_IMAGE_SIZE_W780 = "http://image.tmdb.org/t/p/w780/";
        String TMDB_IMAGE_SIZE_ORIGINAL = "http://image.tmdb.org/t/p/orginal/";
    }

    // No need to use "static final" attributes because this is an interface an not a class.
    public interface MOVIE_DATABASE_COLUMNS {
        int COL_ID = 0;
        int COL_POSTER_PATH = 1;
        int COL_ADULT = 2;
        int COL_OVERVIEW = 3;
        int COL_RELEASE_DATE = 4;
        int COL_GENRE_IDS = 5;
        int COL_MOVIE_ID = 6;
        int COL_ORIGINAL_TITLE = 7;
        int COL_ORIGINAL_LANGUAGE = 8;
        int COL_TITLE = 9;
        int COL_BACKDROP_PATH = 10;
        int COL_POPULARITY = 11;
        int COL_VOTE_COUNT = 12;
        int COL_VIDEO = 13;
        int COL_VOTE_AVERAGE = 14;
    }

    // No need to use "static final" attributes because this is an interface an not a class.
    public interface MOVIE_DATABASE_TABLE_NAMES {
        String TAB_MOVIES = "movies";
    }

    // No need to use "static final" attributes because this is an interface an not a class.
    public interface MOVIE_DATABASE_COLUMN_NAMES {
        String COL_POSTER_PATH = "poster_path";
        String COL_ADULT = "adult";
        String COL_OVERVIEW = "overview";
        String COL_RELEASE_DATE = "release_date";
        String COL_GENRE_IDS = "genre_ids";
        String COL_TMDB_ID = "tmdb_id";
        String COL_ORIGINAL_TITLE = "original_title";
        String COL_ORIGINAL_LANGUAGE = "original_language";
        String COL_TITLE = "title";
        String COL_BACKDROP_PATH = "backdrop_path";
        String COL_POPULARITY = "popularity";
        String COL_VOTE_COUNT = "cote_count";
        String COL_VIDEO = "video";
        String COL_VOTE_AVERAGE = "vote_average";
    }
}
