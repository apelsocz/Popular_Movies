package com.pelsoczi.popularmovies.util;

import android.content.Context;
import android.net.Uri;

import com.pelsoczi.popularmovies.models.Movie;

import java.io.File;

public class Files {

    private static final String DIR_NAME = "images";
    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String PARAM_SIZE_BACKDROP = "w780";
    private static final String PARAM_SIZE_POSTER = "w500";

    public static File getDirectory(Context context) {
        return context.getDir(DIR_NAME, Context.MODE_PRIVATE);
    }

    public static File getBackdropFile(File directory, Movie movie) {
        return new File(directory, getBackdropFileName(movie));
    }

    public static String getBackdropFileName(Movie movie) {
        return movie.getBackdropPath().substring(1);
    }

    public static File getPosterFile(File directory, Movie movie) {
        return new File(directory, getPosterFileName(movie));
    }

    public static String getPosterFileName(Movie movie) {
        return movie.getPosterPath().substring(1);
    }

    public static Uri getBackdropUri(Movie movie) {
        return Uri.parse(BASE_URL).buildUpon()
                .appendPath(PARAM_SIZE_BACKDROP)
                .appendEncodedPath(movie.getBackdropPath())
                .build();
    }

    public static Uri getPosterUri(Movie movie) {
        return Uri.parse(BASE_URL).buildUpon()
                .appendPath(PARAM_SIZE_POSTER)
                .appendEncodedPath(movie.getPosterPath())
                .build();
    }
}