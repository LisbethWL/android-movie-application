package com.lwl.movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter {

    static class placeHolder{
        TextView title;
        TextView isWatched;
        TextView imdb;
        TextView rating;
        ImageView icons;
    }

    private Context mContext;
    private ArrayList mList;

    public CustomListAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);

        mContext = context;
        mList = objects;
    }

    // automatisk hent data til hver række
    public View getView(int position, View view, ViewGroup parent) {

        Movie movie = (Movie) mList.get(position);

        placeHolder holder = new placeHolder();


        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_item_movie, parent, false); // instantierer layout XML fil til det korresponerende view, returnerer view

            // referencer til list_item_movie
            holder.title = view.findViewById(R.id.txt_title);
            holder.isWatched = view.findViewById(R.id.txt_watched);
            holder.imdb = view.findViewById(R.id.txt_imdb);
            holder.rating = view.findViewById(R.id.txt_rating);
            holder.icons = view.findViewById(R.id.img_icon);

            view.setTag(holder);
        } else{
            holder = (placeHolder) view.getTag();
        }

        // sætter objekternes værdi til csv filen
        holder.title.setText(movie.getTitle());
        holder.imdb.setText("IMDB: " + movie.getImdbRating());
        holder.rating.setText("RATE: " + String.valueOf(movie.getMyRating()));

        String allGenres = movie.getGenre();
        String[] splitGenres = allGenres.split(",");
        String primaryGenre = splitGenres[0];

        switch (primaryGenre){
            case "Action":
                movie.setGenreIcon(R.drawable.action);
                break;
            case "Biography":
                movie.setGenreIcon(R.drawable.biography);
                break;
            case "Drama":
                movie.setGenreIcon(R.drawable.drama);
                break;
            case "Animation":
                movie.setGenreIcon(R.drawable.animation);
                break;
            case "Adventure":
                movie.setGenreIcon(R.drawable.adventure);
                break;
            case "Romance":
                movie.setGenreIcon(R.drawable.rom);
                break;
            case "Comedy":
                movie.setGenreIcon(R.drawable.comedy);
                break;
            case "Sci-Fi":
                movie.setGenreIcon(R.drawable.shifi);
                break;
            case "Thriller":
                movie.setGenreIcon(R.drawable.thriller);
                break;

            default:
                movie.setGenreIcon(R.drawable.movieapp);
                break;
        }

        holder.icons.setImageResource(movie.getGenreIcon());

        if(movie.getMovieWatched() == true){
            holder.isWatched.setText(R.string.isWatched);
        } else{
            holder.isWatched.setText(R.string.notWatched);
        }


        return view;
    }

}
