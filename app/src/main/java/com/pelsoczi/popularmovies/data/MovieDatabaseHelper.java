package com.pelsoczi.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pelsoczi.popularmovies.data.MovieContract.MovieEntry;


public class MovieDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieDatabaseHelper.class.getSimpleName();

    private static MovieDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public static synchronized MovieDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MovieDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_POSTS_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME +
                " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                    MovieEntry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL," +
                    " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE" +
                ");";

        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}