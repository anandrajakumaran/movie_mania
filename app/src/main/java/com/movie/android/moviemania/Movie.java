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
    String id;
    String review;
    String trailer = "";

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public Creator<Movie> getCREATOR() {
        return CREATOR;
    }

    public Movie(String originalTitle, String overview, String voteAverage, String releaseDate, String posterName,String thumbnail,String id) {

        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.posterName = posterName;
        this.thumbnail = thumbnail;
        this.id = id;
        this.review=review;
        this.trailer = trailer;
    }

    private Movie(Parcel in){
        originalTitle = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
        posterName =in.readString();
        thumbnail=in.readString();
        id=in.readString();
        review=in.readString();
        trailer =in.readString();
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
        parcel.writeString(id);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

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
