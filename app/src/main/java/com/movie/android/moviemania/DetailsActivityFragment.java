package com.movie.android.moviemania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    private final static String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private String[] movie;
    private MovieAdapter movieAdapter;

    private ArrayList<Movie> movieList;
    final String baseImageUrl="http://image.tmdb.org/t/p/w185/";
    public DetailsActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //new MainActivityFragment().new FetchMovieTask().execute();
        movieAdapter = new MovieAdapter(getActivity(),movieList);
        ArrayList<Movie> movieList = new ArrayList<Movie>();

        //View rootView = inflater.inflate(R.layout.fragment_details, container, false);

//        ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.movieDetailScrollView);
//        movieDetailScrollView.setAdapter(movieAdapter);

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movie = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
//            ((TextView) rootView.findViewById(R.id.movieDetailTitle))
//                    .setText(title);
            Log.v("Image", baseImageUrl + movie[4]);

            ((TextView) rootView.findViewById(R.id.movieDetailTitle))
                    .setText(movie[0]);

            ((TextView) rootView.findViewById(R.id.movieDetailYear))
                    .setText(movie[1]);

            ((TextView) rootView.findViewById(R.id.movieSynopsis))
                    .setText(movie[2]);

            ImageView iconView = (ImageView) rootView.findViewById(R.id.moviePosterImageView);
            Picasso.with(getContext())
                    .load(baseImageUrl + movie[3])
                    .into(iconView);

            ((TextView) rootView.findViewById(R.id.movieDetailUserRating))
                    .setText(String.format("%s / 10", movie[4]));

           // Picasso.with(getContext()).load(baseImageUrl + title).into(iconView);
        }

        return rootView;
    }
}
