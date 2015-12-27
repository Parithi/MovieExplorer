package com.parithi.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parithi.movieexplorer.DetailActivity;
import com.parithi.movieexplorer.R;
import com.parithi.movieexplorer.managers.MovieManager;
import com.parithi.movieexplorer.models.Movie;

public class MovieListFragment extends Fragment implements MovieManager.MovieManagerDelegate {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private GridView mMovieGridView;
    private MovieListAdapter mMovieListAdapter;

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

        mMovieGridView = (GridView) view.findViewById(R.id.movie_list_gridview);

        MovieManager.getInstance().setMovieManagerDelegate(this);
        MovieManager.getInstance().fetchMoviesFromURL();
    }

    @Override
    public void notifyMoviesLoaded() {
        Log.d(LOG_TAG,"Notify Movies Loaded");
        mMovieListAdapter = new MovieListAdapter();
        mMovieGridView.setAdapter(mMovieListAdapter);
    }

    private class MovieListAdapter extends BaseAdapter{

        private Object[] mMoviesList;

        public MovieListAdapter(){
            mMoviesList = MovieManager.getInstance().getMovieList().values().toArray();
        }


        @Override
        public int getCount() {
            return (mMoviesList==null) ? 0 : mMoviesList.length;
        }

        @Override
        public Object getItem(int position) {
            return mMoviesList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MovieViewHolder movieViewHolder;
            if(convertView==null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.movie_poster_layout,parent,false);
                movieViewHolder = new MovieViewHolder();
                movieViewHolder.movieTitleTextView = (TextView) convertView.findViewById(R.id.movie_title_textview);
                movieViewHolder.moviePosterImageView = (ImageView) convertView.findViewById(R.id.movie_poster_imageview);
                movieViewHolder.movieRatingTextView = (TextView) convertView.findViewById(R.id.movie_rating_textview);
                convertView.setTag(movieViewHolder);
            } else {
                movieViewHolder = (MovieViewHolder) convertView.getTag();
            }

            movieViewHolder.movieTitleTextView.setText(((Movie) mMoviesList[position]).getTitle());
            movieViewHolder.movieRatingTextView.setText(String.format("%.2g", ((Movie) mMoviesList[position]).getUserRating()));
            Glide.with(MovieListFragment.this).load(((Movie) mMoviesList[position]).getImageThumbnailURL()).centerCrop().crossFade().into(movieViewHolder.moviePosterImageView);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                    detailIntent.putExtra(Intent.EXTRA_TEXT,((Movie) mMoviesList[position]).getId());
                    startActivity(detailIntent);
                }
            });
            return convertView;
        }
    }

    private static class MovieViewHolder {

        public TextView movieTitleTextView;
        public ImageView moviePosterImageView;
        public TextView movieRatingTextView;
    }
}
