package com.parithi.movieexplorer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parithi.movieexplorer.R;
import com.parithi.movieexplorer.managers.MovieManager;
import com.parithi.movieexplorer.models.Movie;

public class MovieListFragment extends Fragment implements MovieManager.MovieManagerDelegate {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private RecyclerView mMovieRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private GridLayoutManager mGridLayoutManager;

    public MovieListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMovieRecyclerView = (RecyclerView) view.findViewById(R.id.movie_list_recycler_view);
        mGridLayoutManager = new GridLayoutManager(getActivity(),2);

        mMovieRecyclerView.setHasFixedSize(true);
        mMovieRecyclerView.setLayoutManager(mGridLayoutManager);

        MovieManager.getInstance().setMovieManagerDelegate(this);
        MovieManager.getInstance().fetchMoviesFromURL();
    }

    @Override
    public void notifyMoviesLoaded() {
        Log.d(LOG_TAG,"Notify Movies Loaded");
        mMovieListAdapter = new MovieListAdapter();
        mMovieRecyclerView.setAdapter(mMovieListAdapter);
    }

    private class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder>{

        private Object[] mMoviesList;

        public MovieListAdapter(){
            mMoviesList = MovieManager.getInstance().getMovieList().values().toArray();
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_poster_layout, parent, false);
            MovieViewHolder movieViewHolder = new MovieViewHolder(v);
            return movieViewHolder;
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            if(mMoviesList[position]!=null) {
                holder.movieTitleTextView.setText(((Movie) mMoviesList[position]).getTitle());
                Glide.with(MovieListFragment.this).load(((Movie) mMoviesList[position]).getImageThumbnailURL()).centerCrop().crossFade().into(holder.moviePosterImageView);
            }
        }

        @Override
        public int getItemCount() {
            return (mMoviesList==null) ? 0 : mMoviesList.length;
        }
    }

    private static class MovieViewHolder extends RecyclerView.ViewHolder{

        public TextView movieTitleTextView;
        public ImageView moviePosterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePosterImageView = (ImageView) itemView.findViewById(R.id.movie_poster_imageview);
            movieTitleTextView = (TextView) itemView.findViewById(R.id.movie_title_textview);
        }
    }
}
