package com.parithi.movieexplorer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.parithi.movieexplorer.R;
import com.parithi.movieexplorer.fragments.MovieDetailFragment;
import com.parithi.movieexplorer.fragments.MovieListFragment;

public class MainActivity extends AppCompatActivity {

    FrameLayout movieDetailLayout;
    boolean isTablet;
    long lastLoadedId;

    public boolean isTablet() {
        return isTablet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movieDetailLayout = (FrameLayout) findViewById(R.id.movie_detail_container);
        if(movieDetailLayout!=null){
            isTablet = true;
        }
    }

    public void loadMovieToDetail(final long id){
        if(lastLoadedId!=id) {
            Handler taskHandler = new Handler();
            taskHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (movieDetailLayout != null) {
                        isTablet = true;
                        lastLoadedId = id;
                        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
                        Bundle movieBundle = new Bundle();
                        movieBundle.putLong(Intent.EXTRA_TEXT, id);
                        movieDetailFragment.setArguments(movieBundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, movieDetailFragment)
                                .commit();
                    }
                }
            });
        }
    }


}
