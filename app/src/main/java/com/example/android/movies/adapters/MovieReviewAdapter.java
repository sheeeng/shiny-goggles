package com.example.android.movies.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.movies.R;
import com.example.android.movies.models.MovieReview;

import java.util.List;

public class MovieReviewAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final MovieReview mLock = new MovieReview();

    private List<MovieReview> mObjects;

    public MovieReviewAdapter(Context context, List<MovieReview> objects) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObjects = objects;
    }

    public Context getContext() {
        return mContext;
    }

    public void add(MovieReview object) {
        synchronized (mLock) {
            mObjects.add(object);
        }
        // The adapter needs to know that the data has changed.
        // If we don't call this, app will crash!
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
        }
        // The adapter needs to know that the data has changed.
        // If we don't call this, app will crash!
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public MovieReview getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.item_movie_review, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final MovieReview movieReview = getItem(position);

        viewHolder = (ViewHolder) view.getTag();

        viewHolder.authorView.setText(movieReview.getAuthor());
        viewHolder.contentView.setText(Html.fromHtml(movieReview.getContent()));

        return view;
    }

    public static class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.tv_movie_review_author);
            contentView = (TextView) view.findViewById(R.id.tv_movie_review_content);
        }
    }

}
