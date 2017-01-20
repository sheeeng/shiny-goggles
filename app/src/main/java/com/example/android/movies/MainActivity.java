package com.example.android.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.android.movies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "# MOVIES LOGS";
    private TextView mTextViewDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewDisplay = (TextView) findViewById(R.id.tv_display);
        queryMoviesDb();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void queryMoviesDb() {
        if (isOnline()) {
            Log.d(TAG, "Online!");
            URL moviesQueryUrl = NetworkUtils.buildUrl();
            new MoviesQueryTask().execute(moviesQueryUrl);
        } else {
            Log.d(TAG, "Offline!");
        }
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String moviesQueryResults = null;
            try {
                moviesQueryResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return moviesQueryResults;
        }
        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                mTextViewDisplay.setText(s);
            }
            super.onPostExecute(s);
        }
    }
}


