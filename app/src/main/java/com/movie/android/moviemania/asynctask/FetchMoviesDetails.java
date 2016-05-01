package com.movie.android.moviemania.asynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.movie.android.moviemania.BuildConfig;
import com.movie.android.moviemania.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
*
* Async class for fetching a movies reviews and trailers
**
* */
public class FetchMoviesDetails extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchMoviesDetails.class.getSimpleName();
    private final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private Movie movie;
    private String requiredData;

    public FetchMoviesDetails(Movie movie, String requiredData){
        this.movie = movie;
        this.requiredData = requiredData;
    }

    @Override
    protected void onPostExecute(String result) {

        System.out.print("Movie Details"+result);
        if(requiredData.equals("reviews")){

            movie.setReview(result);
        }else {
            movie.setTrailer(result);
        }

    }

    @Override
    protected String doInBackground(String... params) {

        if (params.length != 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {

            Uri builtUri = Uri.parse(BASE_URL + movie.getId() + "/" + requiredData).buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
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

            if(requiredData.equals("reviews")){

                movie.setReview(buffer.toString());
            }else {
                movie.setTrailer(buffer.toString());
            }

            return buffer.toString();

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

    }



}
