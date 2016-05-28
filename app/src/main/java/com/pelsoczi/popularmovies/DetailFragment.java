package com.pelsoczi.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pelsoczi.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    public static final String MOVIE_INDEX = "index";

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    Movie movie;
    int index;

    private TextView originalTitle;
    private ImageView poster;
    private TextView release;
    private TextView average;
    private TextView synopsis;

    public static DetailFragment newInstance(Movie movie, int index) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Movie.TAG, movie);
        bundle.putInt(MOVIE_INDEX, index);
        DetailFragment detail = new DetailFragment();
        detail.setArguments(bundle);
        return detail;
    }

    public int getShownIndex() {
        return getArguments().getInt(MOVIE_INDEX, -1);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        movie = getArguments().getParcelable(Movie.TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        originalTitle = (TextView) rootView.findViewById(R.id.detail_original_title);
        poster = (ImageView) rootView.findViewById(R.id.detail_poster);
        release = (TextView) rootView.findViewById(R.id.detail_release);
        average = (TextView) rootView.findViewById(R.id.detail_vote_average);
        synopsis = (TextView) rootView.findViewById(R.id.detail_plot_synopsis);

        originalTitle.setText(movie.getOriginalTitle());

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String SIZE_PATH = "w780";
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(SIZE_PATH)
                .appendEncodedPath(movie.getPosterPath())
                .build();
        Picasso.with(rootView.getContext()).load(builtUri).into(poster);

        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat out = new SimpleDateFormat("yyyy");
            release.setText(out.format(in.parse(movie.getReleaseDate())));
        } catch (ParseException e) {
            release.setText(movie.getReleaseDate());
        }

        average.setText(String.valueOf(movie.getVoteAverage()));
        synopsis.setText(String.valueOf(movie.getOverview()));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
