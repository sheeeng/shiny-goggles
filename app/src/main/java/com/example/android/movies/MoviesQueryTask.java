package com.example.android.movies;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.net.URL;

/*
UDACITY_REVIEW
In order to make your codes reusable and structural, you can consider to refactor your codes
and put this class in a separate Java file.
 */
public class MoviesQueryTask extends AsyncTask<URL, Void, String> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MoviesQueryTaskInterfaces moviesQueryTaskInterfacesListener;

    public interface MoviesQueryTaskInterfaces{
        void onMoviesQueryTaskPreExecute();
        void onMoviesQueryTaskPostExecute(String s);
    }

    public MoviesQueryTask(MoviesQueryTaskInterfaces interfacesListener) {
        this.moviesQueryTaskInterfacesListener = interfacesListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (moviesQueryTaskInterfacesListener != null) {
            moviesQueryTaskInterfacesListener.onMoviesQueryTaskPreExecute();
        }
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL searchUrl = urls[0];
        String moviesQueryResults = null;

        try {
            moviesQueryResults = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return moviesQueryResults;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (moviesQueryTaskInterfacesListener != null) {
            moviesQueryTaskInterfacesListener.onMoviesQueryTaskPostExecute(s);
        }
    }
}
