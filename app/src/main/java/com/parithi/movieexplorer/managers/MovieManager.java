package com.parithi.movieexplorer.managers;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Parithi on 21/12/15.
 */
public class MovieManager {

    private final static String API_KEY = "3e9001bdbcd8d0e00e416d1826befaa5";
    private MovieManagerDelegate mMovieManagerDelegate;
    private static MovieManager mMovieManagerInstance;
    private LinkedHashMap<Long, Movie> movieList;

    public static MovieManager getInstance() {
        if(mMovieManagerInstance ==null){
            mMovieManagerInstance = new MovieManager();
        }
        return mMovieManagerInstance;
    }

    private MovieManager() {
    }

    public void fetchMoviesFromURL(){
        new MovieFetcherTask().execute("popularity.desc");
    }

    public LinkedHashMap<Long, Movie> getMovieList() {
        Log.d("MovieManager",movieList.size() + "");
        return movieList;
    }

    public void setMovieManagerDelegate(MovieManagerDelegate mMovieManagerDelegate) {
        this.mMovieManagerDelegate = mMovieManagerDelegate;
    }

    public static interface MovieManagerDelegate{
        void notifyMoviesLoaded();
    }

    public class MovieFetcherTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = MovieFetcherTask.class.getSimpleName();

        private Movie[] getMoviesFromJson(String movieJsonStr) throws JSONException {

            final String MOVIE_RESULTS = "results";
            final String MOVIE_ID = "id";
            final String MOVIE_POSTER_PATH = "poster_path";
            final String MOVIE_TITLE = "title";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_RATING = "vote_average";
            final String MOVIE_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);

            Movie[] resultStrs = new Movie[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {

                long id;
                String title;
                String imageThumbnailURL;
                String plotSynopsis;
                double userRating;
                String releaseDate;

                JSONObject movieJsonObject = movieArray.getJSONObject(i);

                id = movieJsonObject.getLong(MOVIE_ID);
                title = movieJsonObject.getString(MOVIE_TITLE);
                imageThumbnailURL = "http://image.tmdb.org/t/p/w342/" + movieJsonObject.getString(MOVIE_POSTER_PATH);
                plotSynopsis = movieJsonObject.getString(MOVIE_OVERVIEW);
                userRating = movieJsonObject.getDouble(MOVIE_RATING);
                releaseDate = movieJsonObject.getString(MOVIE_RELEASE_DATE);
                resultStrs[i] = new Movie(id,title,imageThumbnailURL,plotSynopsis,userRating,releaseDate);
            }

            return resultStrs;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);

            if(movieList == null){
                movieList = new LinkedHashMap<>();
            }

            for(Movie movie : movies){
                movieList.put(movie.getId(),movie);
            }

            if(mMovieManagerDelegate!=null){
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
                final String MOVIEDB_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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

                Log.v(LOG_TAG, "Movie JSON string: " + movieJsonStr);
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
                return getMoviesFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }
}
