package com.movie.android.moviemania.moviedbapi;

/**
 * Created by raanand on 3/8/16.
 */
public enum SortCriteria {

    HIGEST_RATED("vote_average.desc"),
    MOST_POPULAR("popularity.desc");

    private String sortOption;

    SortCriteria(String sortOption) {
        this.sortOption = sortOption;
    }

    public String getSortOption() {
        return sortOption;
    }
}
