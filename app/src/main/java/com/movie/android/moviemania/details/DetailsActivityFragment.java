package com.movie.android.moviemania.details;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.movie.android.moviemania.MainActivityFragment;
import com.movie.android.moviemania.Movie;
import com.movie.android.moviemania.MovieAdapter;
import com.movie.android.moviemania.R;
import com.movie.android.moviemania.asynctask.FetchMoviesDetails;
import com.movie.android.moviemania.moviedbapi.MovieContract;
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

    private String[] movieArray;
    Movie movie ;
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
            movieArray = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
            movie = new Movie(movieArray[0],movieArray[2],movieArray[3],movieArray[1],movieArray[3],movieArray[0],movieArray[5]);

            displayDetails();
            getTrailers(inflater);
            getReviews(inflater);
        }

        return rootView;
    }

    private void displayDetails(){

        toggleFavorites();
        ((TextView) rootView.findViewById(R.id.movieDetailTitle))
                .setText(movieArray[0]);

        ((TextView) rootView.findViewById(R.id.movieDetailYear))
                .setText(movieArray[1]);

        ((TextView) rootView.findViewById(R.id.movieSynopsis))
                .setText(movieArray[2]);

        ImageView iconView = (ImageView) rootView.findViewById(R.id.moviePosterImageView);
        Picasso.with(getContext())
                .load(baseImageUrl + movieArray[3])
                .into(iconView);

        ((TextView) rootView.findViewById(R.id.movieDetailUserRating))
                .setText(String.format("%s / 10", movieArray[4]));

        ImageButton favorites = (ImageButton) rootView.findViewById(R.id.add_to_fav_view);

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean inFavorites = checkFavorites();
                if (inFavorites) {
                    deleteFromFavorites();
                } else {
                    addToFavorites();
                }
                toggleFavorites();
            }
        });

        FetchMoviesDetails movieReviews = new FetchMoviesDetails(movie,"reviews");
        FetchMoviesDetails fetchTrailer = new FetchMoviesDetails(movie,
                "trailers");

        try {
            fetchTrailer.execute().get();
            movieReviews.execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getTrailers(LayoutInflater layoutInflater){

        final String ARRAY_OF_TRAILERS = "youtube";
        final String TRAILER_ID = "source";
        final String TRAILER_TITLE = "name";

        try{

            System.out.println("Get Trailers"+movie.getTrailer());
            JSONObject trailersJson = new JSONObject(movie.getTrailer());
            JSONArray trailersArray = trailersJson.getJSONArray(ARRAY_OF_TRAILERS);


            if(trailersArray.length() > 0) {

                int trailersLength =  trailersArray.length();

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

    /*
* add movie into favorites in the DB
* */
    private void addToFavorites() {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.clear();

        values.put(MovieContract.MovieEntry.MOVIE_ID, movieArray[5]);
        values.put(MovieContract.MovieEntry.MOVIE_BACKDROP_URI, movieArray[0] );
        values.put(MovieContract.MovieEntry.MOVIE_TITLE, movieArray[0]);
        values.put(MovieContract.MovieEntry.MOVIE_POSTER, movieArray[3]);
        values.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, movieArray[2]);
        values.put(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE, movieArray[3]);
        values.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, movieArray[1]);
        values.put(MovieContract.MovieEntry.MOVIE_REVIEWS, movie.getReview());
        values.put(MovieContract.MovieEntry.MOVIE_TRAILERS, movie.getTrailer());

        Uri check = resolver.insert(uri, values);
    }


    /*
    * delete movie from the favorites in the DB
    * */
    private void deleteFromFavorites() {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();

        long noDeleted = resolver.delete(uri,
                MovieContract.MovieEntry.MOVIE_ID + " = ? ",
                new String[]{ movieArray[5] + "" });

    }


    /*
    * query DB to see if the movie is already there.
    * */
    private boolean checkFavorites() {

        Uri uri = MovieContract.MovieEntry.buildMovieUri(Long.parseLong(movieArray[5]));
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = null;

        try {

            cursor = resolver.query(uri, null, null, null, null);
        
            if (cursor.moveToFirst()) {
                return true;
            }
        } finally {

            if(cursor != null)
                cursor.close();

        }

        return false;
    }


    /*
    * toggles active state for the favorited star based on if it is in the database
    * */
    private void toggleFavorites(){
        boolean inFavorites = checkFavorites();
        ImageButton addToFav = (ImageButton) rootView.findViewById(R.id.add_to_fav_view);

        if(inFavorites){
            addToFav.setImageResource(R.drawable.add_favorite);
        }else{
            addToFav.setImageResource(R.drawable.remove_favorite);
        }
    }

    private void getReviews(LayoutInflater layoutInflater){

        if(movie.getReview() == null)
            return;

        final String ARRAY_OF_REVIEW = "results";
        final String AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        try {
            JSONObject reviewsJson = new JSONObject(movie.getReview());
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
