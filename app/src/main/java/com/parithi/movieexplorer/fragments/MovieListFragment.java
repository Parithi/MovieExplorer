package com.parithi.movieexplorer.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parithi.movieexplorer.Constants;
import com.parithi.movieexplorer.activities.DetailActivity;
import com.parithi.movieexplorer.MovieExplorerApplication;
import com.parithi.movieexplorer.R;
import com.parithi.movieexplorer.activities.MainActivity;
import com.parithi.movieexplorer.data.MovieContract;
import com.parithi.movieexplorer.managers.MovieManager;
import com.parithi.movieexplorer.models.Movie;

public class MovieListFragment extends Fragment implements MovieManager.MovieManagerDelegate, LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    String[] projection = { MovieContract.MovieEntry._ID,
                            MovieContract.MovieEntry.COLUMN_TITLE,
                            MovieContract.MovieEntry.COLUMN_USER_RATING,
                            MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_URL};

    private GridView mMovieGridView;
    private MovieListAdapter mMovieListAdapter;
    private String mSortMode = MovieExplorerApplication.getStoredSortMode();

    public MovieListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.sort_by_menuitem) {
            AlertDialog.Builder sortDialog = new AlertDialog.Builder(getActivity());
            sortDialog.setTitle(R.string.sort_by_label);
            int selected = 0;
            switch (mSortMode) {
                case Constants.MOST_POPULAR_PARAM:
                    selected = 0;
                    break;
                case Constants.HIGHEST_RATED_PARAM:
                    selected = 1;
                    break;
                case Constants.FAVORITES:
                    selected = 2;
                    break;
            }
            String[] types = {getString(R.string.most_popular_label), getString(R.string.highest_rated_label), getString(R.string.favorites_label)};

            sortDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(!MovieExplorerApplication.getStoredSortMode().equals(Constants.FAVORITES)) {
                        MovieManager.getInstance().fetchMoviesFromURL();
                    }
                    getLoaderManager().restartLoader(MOVIE_LOADER, null, MovieListFragment.this);
                }
            });
            sortDialog.setSingleChoiceItems(types, selected, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            mSortMode = Constants.MOST_POPULAR_PARAM;
                            MovieExplorerApplication.setStoredSortMode(Constants.MOST_POPULAR_PARAM);
                            break;
                        case 1:
                            mSortMode = Constants.HIGHEST_RATED_PARAM;
                            MovieExplorerApplication.setStoredSortMode(Constants.HIGHEST_RATED_PARAM);
                            break;
                        case 2:
                            mSortMode = Constants.FAVORITES;
                            MovieExplorerApplication.setStoredSortMode(Constants.FAVORITES);
                            break;
                    }
                    dialog.dismiss();
                }

            });

            sortDialog.show();
        }
        return true;
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
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void notifyMoviesLoaded() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(MovieExplorerApplication.getStoredSortMode().equals(Constants.MOST_POPULAR_PARAM)) {
            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC");
        } else if (MovieExplorerApplication.getStoredSortMode().equals(Constants.HIGHEST_RATED_PARAM)) {
            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    MovieContract.MovieEntry.COLUMN_USER_RATING + " DESC");
        } else {
            Log.d("MovieListFragment---","Checking favorites");
            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    projection,
                    MovieContract.MovieEntry.COLUMN_IS_FAVORITE + "=?",
                    new String[]{"1"},
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieListAdapter = new MovieListAdapter(getActivity(),0,data,new String[]{},new int[]{},0);
        mMovieGridView.setAdapter(mMovieListAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(mMovieListAdapter!=null) {
            mMovieListAdapter.swapCursor(null);
        }
    }

    private class MovieListAdapter extends SimpleCursorAdapter {

        public MovieListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        public void reloadList(Cursor cursor) {
            cursor.moveToFirst();
            swapCursor(cursor);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return (getCursor() == null) ? 0 : getCursor().getCount();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MovieViewHolder movieViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.movie_poster_layout, parent, false);
                movieViewHolder = new MovieViewHolder();
                movieViewHolder.movieTitleTextView = (TextView) convertView.findViewById(R.id.movie_title_textview);
                movieViewHolder.moviePosterImageView = (ImageView) convertView.findViewById(R.id.movie_poster_imageview);
                movieViewHolder.movieRatingTextView = (TextView) convertView.findViewById(R.id.movie_rating_textview);
                convertView.setTag(movieViewHolder);
            } else {
                movieViewHolder = (MovieViewHolder) convertView.getTag();
            }

            getCursor().moveToPosition(position);
            movieViewHolder.movieTitleTextView.setText(getCursor().getString(getCursor().getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movieViewHolder.movieRatingTextView.setText(getCursor().getString(getCursor().getColumnIndex(MovieContract.MovieEntry.COLUMN_USER_RATING)));
            Glide.with(MovieListFragment.this).load(getCursor().getString(getCursor().getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_URL))).centerCrop().crossFade().into(movieViewHolder.moviePosterImageView);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((MainActivity) getActivity()).isTablet()) {
                        getCursor().moveToPosition(position);
                        ((MainActivity) getActivity()).loadMovieToDetail((getCursor().getLong(getCursor().getColumnIndex(MovieContract.MovieEntry._ID))));
                    } else {
                        getCursor().moveToPosition(position);
                        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                        detailIntent.putExtra(Intent.EXTRA_TEXT, (getCursor().getLong(getCursor().getColumnIndex(MovieContract.MovieEntry._ID))));
                        startActivity(detailIntent);
                    }
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
