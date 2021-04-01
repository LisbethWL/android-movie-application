package com.lwl.movie;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import java.util.ArrayList;
import okhttp3.OkHttpClient;



public class Overview extends AppCompatActivity {

    public static final String EDIT_KEY = "editkey";
    public static final String DETAIL_KEY = "detailkey";

    MovieService mService;
    boolean mBound = false;
    ArrayList<Movie> movies = new ArrayList<>();
    CustomListAdapter adapter;

    //************ widgets ************
    Button Exit;
    Button Add;
    ListView listView;
    EditText AddTitle;
    //*********************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        adapter = new CustomListAdapter(this, R.layout.list_item_movie, movies);

        listView = (ListView) findViewById(R.id.listviewID);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Overview.this, Details.class);
                intent.putExtra(DETAIL_KEY, movies.get(position).getTitle());
                intent.putExtra("image", movies.get(position).getGenreIcon());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Overview.this, Edit.class);
                intent.putExtra(EDIT_KEY, movies.get(position).getTitle());
                startActivity(intent);
                return true;
            }
        });


        Exit = findViewById(R.id.btn_exit);

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Add = findViewById(R.id.btn_add);
        AddTitle = findViewById(R.id.edt_title);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mBound){
                    mService.addMovie(AddTitle.getText().toString());
                    AddTitle.setText("");

                    // Hide keyboard when pushing Add button
                    //https://stackoverflow.com/questions/3400028/close-virtual-keyboard-on-button-press?fbclid=IwAR3VqYpA2YPZcCjfH_5NfcSjh1m2dOeYSRXCNkK09Pkj_MYGoA-xnevL2ew
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });




        //Facebook Stetho setup: viser databasen
        Stetho.initializeWithDefaults(this);
        OkHttpClient client =
                new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
    }


    //************ Binding ************
        @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MovieService.class);
        startService(intent); // Start service - before binding
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        mBound = true;

        //Broadcast results
        IntentFilter filter = new IntentFilter();
        filter.addAction("updated");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MovieService.MovieLocalBinder binder = (MovieService.MovieLocalBinder) service;
            mService = binder.getService();
            mBound = true;

            // get movies fra MovieService
            movies.clear();
            adapter.clear();
            movies = mService.getAllMovies();
            adapter.addAll(movies);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    // Updating UI - inner class
    // https://www.youtube.com/watch?v=qNocH6Angt0
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            movies.clear();
            adapter.clear();
            movies = mService.getAllMovies();
            adapter.addAll(movies);

        }
    };

}
