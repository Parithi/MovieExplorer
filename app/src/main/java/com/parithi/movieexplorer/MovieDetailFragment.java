package com.parithi.movieexplorer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parithi.movieexplorer.managers.MovieManager;
import com.parithi.movieexplorer.models.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    ImageView mBackDropImageView;
    TextView mMovieDetailTextView;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBackDropImageView = (ImageView) view.findViewById(R.id.backdrop_imageview);
        mMovieDetailTextView = (TextView) view.findViewById(R.id.movie_title_textview);


        if(getActivity().getIntent().getExtras()!=null){
            long id = getActivity().getIntent().getLongExtra(Intent.EXTRA_TEXT,-1);

            if(id != -1){
                Movie currentMovie = MovieManager.getInstance().getMovieList().get(id);
                if(currentMovie!=null){
                    Glide.with(MovieDetailFragment.this).load(currentMovie.getMovieBackDropURL()).centerCrop().crossFade().into(mBackDropImageView);
                    mMovieDetailTextView.setText(currentMovie.getTitle());
                }
            }
        }

    }
}
