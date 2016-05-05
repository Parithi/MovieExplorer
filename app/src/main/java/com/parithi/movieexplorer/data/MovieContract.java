package com.parithi.movieexplorer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by earul on 3/2/16.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.parithi.movieexplorer.movies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_THUMBNAIL_URL = "imageThumbnailURL";
        public static final String COLUMN_MOVIE_BACKDROP_URL = "movieBackDropURL";
        public static final String COLUMN_PLOT_SYNOPSIS= "plotSynopsis";
        public static final String COLUMN_USER_RATING = "userRating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_IS_FAVORITE = "isFavorite";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromMovieUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }



}
