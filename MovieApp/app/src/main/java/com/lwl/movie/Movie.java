package com.lwl.movie;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "movie")
public class Movie implements Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "title")
    private String Title;

    @ColumnInfo(name = "plot")
    private  String Plot;

    @ColumnInfo(name = "genre")
    private String Genre;

    @ColumnInfo(name = "myRating")
    private Double MyRating = 0.0;

    @ColumnInfo(name = "imdbRating")
    private String ImdbRating;

    @ColumnInfo(name = "userComment")
    private String UserComment = "";

    @ColumnInfo(name = "isWatched")
    private Boolean MovieWatched = false; //private Boolean IsMovieWatched = false;

    @ColumnInfo(name = "genreIcon")
    private Integer GenreIcon; // ikonet

    public Movie(){}

    public Movie(@NonNull String title, String plot, String genre, Double myRating, String imdbRating, String userComment, Boolean isMovieWatched, Integer genreIcon) {
        Title = title;
        Plot = plot;
        Genre = genre;
        MyRating = myRating;
        ImdbRating = imdbRating;
        UserComment = userComment;
        MovieWatched = isMovieWatched; //IsMovieWatched = isMovieWatched;
        GenreIcon = genreIcon;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPlot() {
        return Plot;
    }

    public void setPlot(String plot) {
        Plot = plot;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public Double getMyRating() {
        return MyRating;
    }

    public void setMyRating(Double myRating) {
        MyRating = myRating;
    }

    public String getImdbRating() {
        return ImdbRating;
    }

    public void setImdbRating(String imdbRating) {
        ImdbRating = imdbRating;
    }

    public String getUserComment() {
        return UserComment;
    }

    public void setUserComment(String userComment) {
        UserComment = userComment;
    }

    public Boolean getMovieWatched() {
        return MovieWatched;
    }

    public void setMovieWatched(Boolean movieWatched) {
        MovieWatched = movieWatched;
    }

    public Integer getGenreIcon() {
        return GenreIcon;
    }

    public void setGenreIcon(Integer genreIcon) {
        GenreIcon = genreIcon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Title);
        dest.writeString(this.Plot);
        dest.writeString(this.Genre);
        dest.writeValue(this.MyRating);
        dest.writeString(this.ImdbRating);
        dest.writeString(this.UserComment);
        dest.writeValue(this.MovieWatched);
        dest.writeValue(this.GenreIcon);
    }

    protected Movie(Parcel in) {
        this.Title = in.readString();
        this.Plot = in.readString();
        this.Genre = in.readString();
        this.MyRating = (Double) in.readValue(Double.class.getClassLoader());
        this.ImdbRating = in.readString();
        this.UserComment = in.readString();
        this.MovieWatched = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.GenreIcon = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
