package com.example.android.movies.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.movies.Constants;

public class MovieDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // Clear database upon startup for debugging.
        // http://stackoverflow.com/a/4420083
        // context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + Constants.MOVIE_DATABASE_TABLE_NAMES.TAB_MOVIES + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_POSTER_PATH + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ADULT + " BOOL, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_OVERVIEW + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_RELEASE_DATE + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_GENRE_IDS + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TMDB_ID + " INTEGER NOT NULL, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ORIGINAL_TITLE + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_ORIGINAL_LANGUAGE + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_TITLE + " TEXT NOT NULL, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_BACKDROP_PATH + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_POPULARITY + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VOTE_COUNT + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VIDEO + " TEXT, " +
                Constants.MOVIE_DATABASE_COLUMN_NAMES.COL_VOTE_AVERAGE + " DOUBLE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.MOVIE_DATABASE_TABLE_NAMES.TAB_MOVIES);
        onCreate(db);
    }
}
