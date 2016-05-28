package com.pelsoczi.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pelsoczi.popularmovies.models.Movie;
import com.pelsoczi.popularmovies.models.MoviesResponse;
import com.pelsoczi.popularmovies.rest.ApiInterface;
import com.pelsoczi.popularmovies.rest.RestClient;
import com.pelsoczi.popularmovies.util.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {

    public static String TAG = MovieFragment.class.getSimpleName();
    public static String LOG_TAG = MovieFragment.class.getSimpleName();

    private RecyclerView mRecycler;
    private Adapter mAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private List<Movie> movies = new ArrayList<Movie>();

    private static final String POSITION_KEY = "position";

    public MovieFragment(){}
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_refresh) {
//            updateMovies();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_movies);
        mRecycler.setHasFixedSize(true);
        int columns = Utility.getGridColumnCount(getActivity());

        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), columns));
        return rootView;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void updateMovies() {
        String sort = Utility.getSortOrder(getActivity());

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = sort.equals(getString(R.string.pref_sort_popular)) ?
                apiService.getPopularMovies(BuildConfig.THE_MOVIE_DATABASE_API_KEY) :
                apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DATABASE_API_KEY);

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                movies = response.body().getResults();
                mAdapter = new Adapter(getActivity(), movies);
                mAdapter.setHasStableIds(true);
                mRecycler.setAdapter(mAdapter);
                mRecycler.setItemViewCacheSize(movies.size());
                Log.v(LOG_TAG, "onResponse(), movies returned " + movies.size());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure(), " + t.toString());
            }
        });

        int iconResId = Utility.getSortIconResId(getActivity());
        String subtitle = Utility.getSortLabel(getActivity());

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        toolbar.setLogo(iconResId);
        toolbar.setSubtitle(subtitle);
    }
}