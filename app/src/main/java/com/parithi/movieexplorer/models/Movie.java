package com.parithi.movieexplorer.models;

import java.util.Date;

/**
 * Created by Parithi on 21/12/15.
 */
public class Movie {
    private long id;
    private String title;
    private String imageThumbnailURL;
    private String plotSynopsis;
    private double userRating;
    private String releaseDate;

    public Movie(long id, String title, String imageThumbnailURL, String plotSynopsis, double userRating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.imageThumbnailURL = imageThumbnailURL;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageThumbnailURL() {
        return imageThumbnailURL;
    }

    public void setImageThumbnailURL(String imageThumbnailURL) {
        this.imageThumbnailURL = imageThumbnailURL;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
