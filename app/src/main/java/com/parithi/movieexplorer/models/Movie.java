package com.parithi.movieexplorer.models;

import java.util.Date;

/**
 * Created by Parithi on 21/12/15.
 */
public class Movie {
    private String title;
    private String imageThumbnailURL;
    private String plotSynopsis;
    private int userRating;
    private Date releaseDate;

    public Movie(String title, String imageThumbnailURL, String plotSynopsis, int userRating, Date releaseDate) {
        this.title = title;
        this.imageThumbnailURL = imageThumbnailURL;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
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

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
