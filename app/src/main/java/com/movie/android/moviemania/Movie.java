package com.movie.android.moviemania;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by raanand on 2/21/16.
 */
public class Movie implements Parcelable{

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

    private Movie(Parcel in){
        originalTitle = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
        posterName =in.readString();
        thumbnail=in.readString();
    }

    public String toString() {
        return originalTitle + "--" + overview + "--" + voteAverage +"--" + releaseDate + "--" + posterName+ "--" + thumbnail;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(originalTitle);
        parcel.writeString(overview);
        parcel.writeString(voteAverage);
        parcel.writeString(releaseDate);
        parcel.writeString(posterName);
        parcel.writeString(thumbnail);
    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
