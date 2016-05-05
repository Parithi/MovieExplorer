package com.parithi.movieexplorer.managers;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.parithi.movieexplorer.Constants;
import com.parithi.movieexplorer.MovieExplorerApplication;
import com.parithi.movieexplorer.data.MovieContract;
import com.parithi.movieexplorer.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * Created by Parithi on 21/12/15.
 */
public class MovieManager {

    private MovieManagerDelegate mMovieManagerDelegate;
    private static MovieManager mMovieManagerInstance;
    private LinkedHashMap<Long, Movie> movieList;

    public static MovieManager getInstance() {
        if (mMovieManagerInstance == null) {
            mMovieManagerInstance = new MovieManager();
        }
        return mMovieManagerInstance;
    }

    private MovieManager() {
    }

    public void fetchMoviesFromURL() {
        new MovieFetcherTask().execute(MovieExplorerApplication.getStoredSortMode());
    }

    public LinkedHashMap<Long, Movie> getMovieList() {
        return movieList;
    }

    public void setMovieManagerDelegate(MovieManagerDelegate mMovieManagerDelegate) {
        this.mMovieManagerDelegate = mMovieManagerDelegate;
    }

    public static interface MovieManagerDelegate {
        void notifyMoviesLoaded();
    }

    public class MovieFetcherTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = MovieFetcherTask.class.getSimpleName();

        private void getMoviesFromJson(String movieJsonStr) throws JSONException {

            final String MOVIE_RESULTS = "results";
            final String MOVIE_ID = "id";
            final String MOVIE_POSTER_PATH = "poster_path";
            final String MOVIE_BACKDROP_PATH = "backdrop_path";
            final String MOVIE_TITLE = "title";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_RATING = "vote_average";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_POPULARITY = "popularity";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);

            Movie[] resultStrs = new Movie[movieArray.length()];
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {

                long id;
                String title;
                String imageThumbnailURL;
                String movieBackDropURL;
                String plotSynopsis;
                double userRating;
                String releaseDate;
                double popularity;

                JSONObject movieJsonObject = movieArray.getJSONObject(i);

                id = movieJsonObject.getLong(MOVIE_ID);
                title = movieJsonObject.getString(MOVIE_TITLE);
                imageThumbnailURL = "http://image.tmdb.org/t/p/w185/" + movieJsonObject.getString(MOVIE_POSTER_PATH);
                movieBackDropURL = "http://image.tmdb.org/t/p/w500/" + movieJsonObject.getString(MOVIE_BACKDROP_PATH);
                plotSynopsis = movieJsonObject.getString(MOVIE_OVERVIEW);
                userRating = movieJsonObject.getDouble(MOVIE_RATING);
                releaseDate = movieJsonObject.getString(MOVIE_RELEASE_DATE);
                popularity = movieJsonObject.getDouble(MOVIE_POPULARITY);
                resultStrs[i] = new Movie(id, title, imageThumbnailURL, movieBackDropURL, plotSynopsis, userRating, releaseDate);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry._ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_URL, imageThumbnailURL);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_URL, movieBackDropURL);
                movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, plotSynopsis);
                movieValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, userRating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = MovieExplorerApplication.getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);

            if (movieList == null) {
                movieList = new LinkedHashMap<>();
            } else {
                movieList.clear();
            }

            if(movies!=null) {
                for (Movie movie : movies) {
                    movieList.put(movie.getId(), movie);
                }
            }

            if (mMovieManagerDelegate != null) {
                mMovieManagerDelegate.notifyMoviesLoaded();
            }
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {
                Uri builtUri = Uri.parse(Constants.MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(Constants.DISCOVER_PARAM)
                        .appendPath(Constants.MOVIE_PARAM)
                        .appendQueryParameter(Constants.SORT_PARAM, params[0])
                        .appendQueryParameter(Constants.API_KEY_PARAM, Constants.API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                getMoviesFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }
}
