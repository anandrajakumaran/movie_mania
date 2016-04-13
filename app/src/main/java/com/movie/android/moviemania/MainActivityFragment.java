package com.movie.android.moviemania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.movie.android.moviemania.asynctask.FetchMoviesDetails;
import com.movie.android.moviemania.details.DetailsActivity;
import com.movie.android.moviemania.moviedbapi.SortCriteria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class    MainActivityFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private final static String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private MovieAdapter movieAdapter;

    private ArrayList<Movie> movieList;
    private FetchMovieTask fetchMovieTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext())
                .registerOnSharedPreferenceChangeListener(this);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movieList = new ArrayList<Movie>();
            fetchMovies();
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //new FetchMovieTask().execute();
        movieAdapter = new MovieAdapter(getActivity(),movieList);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = movieAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailsActivity.class).putExtra(Intent.EXTRA_TEXT, new String[]{movie.originalTitle,
                        movie.releaseDate, movie.overview, movie.posterName, movie.voteAverage,movie.id,movie.trailer,movie.review});

                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (isAdded() && getString(R.string.pref_sortorder_key).equals(key)) {
            fetchMovies();
        }
    }

    private void fetchMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        fetchMovieTask = new FetchMovieTask();

        final String orderByMostPopular = getString(R.string.pref_sortorder_mostPopular);
        final String orderByHigestRated = getString(R.string.pref_sortorder_highestRated);
        String sortOrderSetting = prefs.getString(getString(R.string.pref_sortorder_key),
                orderByMostPopular);

        SortCriteria sortCriteria;
        if (orderByMostPopular.equals(sortOrderSetting))
            sortCriteria = SortCriteria.MOST_POPULAR;
        else if (orderByHigestRated.equals(sortOrderSetting))
            sortCriteria = SortCriteria.HIGEST_RATED;
        else {
            Log.w(LOG_TAG,
                    String.format("Default Sort Order", sortOrderSetting));
            sortCriteria = SortCriteria.MOST_POPULAR;
        }

        fetchMovieTask.execute(sortCriteria);
    }

    class FetchMovieTask extends AsyncTask<SortCriteria,Void,Movie[]>
    {
        Movie[] movieObject;

        @Override
        protected void onPostExecute(Movie[] movies) {
            if(movies!=null)
            {
                movieAdapter.clear();
                for(Movie m : movies)
                {
                    movieAdapter.add(m);
                }
            }
        }

        @Override
        protected Movie[] doInBackground(SortCriteria... strings) {

            SortCriteria sortCriteria = null;

            if(strings.length == 1){
                sortCriteria = strings[0];
            }else{
                sortCriteria = SortCriteria.MOST_POPULAR;
            }

            Log.v(LOG_TAG, "Sort Order" + sortCriteria);

            String api_key=BuildConfig.MOVIE_DB_API_KEY;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;
            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY,sortCriteria.getSortOption())
                        .appendQueryParameter(API_KEY, api_key)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                Log.v("Movie API URL", builtUri.toString());

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    movieJSONStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    movieJSONStr = null;
                }
                movieJSONStr = buffer.toString();

                String title="title";
                String vote_average="vote_avergae";
                String overview="overview";
                JSONObject movieJSONObject=new JSONObject(movieJSONStr);
                JSONArray movieJSONArray=movieJSONObject.optJSONArray("results");

                movieObject=new Movie[movieJSONArray.length()];
                for(int i=0;i<movieJSONArray.length();i++)
                {

                    JSONObject jsonObject=movieJSONArray.getJSONObject(i);
                    movieObject[i]= new Movie(jsonObject.getString("original_title"),
                            jsonObject.getString("overview"),
                            jsonObject.getString("vote_average"),
                            jsonObject.getString("release_date"),
                            jsonObject.getString("poster_path"),
                            jsonObject.getString("backdrop_path"),
                            jsonObject.getString("id"));


                    Log.v("POSTER PATH", jsonObject.getString("backdrop_path"));
                    executeMovieDetails(movieObject[i], "reviews");
                    System.out.println("review-->"+movieObject[i].getReview());

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movieObject;

        }

        private void executeMovieDetails(Movie movie,String requiredDetail){
            FetchMoviesDetails movieReviews = new FetchMoviesDetails(movie,"reviews");
            movieReviews.execute();
            FetchMoviesDetails fetchTrailer = new FetchMoviesDetails(movie,
                    "videos");
            fetchTrailer.execute();
            System.out.println("review11-->" + movie.getReview());
        }
    }


}
