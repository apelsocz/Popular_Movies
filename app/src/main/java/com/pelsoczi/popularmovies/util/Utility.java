package com.pelsoczi.popularmovies.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.pelsoczi.popularmovies.R;

public class Utility {

    public static String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_favorite));
    }

    public static String getSortLabel(Context context) {
        String sort = getSortOrder(context);
        if (sort.equals(context.getString(R.string.pref_sort_popular))) {
            return context.getString(R.string.pref_sort_label_popular);
        }
        if (sort.equals(context.getString(R.string.pref_sort_rating))) {
            return context.getString(R.string.pref_sort_label_rating);
        }
        if (sort.equals(context.getString(R.string.pref_sort_favorite))) {
            return context.getString(R.string.pref_sort_label_favorite);
        }
        return null;
    }

    public static int getSortIconResId (Context context) {
        String sort = getSortOrder(context);
        if (sort.equals(context.getString(R.string.pref_sort_popular))) {
            return R.drawable.ic_whatshot_black_24dp;
        }
        if (sort.equals(context.getString(R.string.pref_sort_rating))) {
            return R.drawable.ic_stars_black_24dp;
        }
        if (sort.equals(context.getString(R.string.pref_sort_favorite))) {
            return R.drawable.ic_movie_filter_black_24dp;
        }
        return 0;
    }

    public static int getGridColumnCount(Context context) {
        boolean tablet = context.getResources().getBoolean(R.bool.isTablet);
        int orientation = context.getResources().getConfiguration().orientation;
        int columns;
        if (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columns = 4;
        }
        else {
            columns = 2;
        }

        return getSortOrder(context).equals(context.getString(R.string.pref_sort_favorite))
                ? columns/2 : columns;
    }
}