package com.pelsoczi.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pelsoczi.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String LOG_TAG = Adapter.class.getSimpleName();

    public final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<Movie> items;
    // cache for quicker response
    private final int itemsSize;

    public Adapter(Context context, List<Movie> movies) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        setHasStableIds(true);
        items = movies;
        itemsSize = items.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = R.layout.list_item_movie;
        return viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(viewType, parent, false);
        return new CellViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = items.get(position);
        ((CellViewHolder)holder).setMovie(movie);

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE_PATH = "w780";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(SIZE_PATH)
                .appendEncodedPath(movie.getPosterPath())
                .build();


        ImageView image = ((CellViewHolder)holder).poster;
        Picasso.with(mContext)
                .load(builtUri)
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

    private static final class CellViewHolder extends ViewHolder implements View.OnClickListener {

        private final Context context;
        private final ImageView poster;
        private Movie movie;

        public CellViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            poster = (ImageView) itemView.findViewById(R.id.movie_poster);
            poster.setOnClickListener(this);
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DetailActivity.class)
                    .putExtra(Movie.TAG, movie);
            context.startActivity(intent);
        }
    }
}