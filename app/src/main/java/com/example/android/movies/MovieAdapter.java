package com.example.android.movies;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    
    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context The current context. Used to inflate the layout file.
     * @param movies  Movie objects to display in a list.
     */
    public MovieAdapter(Activity context, List<Movie> movies) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // The second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, movies);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param viewRecycler The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @return The View for the position in the AdapterView.
     * @param parent The parent ViewGroup that is used for inflation.
     */
    @Override
    public View getView(int position, View viewRecycler, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (viewRecycler == null) {
            viewRecycler = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }

        ImageView imageViewMoviePoster = (ImageView) viewRecycler.findViewById(R.id.iv_item_movie_poster);
        String posterPath = "http://image.tmdb.org/t/p/w185/" + movie.poster_path;
        Log.d(LOG_TAG, posterPath);
        Picasso.with(parent.getContext()).load(posterPath).into(imageViewMoviePoster);

        TextView textViewMovieTitle = (TextView) viewRecycler.findViewById(R.id.tv_item_movie_title);
        Log.d(LOG_TAG, movie.title);
        textViewMovieTitle.setText(movie.title);

        return viewRecycler;
    }
}