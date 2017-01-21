package com.example.android.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mTextViewQueryResults;
    private TextView mTextViewErrorMessage;
    private ProgressBar mProgressBarQuery;

    /*
    //TODO: Uncomment this to try query after network state changes.
    BroadcastReceiver broadcastReceiverConnectivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            queryMoviesDb();
        }
    };

    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewQueryResults = (TextView) findViewById(R.id.tv_query_results);
        mTextViewErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mProgressBarQuery = (ProgressBar) findViewById(R.id.pb_query);
        queryMoviesDb();
        /*
        //TODO: Uncomment this to try query after network state changes.
        try {
            registerReceiver(broadcastReceiverConnectivity, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void queryMoviesDb() {
        URL moviesQueryUrl = NetworkUtils.buildUrl();
        new MoviesQueryTask().execute(moviesQueryUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.query, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_query) {
            mTextViewQueryResults.setText("");
            queryMoviesDb();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showJsonDataView() {
        mTextViewQueryResults.setVisibility(View.VISIBLE);
        mTextViewErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mTextViewErrorMessage.setVisibility(View.VISIBLE);
        mTextViewQueryResults.setVisibility(View.INVISIBLE);
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBarQuery.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String moviesQueryResults = null;
            if (isOnline()) {
                Log.d(TAG, "Query working while online.");
                try {
                    moviesQueryResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "Query will not work while offline.");
            }
            return moviesQueryResults;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressBarQuery.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                mTextViewQueryResults.setText(s);
                showJsonDataView();
                analyzeJson(s);
            } else {
                showErrorMessage();
            }
        }

        private void analyzeJson(String stringJson) {
            largeLog(TAG, stringJson);
        }
    }

    // Credits to http://stackoverflow.com/a/25734136 post.
    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }
}


