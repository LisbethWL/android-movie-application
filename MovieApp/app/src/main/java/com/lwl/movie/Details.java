package com.lwl.movie;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class Details extends AppCompatActivity {

    String movietitle;
    Integer icon;
    Drawable drawable;
    Movie d_m = new Movie();

    MovieService mService;
    boolean mBound = false;

    Button ok;
    Button delete;

    ImageView image;
    TextView title, imdbRating, userRating, plot, comment, genre;
    CheckBox watchStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ok = findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupResource();

        movietitle = getIntent().getStringExtra(Overview.DETAIL_KEY);
        icon = getIntent().getIntExtra("image", R.drawable.movieapp);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound){
                    mService.removeMovie(d_m.getTitle());
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MovieService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MovieService.MovieLocalBinder binder = (MovieService.MovieLocalBinder) service;
            mService = binder.getService();
            mBound = true;

            d_m = mService.getMovie(movietitle);

            setUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void setupResource(){
        title = findViewById(R.id.txt_title);
        imdbRating = findViewById(R.id.txt_imdb);
        userRating = findViewById(R.id.txt_rating);
        plot = findViewById(R.id.txt_plot);
        comment = findViewById(R.id.txt_comment);
        genre = findViewById(R.id.txt_genre);
        delete = findViewById(R.id.btn_del);

        image = findViewById(R.id.imageViewDetails);
        watchStatus = findViewById(R.id.cb_details);
    }

    public void setUI(){

        title.setText(d_m.getTitle());
        imdbRating.setText(d_m.getImdbRating());
        userRating.setText(d_m.getMyRating().toString());
        plot.setText(d_m.getPlot());
        comment.setText(d_m.getUserComment());
        genre.setText(d_m.getGenre());

        drawable = getResources().getDrawable(icon);
        image.setImageDrawable(drawable);
        //image.setImageResource(d_m.getGenreIcon());
        watchStatus.setChecked(d_m.getMovieWatched());

    }
}
