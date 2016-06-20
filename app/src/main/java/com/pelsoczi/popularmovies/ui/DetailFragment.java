package com.pelsoczi.popularmovies.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pelsoczi.popularmovies.BuildConfig;
import com.pelsoczi.popularmovies.R;
import com.pelsoczi.popularmovies.data.MovieContract;
import com.pelsoczi.popularmovies.models.Movie;
import com.pelsoczi.popularmovies.models.Review;
import com.pelsoczi.popularmovies.models.ReviewsResponse;
import com.pelsoczi.popularmovies.models.Video;
import com.pelsoczi.popularmovies.models.VideosResponse;
import com.pelsoczi.popularmovies.rest.ApiInterface;
import com.pelsoczi.popularmovies.rest.RestClient;
import com.pelsoczi.popularmovies.util.Files;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    public static final String MOVIE_INDEX = "index";

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private Movie movie;
    private List<Review> reviews = new ArrayList<Review>();
    private List<Video> videos = new ArrayList<Video>();
    private boolean mIsFavorite;

    private ImageView poster;
    private ImageView backdrop;
    private TextView release;
    private TextView average;
    private TextView synopsis;
    private FloatingActionButton fab;
    private Button reviewsButton;

    public static DetailFragment newInstance(Movie movie, int index) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Movie.TAG, movie);
        bundle.putInt(MOVIE_INDEX, index);
        DetailFragment detail = new DetailFragment();
        detail.setArguments(bundle);
        return detail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        movie = getArguments().getParcelable(Movie.TAG);
        init();
    }

    private void init() {
        int id = movie.getId();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        Call<ReviewsResponse> callReviews = apiService.getReviews(id,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        callReviews.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                reviews = response.body().getReviews();
                if (reviews.size() == 0) {
                    reviewsButton.setEnabled(false);
                }
            }
            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure(), " + t.toString());
            }
        });

        Call<VideosResponse> callVideos = apiService.getVideos(id,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        callVideos.enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(Call<VideosResponse> call, Response<VideosResponse> response) {
                videos = response.body().getVideos();
                if (videos.size() == 0) {
                    fab.hide();
                }
            }
            @Override
            public void onFailure(Call<VideosResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure(), " + t.toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        poster = (ImageView) rootView.findViewById(R.id.detail_poster);
        backdrop = (ImageView) rootView.findViewById(R.id.detail_backdrop);
        release = (TextView) rootView.findViewById(R.id.detail_release);
        average = (TextView) rootView.findViewById(R.id.detail_vote_average);
        synopsis = (TextView) rootView.findViewById(R.id.detail_plot_synopsis);
        fab = (FloatingActionButton) rootView.findViewById(R.id.detail_fab_trailer);
        reviewsButton = (Button) rootView.findViewById(R.id.detail_reviews);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.PROJECTION_COLUMNS,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movie.getId())},
                null
        );

        mIsFavorite = cursor != null && cursor.moveToFirst();
        if (mIsFavorite) {
            cursor.close();
        }

        File directory = Files.getDirectory(getActivity());

        Uri backdropUri = mIsFavorite ?
                Uri.fromFile(Files.getBackdropFile(directory, movie)) : Files.getBackdropUri(movie);
        Picasso.with(getActivity())
                .load(backdropUri)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.placeholder_backdrop)
                .error(R.drawable.error_backdrop)
                .into(backdrop);

        Uri posterUri = mIsFavorite ?
                Uri.fromFile(Files.getPosterFile(directory, movie)) : Files.getPosterUri(movie);
        Picasso.with(getActivity())
                .load(posterUri)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.error_poster)
                .into(poster);

        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat out = new SimpleDateFormat("yyyy");
            release.setText(out.format(in.parse(movie.getReleaseDate())));
        } catch (ParseException e) {
            release.setText(movie.getReleaseDate());
        }

        average.setText(String.valueOf(movie.getVoteAverage()));
        synopsis.setText(String.valueOf(movie.getOverview()));

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        toolbar.setTitle(movie.getOriginalTitle());
        if (!movie.getOriginalTitle().equals(movie.getTitle())) {
            toolbar.setSubtitle(movie.getTitle());
        }
        else {
            toolbar.setSubtitle(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String BASE_URL = "http://www.youtube.com/watch";
                final String PARAM_VIDEO = "v";
                final String KEY_VIDEO = videos.get(0).getKey();

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_VIDEO, KEY_VIDEO)
                        .build();

                startActivity(new Intent(Intent.ACTION_VIEW, builtUri));
            }
        });
        reviewsButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewsDialog();
            }
        });
    }

    private void showReviewsDialog() {
        ReviewDialog dialog = ReviewDialog.newInstance(reviews, movie);
        dialog.show(getActivity().getSupportFragmentManager(), ReviewDialog.TAG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        int iconRes = mIsFavorite ?
                R.drawable.ic_bookmark_green_24dp : R.drawable.ic_bookmark_border_black_24dp;
        menu.findItem(R.id.action_favorite).setIcon(iconRes);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            if (!mIsFavorite) {
                new SaveMovieTask().execute(movie);
                return true;
            }
            else {
                new DeleteMovieTask().execute(movie);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public int getShownIndex() {
        return getArguments().getInt(MOVIE_INDEX, -1);
    }


    private class SaveMovieTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... params) {
            Context context = getActivity();
            File directory = Files.getDirectory(getActivity());

            File backdrop = Files.getBackdropFile(directory, movie);
            FileOutputStream backdropFOS = null;
            try {
                backdropFOS = new FileOutputStream(backdrop);
                Bitmap bmp = Picasso.with(context)
                        .load(Files.getBackdropUri(movie))
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .get();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, backdropFOS);
                backdropFOS.close();
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error saving backdrop to internal storage: " + e.getMessage());
            }

            File poster = Files.getPosterFile(directory, movie);
            FileOutputStream posterFOS = null;
            try {
                posterFOS = new FileOutputStream(poster);
                Bitmap bmp = Picasso.with(context)
                        .load(Files.getPosterUri(movie))
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .get();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, posterFOS);
                posterFOS.close();
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error saving poster to internal storage: " + e.getMessage());
            }

            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, movie.getBackdropPath());
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVoteAverage());

            context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsFavorite = !mIsFavorite;
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    private class DeleteMovieTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... params) {
            Context context = getActivity();
            File directory = Files.getDirectory(getActivity());

            File poster = Files.getPosterFile(directory, movie);
            poster.delete();

            File backdrop = Files.getBackdropFile(directory, movie);
            backdrop.delete();

            String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
            String[] selectionArgs = new String[] {String.valueOf(movie.getId())};
            context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                    selection, selectionArgs);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsFavorite = !mIsFavorite;
            getActivity().supportInvalidateOptionsMenu();
        }
    }
}