package com.example.android.movies;

import android.os.AsyncTask;

import java.net.URL;

/*
UDACITY_REVIEW
In order to make your codes reusable and structural, you can consider to refactor your codes
and put this class in a separate Java file.
 */
public class MovieQueryTask extends AsyncTask<URL, Void, String> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MovieQueryTaskInterfaces movieQueryTaskInterfacesListener;

    public interface MovieQueryTaskInterfaces {
        void onMovieQueryTaskPreExecute();
        void onMovieQueryTaskPostExecute(String s);
    }

    public MovieQueryTask(MovieQueryTaskInterfaces interfacesListener) {
        this.movieQueryTaskInterfacesListener = interfacesListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (movieQueryTaskInterfacesListener != null) {
            movieQueryTaskInterfacesListener.onMovieQueryTaskPreExecute();
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
        if (movieQueryTaskInterfacesListener != null) {
            movieQueryTaskInterfacesListener.onMovieQueryTaskPostExecute(s);
        }
    }
}
