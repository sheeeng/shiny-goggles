package com.example.android.movies.databases;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    private static final String TAG = MovieContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.example.android.movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
