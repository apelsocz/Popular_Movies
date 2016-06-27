package com.pelsoczi.popularmovies.ui;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pelsoczi.popularmovies.MovieActivity;
import com.pelsoczi.popularmovies.R;
import com.pelsoczi.popularmovies.models.Movie;
import com.pelsoczi.popularmovies.util.Files;
import com.pelsoczi.popularmovies.util.Utility;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {

    public static final String TAG = Adapter.class.getSimpleName();

    public final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Movie> items;
    // cache for quicker response
    private int itemsSize;
    private boolean favorites;

    public Adapter(Context context, List<Movie> movies) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        setHasStableIds(true);
        items = movies;
        itemsSize = items.size();
        favorites = Utility.getSortOrder(context)
                .equals(context.getString(R.string.pref_sort_favorite));
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = favorites ? R.layout.list_item_favorite : R.layout.list_item_movie;
        return viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(viewType, parent, false);
        return favorites ?
                new FavoriteViewHolder(mContext, view) : new PosterViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = items.get(position);
        Uri uri;
        int placeholder;
        int error;
        ImageView image;

        if (holder instanceof FavoriteViewHolder) {
            ((FavoriteViewHolder)holder).setMovie(movie);
            uri = Files.getBackdropUri(movie);
            placeholder = R.drawable.placeholder_backdrop;
            error = R.drawable.error_backdrop;
            image = ((FavoriteViewHolder)holder).backdrop;
        }
        else {
            ((PosterViewHolder)holder).setMovie(movie);
            uri = Files.getPosterUri(movie);
            placeholder = R.drawable.placeholder_poster;
            error = R.drawable.error_poster;
            image = ((PosterViewHolder)holder).poster;
        }

        Picasso.with(mContext)
                .load(uri)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(placeholder)
                .error(error)
                .into(image);
    }

    @Override
    public int getItemCount() {
        return itemsSize;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class FavoriteViewHolder extends ViewHolder implements View.OnClickListener {

        private final Context context;
        private final ImageView backdrop;
        private TextView originalTitle;
        private TextView title;
        private int position;
        private Movie movie;

        public FavoriteViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            backdrop = (ImageView) itemView.findViewById(R.id.movie_backdrop);
            originalTitle = (TextView) itemView.findViewById(R.id.movie_original_title);
            title = (TextView) itemView.findViewById(R.id.movie_title);
            backdrop.setOnClickListener(this);
            position = getAdapterPosition();
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
            String originalTitle = movie.getOriginalTitle();
            String title = movie.getTitle();
            if (originalTitle.equals(title)) {
                this.title.setVisibility(View.GONE);
                this.originalTitle.setText(movie.getOriginalTitle());
            }
            else {
                this.originalTitle.setText(originalTitle);
                this.title.setText(title);
            }
        }

        @Override
        public void onClick(View v) {
            MovieActivity activity = ((MovieActivity)context);
            activity.showDetails(movie, position);
        }
    }

    private static class PosterViewHolder extends ViewHolder implements View.OnClickListener {

        private final Context context;
        private final ImageView poster;
        private int position;
        private Movie movie;

        public PosterViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            poster = (ImageView) itemView.findViewById(R.id.movie_poster);
            poster.setOnClickListener(this);
            position = getAdapterPosition();
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void onClick(View v) {
            MovieActivity activity = ((MovieActivity)context);
            activity.showDetails(movie, position);
        }
    }
}