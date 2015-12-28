package com.parithi.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parithi.movieexplorer.R;
import com.parithi.movieexplorer.managers.MovieManager;
import com.parithi.movieexplorer.models.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    ImageView mBackDropImageView;
    ImageView mMoviePosterImageView;
    TextView mMovieTitleTextView;
    TextView mMovieRatingTextView;
    TextView mReleaseDateTextView;
    TextView mPlotSynopsisTextView;
    TextView mMovieSubTitleTextView;


    public MovieDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBackDropImageView = (ImageView) view.findViewById(R.id.backdrop_imageview);
        mMovieTitleTextView = (TextView) view.findViewById(R.id.movie_title_textview);
        mMovieSubTitleTextView = (TextView) view.findViewById(R.id.movie_sub_title_textview);
        mMovieRatingTextView = (TextView) view.findViewById(R.id.movie_rating_textview);
        mReleaseDateTextView = (TextView) view.findViewById(R.id.release_date_textview);
        mPlotSynopsisTextView = (TextView) view.findViewById(R.id.plot_synopsis_textview);
        mMoviePosterImageView = (ImageView) view.findViewById(R.id.movie_poster_imageview);

        if (getActivity().getIntent().getExtras() != null) {
            long id = getActivity().getIntent().getLongExtra(Intent.EXTRA_TEXT, -1);

            if (id != -1) {
                Movie currentMovie = MovieManager.getInstance().getMovieList().get(id);
                if (currentMovie != null) {
                    Glide.with(MovieDetailFragment.this).load(currentMovie.getMovieBackDropURL()).centerCrop().crossFade().into(mBackDropImageView);
                    mMovieTitleTextView.setText(currentMovie.getTitle());
                    mMovieSubTitleTextView.setText(currentMovie.getTitle());
                    mMovieRatingTextView.setText(String.format("%.2g", currentMovie.getUserRating()));
                    mReleaseDateTextView.setText(currentMovie.getReleaseDate());
                    mPlotSynopsisTextView.setText(currentMovie.getPlotSynopsis());
                    Glide.with(MovieDetailFragment.this).load(currentMovie.getImageThumbnailURL()).centerCrop().crossFade().into(mMoviePosterImageView);
                }
            }
        }

    }
}
