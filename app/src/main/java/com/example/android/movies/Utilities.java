package com.example.android.movies;

import android.content.Context;
import android.database.Cursor;

import com.example.android.movies.databases.MovieContract;

public class Utilities {
    static final String TAG = Utilities.class.getSimpleName();

    public static int isFavorited(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,  // uri
                null,  // projection
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TMDB_ID + " = ?",  // selection
                new String[] { Integer.toString(id) },  // selectionArgs
                null  // sortOrder
        );
        int rowCount = cursor.getCount();
        cursor.close();
        return rowCount;
    }

    public static String buildImageUrl(int width, String fileName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + fileName;
    }
}
