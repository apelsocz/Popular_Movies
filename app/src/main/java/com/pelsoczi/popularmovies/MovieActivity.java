package com.pelsoczi.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MovieActivity extends AppCompatActivity {

    private static String LOG_TAG = MovieActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));

        String title = sort.equals(getString(R.string.pref_sort_popular)) ?
                getString(R.string.pref_sort_label_popular) :
                getString(R.string.pref_sort_label_rating);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("  " + title);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_movie, new MovieFragment(), MovieFragment.TAG)
                    .commit();
        }
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
}