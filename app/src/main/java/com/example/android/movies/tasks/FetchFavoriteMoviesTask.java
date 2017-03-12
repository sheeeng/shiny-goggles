package com.example.android.movies.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.android.movies.Constants;
import com.example.android.movies.databases.MovieContract;
import com.example.android.movies.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
    private static final String TAG = FetchFavoriteMoviesTask.class.getSimpleName();

    private FetchFavoriteMoviesTaskInterfaces fetchFavoriteMoviesTaskInterfacesListener;

    public interface FetchFavoriteMoviesTaskInterfaces {
        void onFetchFavoriteMoviesTaskPreExecute();
        void onFetchFavoriteMoviesTaskPostExecute(List<Movie> movies);
    }

    private Context context;

    public FetchFavoriteMoviesTask(
            Context context,
            FetchFavoriteMoviesTaskInterfaces fetchFavoriteMoviesTaskInterfaces) {
        this.context = context;
        this.fetchFavoriteMoviesTaskInterfacesListener = fetchFavoriteMoviesTaskInterfaces;
    }

    private List<Movie> getFavoriteMoviesDataFromCursor(Cursor cursor) {
        List<Movie> results = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(cursor);
                results.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
        }

//        for(Movie movie : results) {
//            Log.d(TAG, movie.getTitle());
//        }
        return results;
    }

    @Override
    protected List<Movie> doInBackground(Void... params) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,  //uri
                Constants.MOVIE_COLUMNS,  // projection
                null,  // selection
                null,  // selectionArgs
                null  // sortOrder
        );
//        Log.d(TAG, cursor.toString());
        return getFavoriteMoviesDataFromCursor(cursor);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (fetchFavoriteMoviesTaskInterfacesListener != null) {
            fetchFavoriteMoviesTaskInterfacesListener.onFetchFavoriteMoviesTaskPreExecute();
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        if (fetchFavoriteMoviesTaskInterfacesListener != null) {
            fetchFavoriteMoviesTaskInterfacesListener.onFetchFavoriteMoviesTaskPostExecute(movies);
        }
    }
}
