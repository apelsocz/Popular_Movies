package com.pelsoczi.popularmovies.ui;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pelsoczi.popularmovies.BuildConfig;
import com.pelsoczi.popularmovies.MovieActivity;
import com.pelsoczi.popularmovies.R;
import com.pelsoczi.popularmovies.data.MovieContract;
import com.pelsoczi.popularmovies.models.Movie;
import com.pelsoczi.popularmovies.models.MoviesResponse;
import com.pelsoczi.popularmovies.rest.ApiInterface;
import com.pelsoczi.popularmovies.rest.RestClient;
import com.pelsoczi.popularmovies.util.Files;
import com.pelsoczi.popularmovies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MovieFragment.class.getSimpleName();
    public static final String LOG_TAG = MovieFragment.class.getSimpleName();

    private static final String STATE_LAYOUT = "layoutState";
    private static final String STATE_MOVIES = "listState";
    private static final String STATE_SORT = "sortState";
    private static final int LOADER_MOVIE = 0;

    private RecyclerView mRecycler;
    private Adapter mAdapter;
    private List<Movie> movies = new ArrayList<Movie>();
    private Bundle mSavedInstanceState;

    public MovieFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_movies);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mSavedInstanceState = (savedInstanceState != null) ? savedInstanceState : null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MovieActivity)getActivity()).updateUI();

        String sort = Utility.getSortOrder(getActivity());
        if (sort.equals(getString(R.string.pref_sort_favorite))) {
            getLoaderManager().restartLoader(LOADER_MOVIE, null, this);
        }
        else {
            if (mSavedInstanceState == null) {
                downloadMovies();
            }
            else {
                String savedSort = mSavedInstanceState.getString(STATE_SORT, "");
                if (!savedSort.equals(sort)) {
                    mRecycler.setAdapter(null);
                    downloadMovies();
                }
                else {
                    new DownloadPosterTask().execute(
                            (ArrayList)mSavedInstanceState.getParcelableArrayList(STATE_MOVIES)
                    );
                }
            }
        }
    }

    private void downloadMovies() {
        String sort = Utility.getSortOrder(getActivity());
        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = sort.equals(getString(R.string.pref_sort_popular)) ?
                apiService.getPopularMovies(BuildConfig.THE_MOVIE_DATABASE_API_KEY) :
                apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DATABASE_API_KEY);

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call,
                                   Response<MoviesResponse> response) {
                new DownloadPosterTask().execute(response.body().getMovies());
                Log.v(LOG_TAG, "onResponse(), movies returned " + response.body().getMovies().size());
            }
            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure(), " + t.toString());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SORT, Utility.getSortOrder(getActivity()));
        outState.putParcelableArrayList(STATE_MOVIES, (ArrayList) movies);
        if (mRecycler.getLayoutManager() != null) {
            outState.putParcelable(STATE_LAYOUT, mRecycler.getLayoutManager().onSaveInstanceState());
        }
        mSavedInstanceState = outState;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_MOVIE:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.PROJECTION_COLUMNS,
                        null,
                        null,
                        MovieContract.MovieEntry._ID + " ASC"
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int posterPath = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);
            int overview = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
            int releaseDate = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
            int id = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int originalTitle = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ORIGINAL_TITLE);
            int title = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
            int backdropPath = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH);
            int voteAverage = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE);
            movies.clear();
            do {
                Movie movie = new Movie();
                movie.setPosterPath(data.getString(posterPath));
                movie.setOverview(data.getString(overview));
                movie.setReleaseDate(data.getString(releaseDate));
                movie.setId(data.getInt(id));
                movie.setOriginalTitle(data.getString(originalTitle));
                movie.setTitle(data.getString(title));
                movie.setBackdropPath(data.getString(backdropPath));
                movie.setVoteAverage(data.getDouble(voteAverage));
                movies.add(movie);
            } while (data.moveToNext());
            updateRecyclerView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, "onLoaderReset");
    }

    private class DownloadPosterTask extends AsyncTask<List<Movie>, Void, Void> {

        @Override
        protected Void doInBackground(List<Movie>... params) {
            movies = params[0];
            Picasso picasso = Picasso.with(getActivity());
            for (Movie movie : movies) {
                picasso.load(Files.getPosterUri(movie)).fetch();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRecyclerView();
        }
    }

    private void updateRecyclerView() {
        initRecyclerView();
        if (mSavedInstanceState != null) {
            String savedSort = mSavedInstanceState.getString(STATE_SORT, "");
            if (savedSort.equals(Utility.getSortOrder(getActivity()))) {
                mRecycler.getLayoutManager()
                        .onRestoreInstanceState(mSavedInstanceState.getParcelable(STATE_LAYOUT));
            }
        }
        updateDetails();
    }

    private void initRecyclerView() {
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemAnimator(null);
        mRecycler.setLayoutManager(
                new GridLayoutManager(getActivity(), Utility.getGridColumnCount(getActivity())));
        mAdapter = new Adapter(getActivity(), movies);
        mAdapter.setHasStableIds(true);
        mRecycler.setAdapter(mAdapter);
    }

    private void updateDetails() {
        if (mSavedInstanceState == null) {
            //// TODO: 16-06-26 show first index in details fragment
            ((MovieActivity)getActivity()).loadDetails(movies.get(0), 0);
        }
        else {
            String sort = Utility.getSortOrder(getActivity());
            String savedSort = mSavedInstanceState.getString(STATE_SORT, "");
            if (!savedSort.equals(sort)) {
                //// TODO: 16-06-26 show first index in details fragment
                ((MovieActivity)getActivity()).loadDetails(movies.get(0), 0);
            }
        }
    }
}