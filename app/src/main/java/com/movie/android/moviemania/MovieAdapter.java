package com.movie.android.moviemania;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by raanand on 2/21/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String log_tag = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context,List<Movie> movieList) {

        super(context, 0,movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);
        final String baseImageUrl="http://image.tmdb.org/t/p/w185/";

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie,parent,false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load(baseImageUrl + movie.posterName).into(iconView);

        return convertView;
    }
}
