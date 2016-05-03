package com.movie.android.moviemania.moviedbapi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.movie.android.moviemania.moviedbapi.MovieContract.MovieEntry;

/**
 * DB Helper
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private final static String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private  static  final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.MOVIE_ID + " TEXT UNIQUE NOT NULL," +
                MovieEntry.MOVIE_BACKDROP_URI + " TEXT NOT NULL," +
                MovieEntry.MOVIE_TITLE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_POSTER + " TEXT NOT NULL," +
                MovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.MOVIE_VOTE_AVERAGE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_REVIEWS + " TEXT NOT NULL," +
                MovieEntry.MOVIE_TRAILERS + " TEXT NOT NULL," +
                "UNIQUE (" + MovieEntry.MOVIE_ID +") ON CONFLICT IGNORE"+
                " );";

        Log.v(LOG_TAG, "Create table query" + SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
