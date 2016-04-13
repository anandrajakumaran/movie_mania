package com.movie.android.moviemania.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.movie.android.moviemania.MainActivityFragment;
import com.movie.android.moviemania.Movie;
import com.movie.android.moviemania.MovieAdapter;
import com.movie.android.moviemania.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    private final static String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private String[] movie;
    private MovieAdapter movieAdapter;
    private View rootView;
    private String trailerId;
    private ArrayList<Movie> movieList;
    final String baseImageUrl="http://image.tmdb.org/t/p/w185/";
    public DetailsActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        movieAdapter = new MovieAdapter(getActivity(),movieList);
        ArrayList<Movie> movieList = new ArrayList<Movie>();

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movie = intent.getStringArrayExtra(Intent.EXTRA_TEXT);

            Log.v("Image", baseImageUrl + movie[4]);
            displayDetails();
            getTrailers(inflater);
            getReviews(inflater);
        }

        return rootView;
    }

    private void displayDetails(){

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

        System.out.println("Trailer"+movie[6]);
    }

    private void getTrailers(LayoutInflater layoutInflater){

        final String ARRAY_OF_TRAILERS = "results";
        final String TRAILER_ID = "key";
        final String TRAILER_TITLE = "name";

        try{

            JSONObject trailersJson = new JSONObject(movie[6]);
            JSONArray trailersArray = trailersJson.getJSONArray(ARRAY_OF_TRAILERS);
            int trailersLength =  trailersArray.length();

            if(trailersLength > 0) {

                LinearLayout innerScrollLayout = (LinearLayout)
                        rootView.findViewById(R.id.inner_scroll_layout);

                View trailersListView = layoutInflater.inflate(R.layout.trailers_list,
                        innerScrollLayout, false);

                innerScrollLayout.addView(trailersListView);

                LinearLayout trailerList = (LinearLayout)
                        trailersListView.findViewById(R.id.trailers_list);

                for (int i = 0; i < trailersLength; ++i) {

                    View trailerItem = layoutInflater.inflate(R.layout.trailer_item,
                            trailerList, false);

                    JSONObject trailer = trailersArray.getJSONObject(i);
                    final String trailerId = trailer.getString(TRAILER_ID);
                    String trailerTitle = trailer.getString(TRAILER_TITLE);
                    TextView videoTitle = (TextView) trailerItem.findViewById(R.id.video_title);

                    this.trailerId = trailerId;
                    videoTitle.setText(trailerTitle);
                    trailerList.addView(trailerItem);

                    trailerItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent ytIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("vnd.youtube:" + trailerId));
                            ytIntent.putExtra("VIDEO_ID", trailerId);
                            try{
                                startActivity(ytIntent);
                            }catch (ActivityNotFoundException ex){
                                Log.i(LOG_TAG, "youtube app not installed");
                            }
                        }
                    });
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, "ERROR PARSING TRAILER JSON");
        }
    }


    private Intent runTrailerIntent(){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Opening Trailer");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://youtu.be/" + trailerId);
        return intent;

    }

    private void getReviews(LayoutInflater layoutInflater){

        if(movie[7] == null)
            return;

        final String ARRAY_OF_REVIEW = "results";
        final String AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        try {
            JSONObject reviewsJson = new JSONObject(movie[7]);
            JSONArray reviewsArray = reviewsJson.getJSONArray(ARRAY_OF_REVIEW);
            int reviewsLength = reviewsArray.length();

            if (reviewsLength > 0){

                // append the review folder
                LinearLayout innerScrollLayout = (LinearLayout)
                        rootView.findViewById(R.id.inner_scroll_layout);

                View reviewsListView = layoutInflater.inflate(R.layout.review_list,
                        innerScrollLayout, false);

                innerScrollLayout.addView(reviewsListView);

                LinearLayout reviewList = (LinearLayout)
                        reviewsListView.findViewById(R.id.review_list);

                for (int i = 0; i < reviewsLength; ++i) {

                    View reviewItem = layoutInflater.inflate(R.layout.review_item,
                            reviewList, false);

                    JSONObject review = reviewsArray.getJSONObject(i);
                    String reviewAuthor = review.getString(AUTHOR);
                    String reviewContent = review.getString(REVIEW_CONTENT);

                    TextView author = (TextView) reviewItem.findViewById(R.id.review_author);
                    TextView content = (TextView) reviewItem.findViewById(R.id.review_content);

                    author.setText(reviewAuthor);
                    content.setText(reviewContent);

                    reviewList.addView(reviewItem);
                }
            }

        }catch (JSONException e){
            Log.e(LOG_TAG, "ERROR PARSING REVIEW JSON");
        }
    }
}
