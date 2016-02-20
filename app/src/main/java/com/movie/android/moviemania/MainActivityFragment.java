package com.movie.android.moviemania;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] data = {
                "Cupcake (Android Version -1.5",
                "Cupcake (Android Version -1.5",
                "Cupcake (Android Version -1.5"
        };

        List<Movie> movies = new ArrayList<Movie>();
        new FetchMovieTask().execute();
        movieAdapter = new MovieAdapter(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(movieAdapter);

        return rootView;
    }

    class FetchMovieTask extends AsyncTask<Void,Void,Movie[]>
    {
        Movie[] movieObject = null;

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
        protected Movie[] doInBackground(Void... strings) {


            String api_key=BuildConfig.MOVIE_DB_API_KEY;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;
            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, "popularity.desc")
                        .appendQueryParameter(API_KEY, api_key)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

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
                // Log.v("MY JSON OUTPUT", forecastJSONStr);
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
                            jsonObject.getString("backdrop_path"));


                    Log.v("POSTER PATH", jsonObject.getString("backdrop_path"));
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
    }
}