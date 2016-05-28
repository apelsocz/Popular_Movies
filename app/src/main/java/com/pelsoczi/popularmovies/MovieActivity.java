package com.pelsoczi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pelsoczi.popularmovies.models.Movie;

public class MovieActivity extends AppCompatActivity {

    private static String LOG_TAG = MovieActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.container_detail) != null) {
            mTwoPane = true;
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.container_movie, new MovieFragment(), MovieFragment.TAG)
//                        .commit();
        }
        else {
            mTwoPane = false;
        }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return true;
    }

    void showDetails(Movie movie, int index) {
        if (mTwoPane) {
            DetailFragment detail = (DetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_detail);

            //// TODO: 16-05-28 add to backstack
            if (detail == null || detail.getShownIndex() != index) {
                detail = DetailFragment.newInstance(movie, index);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_detail, detail, DetailFragment.TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Movie.TAG, movie)
                    .putExtra(DetailFragment.MOVIE_INDEX, index);
            startActivity(intent);
        }
    }
}