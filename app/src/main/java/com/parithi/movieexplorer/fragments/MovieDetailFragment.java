package com.parithi.movieexplorer.fragments;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parithi.movieexplorer.Constants;
import com.parithi.movieexplorer.R;
import com.parithi.movieexplorer.data.MovieContract;
import com.parithi.movieexplorer.models.Review;
import com.parithi.movieexplorer.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView mBackDropImageView;
    private ImageView mMoviePosterImageView;
    private TextView mMovieTitleTextView;
    private TextView mMovieRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mPlotSynopsisTextView;
    private TextView mMovieSubTitleTextView;
    private Button mFavoriteButton, mShareButton;
    private ProgressBar mReviewsProgressBar, mTrailersProgressBar;
    private LinearLayout mReviewsLayout, mTrailersLayout;
    private LinkedHashMap<String, Review> reviewsList;
    private LinkedHashMap<String, Trailer> trailersList;
    private String mFirstTrailerUrl;
    private static final int MOVIE_DETAIL_LOADER = 1;
    long movieId;
    private boolean isFavorite;
    private boolean isTrailersLoaded, isReviewsLoaded;

    String[] projection = {MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_USER_RATING,
            MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_URL,
            MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_URL,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE
    };

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
        mTrailersProgressBar = (ProgressBar) view.findViewById(R.id.trailers_progress);
        mReviewsProgressBar = (ProgressBar) view.findViewById(R.id.reviews_progress);
        mReviewsLayout = (LinearLayout) view.findViewById(R.id.reviews_layout);
        mTrailersLayout = (LinearLayout) view.findViewById(R.id.trailers_layout);
        mFavoriteButton = (Button) view.findViewById(R.id.favorite_button);
        mShareButton = (Button) view.findViewById(R.id.share_button);

        if(savedInstanceState==null) {
            if (getActivity().getIntent().getExtras() != null) {
                movieId = getActivity().getIntent().getLongExtra(Intent.EXTRA_TEXT, -1);
                if (movieId != -1) {
                    getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
                }
            } else if (getArguments() != null) {
                movieId = getArguments().getLong(Intent.EXTRA_TEXT, -1);
                if (movieId != -1) {
                    getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
                }
            }
        } else {
            movieId = savedInstanceState.getLong(Constants.LAST_LOADED_MOVIE_ID);
            if (movieId != -1) {
                getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
            }
        }

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(mFirstTrailerUrl)){
                    String shareBody = getString(R.string.check_out_label) + mFirstTrailerUrl;
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.buildMovieUri(movieId),
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        loadViews(data);
    }

    private void loadViews(Cursor data) {
        Glide.with(MovieDetailFragment.this).load(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_URL))).centerCrop().crossFade().into(mBackDropImageView);
        mMovieTitleTextView.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        mMovieSubTitleTextView.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        mMovieRatingTextView.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING)));
        mReleaseDateTextView.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        mPlotSynopsisTextView.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS)));

        isFavorite = data.getInt(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITE)) > 0;
        mFavoriteButton.setText(isFavorite ? getString(R.string.favorited_label) : "+ Favorite");

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFavorite();
            }
        });
        Glide.with(MovieDetailFragment.this).load(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_URL))).centerCrop().crossFade().into(mMoviePosterImageView);

        if(!isTrailersLoaded) {
            new ReviewFetcherTask().execute(data.getString(data.getColumnIndex(MovieContract.MovieEntry._ID)));
        }
        if(!isReviewsLoaded) {
            new TrailerFetcherTask().execute(data.getString(data.getColumnIndex(MovieContract.MovieEntry._ID)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateFavorite(){
        ContentValues mUpdateValues = new ContentValues();
        mUpdateValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, isFavorite ? 0 : 1);
        getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, mUpdateValues, MovieContract.MovieEntry._ID + "=?",new String[]{movieId+""});
    }

    public class ReviewFetcherTask extends AsyncTask<String, Void, Review[]> {

        private final String LOG_TAG = ReviewFetcherTask.class.getSimpleName();

        private Review[] getReviewsFromJson(String reviewJsonStr) throws JSONException {

            final String REVIEW_RESULTS = "results";
            final String REVIEW_ID = "id";
            final String REVIEW_AUTHOR = "author";
            final String REVIEW_CONTENT = "content";

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_RESULTS);

            Review[] resultStrs = new Review[reviewArray.length()];

            for (int i = 0; i < reviewArray.length(); i++) {

                String id;
                String author;
                String content;

                JSONObject reviewJsonObject = reviewArray.getJSONObject(i);

                id = reviewJsonObject.getString(REVIEW_ID);
                author = reviewJsonObject.getString(REVIEW_AUTHOR);
                content = reviewJsonObject.getString(REVIEW_CONTENT);
                resultStrs[i] = new Review(id, author, content);
            }

            return resultStrs;
        }

        @Override
        protected void onPostExecute(Review[] reviews) {
            super.onPostExecute(reviews);

            if (reviewsList == null) {
                reviewsList = new LinkedHashMap<>();
            } else {
                reviewsList.clear();
            }

            if(reviews!=null) {
                for (Review review : reviews) {
                    reviewsList.put(review.getId(), review);
                }
            }

            updateReviews();
        }

        @Override
        protected Review[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {
                Uri builtUri = Uri.parse(Constants.MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(Constants.MOVIE_PARAM)
                        .appendPath(params[0])
                        .appendPath(Constants.REVIEWS_PARAM)
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
                return getReviewsFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    private void updateReviews() {
        mReviewsLayout.removeAllViews();
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mReviewsProgressBar.setVisibility(View.GONE);
                    if (reviewsList.size() > 0) {
                        for (Review review : reviewsList.values()) {
                            View reviewLayout = LayoutInflater.from(getActivity()).inflate(R.layout.review_layout, null);
                            TextView titleReview = (TextView) reviewLayout.findViewById(R.id.author_review_label);
                            TextView contentReview = (TextView) reviewLayout.findViewById(R.id.content_review_label);

                            if (TextUtils.isEmpty(review.getAuthor())) {
                                titleReview.setText("Anonymous");
                            } else {
                                titleReview.setText(review.getAuthor());
                            }
                            contentReview.setText(review.getContent());
                            mReviewsLayout.addView(reviewLayout);
                        }
                    } else {
                        View reviewLayout = LayoutInflater.from(getActivity()).inflate(R.layout.review_layout, null);
                        TextView titleReview = (TextView) reviewLayout.findViewById(R.id.author_review_label);
                        TextView contentReview = (TextView) reviewLayout.findViewById(R.id.content_review_label);

                        titleReview.setVisibility(View.GONE);
                        contentReview.setText("No Reviews Found");
                        mReviewsLayout.addView(reviewLayout);
                    }
                }
            });
        }
        isReviewsLoaded = true;
    }

    public class TrailerFetcherTask extends AsyncTask<String, Void, Trailer[]> {

        private final String LOG_TAG = TrailerFetcherTask.class.getSimpleName();

        private Trailer[] getTrailersFromJson(String trailerJsonStr) throws JSONException {

            final String TRAILER_RESULTS = "results";
            final String TRAILER_ID = "id";
            final String TRAILER_NAME = "name";
            final String TRAILER_SITE = "site";
            final String TRAILER_TYPE = "type";
            final String TRAILER_KEY = "key";

            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_RESULTS);

            Trailer[] resultStrs = new Trailer[trailerArray.length()];

            for (int i = 0; i < trailerArray.length(); i++) {

                String id;
                String name;
                String site;
                String type;
                String key;

                JSONObject reviewJsonObject = trailerArray.getJSONObject(i);

                id = reviewJsonObject.getString(TRAILER_ID);
                name = reviewJsonObject.getString(TRAILER_NAME);
                site = reviewJsonObject.getString(TRAILER_SITE);
                type = reviewJsonObject.getString(TRAILER_TYPE);
                key = reviewJsonObject.getString(TRAILER_KEY);
                resultStrs[i] = new Trailer(id, name, site, type, key);
            }

            return resultStrs;
        }

        @Override
        protected void onPostExecute(Trailer[] trailers) {
            super.onPostExecute(trailers);

            if (trailersList == null) {
                trailersList = new LinkedHashMap<>();
            } else {
                trailersList.clear();
            }

            if(trailers!=null) {
                for (Trailer trailer : trailers) {
                    trailersList.put(trailer.getId(), trailer);
                }
            }

            updateTrailers();
        }

        @Override
        protected Trailer[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {
                Uri builtUri = Uri.parse(Constants.MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(Constants.MOVIE_PARAM)
                        .appendPath(params[0])
                        .appendPath(Constants.VIDEOS_PARAM)
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
                return getTrailersFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }

    private void updateTrailers() {
        mTrailersLayout.removeAllViews();
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTrailersProgressBar.setVisibility(View.GONE);
                    if (trailersList.size() > 0) {
                        for (int i=0; i<trailersList.values().size(); i++) {
                            final Trailer trailer = (Trailer) trailersList.values().toArray()[i];
                            if(i==0) {
                                mFirstTrailerUrl = Constants.YOUTUBE_PATH + trailer.getKey();
                                mShareButton.setVisibility(View.VISIBLE);
                            }
                            View trailerLayout = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_layout, null);
                            TextView trailerLabel = (TextView) trailerLayout.findViewById(R.id.trailer_label);

                            if (TextUtils.isEmpty(trailer.getName())) {
                                trailerLabel.setText("No Title");
                            } else {
                                trailerLabel.setText(trailer.getName());
                            }

                            if (trailer.getSite().equalsIgnoreCase("Youtube")) {
                                trailerLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        watchYoutubeVideo(trailer.getKey());
                                    }
                                });
                            }
                            mTrailersLayout.addView(trailerLayout);
                        }
                    } else {
                        View trailerLayout = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_layout, null);
                        TextView trailerLabel = (TextView) trailerLayout.findViewById(R.id.trailer_label);
                        trailerLabel.setText("No Trailers Found");
                        mTrailersLayout.addView(trailerLayout);
                    }
                }
            });
        }
        isTrailersLoaded = true;
    }

    public void watchYoutubeVideo(String id){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_PATH + id));
            startActivity(intent);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.LAST_LOADED_MOVIE_ID, movieId);
    }
}
