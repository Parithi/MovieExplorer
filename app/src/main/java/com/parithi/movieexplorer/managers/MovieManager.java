package com.parithi.movieexplorer.managers;

/**
 * Created by Parithi on 21/12/15.
 */
public class MovieManager {
    private MovieManagerDelegate mMovieManagerDelegate;
    private static MovieManager mMovieManagerInstance;
    public static MovieManager getInstance() {
        if(mMovieManagerInstance ==null){
            mMovieManagerInstance = new MovieManager();
        }
        return mMovieManagerInstance;
    }

    private MovieManager() {
    }

    public void fetchMoviesFromURL(){

    }

    public MovieManagerDelegate getmMovieManagerDelegate() {
        return mMovieManagerDelegate;
    }

    public void setmMovieManagerDelegate(MovieManagerDelegate mMovieManagerDelegate) {
        this.mMovieManagerDelegate = mMovieManagerDelegate;
    }

    public static MovieManager getmMovieManagerInstance() {
        return mMovieManagerInstance;
    }

    public static void setmMovieManagerInstance(MovieManager mMovieManagerInstance) {
        MovieManager.mMovieManagerInstance = mMovieManagerInstance;
    }

    public static interface MovieManagerDelegate{
        void moviesLoaded();
    }
}
