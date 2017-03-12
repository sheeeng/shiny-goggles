package com.example.android.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.movies.R;
import com.example.android.movies.models.MovieVideo;

import java.util.List;

public class MovieVideoAdapter  extends BaseAdapter {
    private static final String TAG = MovieVideoAdapter.class.getSimpleName();

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final MovieVideo mLock = new MovieVideo();

    private List<MovieVideo> mObjects;

    public MovieVideoAdapter(Context context, List<MovieVideo> objects) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObjects = objects;
    }

    public Context getContext() {
        return mContext;
    }

    public void add(MovieVideo object) {
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
    public MovieVideo getItem(int position) {
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
            view = mInflater.inflate(R.layout.item_movie_video, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final MovieVideo movieVideo = getItem(position);

        viewHolder = (ViewHolder) view.getTag();

        String youtubeThumbnailUrl = "http://img.youtube.com/vi/" + movieVideo.getKey() + "/0.jpg";

        if (getContext() != null) {
            Glide.with(getContext())
                    .load(youtubeThumbnailUrl)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(viewHolder.imageView);
        }

        viewHolder.nameView.setText(movieVideo.getName());

        return view;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView nameView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.iv_movie_video_image);
            nameView = (TextView) view.findViewById(R.id.tv_movie_video_name);
        }
    }

}
