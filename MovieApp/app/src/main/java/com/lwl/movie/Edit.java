package com.lwl.movie;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class Edit extends AppCompatActivity {


    public static final String STATE_MOVIE = "state_movie";

    Movie e_m = new Movie();
    String movietitle;
    MovieService mService;
    boolean mBound = false;

    Button cancel, ok;
    TextView movieTitle, value;
    EditText userComment;
    SeekBar seek;
    CheckBox setWatchStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        cancel = findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        movietitle = getIntent().getStringExtra(Overview.EDIT_KEY);

        ok = findViewById(R.id.btn_ok2);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                e_m.setUserComment(userComment.getText().toString());


                if(setWatchStatus.isChecked()){
                    e_m.setMovieWatched(true);
                }else{
                    e_m.setMovieWatched(false);
                }

                mService.updateMovie(e_m);

                finish();

            }
        });

        setupEditResources();

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //double r = progress/10.0;
                //value.setText(String.valueOf(r));
                //e_m.setMyRating(r);
                value.setText(String.valueOf((double)(progress/10.0)));
                e_m.setMyRating((double)(progress/10.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void setupEditResources(){
        movieTitle = findViewById(R.id.txt_title_edit);
        value = findViewById(R.id.txt_value);
        userComment = findViewById(R.id.editText);
        seek = findViewById(R.id.seekBar);
        setWatchStatus = findViewById(R.id.cb_edit);
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
            e_m = mService.getMovie(movietitle);
            setUI();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void setUI(){
        // Saving user values
        movieTitle.setText(e_m.getTitle());
        userComment.setText(e_m.getUserComment());
        setWatchStatus.setChecked(e_m.getMovieWatched());

        if(e_m.getMyRating() != null){
            seek.setMax(100);
            Double bar = e_m.getMyRating()*10;
            seek.setProgress(bar.intValue());
        }

        String myRating = String.valueOf(e_m.getMyRating());
        value.setText(myRating);
    }
}
