package com.movie.android.moviemania;

/**
 * Created by raanand on 2/21/16.
 */
public class Movie {

    String originalTitle;
    String overview;
    String voteAverage;
    String releaseDate;
    String posterName;
    String thumbnail;

    public Movie(String originalTitle, String overview, String voteAverage, String releaseDate, String posterName,String thumbnail) {

        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.posterName = posterName;
        this.thumbnail = thumbnail;
    }
}
