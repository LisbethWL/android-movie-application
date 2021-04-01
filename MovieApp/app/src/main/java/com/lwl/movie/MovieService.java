package com.lwl.movie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Binder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class MovieService extends Service {

    private RequestQueue mQueue;
    AppDatabase db;
    Movie m;
    private boolean run = false;

    private final IBinder movieBinder = new MovieLocalBinder();


    public MovieService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("SERVICE","onStartCommand");
        Toast.makeText(this, "Service onCommand", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        readMovieData();

        int ID = 1; // Must be different from ID in MakeNotification

        // Notification Channel for højere end version Oreo...
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

         Notification m_notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notify)
                 .setChannelId(CHANNEL_ID)
                 .build();

                startForeground(ID, m_notification);

        if(!run){
            MakeNotification();
            new MovieTask().execute();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return movieBinder;
    }

    public class MovieLocalBinder extends Binder{
        MovieService getService(){
            // reference til MovieService klassen
            return MovieService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // indlæs CSV filen
    private void readMovieData(){
        //MovieSample sample;

        InputStream input = getResources().openRawResource(R.raw.movielist);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));

        String line = "";

        try {

            // undlad headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                //Split ved ;
                String[] tokens = line.split(";");

                // læs data
                Movie sample = new Movie();
                sample.setTitle(tokens[0]);
                sample.setPlot(tokens[1]);
                sample.setGenre(tokens[2]);
                sample.setImdbRating(tokens[3]);

                db = AppDatabase.getAppDatabase(getApplicationContext());
                db.movieDao().insert(sample);

                Log.d("MyActivity", "Netop oprettet: " + sample);
            }
        } catch (IOException e){
            Log.wtf("MyActivity", "Datafilen bliver ikke læst" + line, e);
            e.printStackTrace();
        }
    }


    // Bruges af Add - HER HENTER JEG FILMEN FRA API'ET
    public String createUrl(String title){
        String tab = title.replace(" ", "+");
        return "http://www.omdbapi.com/?apikey=2e71adcf&t=" + tab;

    }

    // Metoder til aktiviteter
    // https://www.youtube.com/watch?v=y2xtLqP8dSQ
    public void addMovie(String title){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, createUrl(title), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Movie m = new Movie();
                            m.setTitle(response.getString("Title"));
                            m.setGenre(response.getString("Genre"));
                            m.setPlot(response.getString("Plot"));
                            m.setImdbRating(response.getString("imdbRating"));

                            db = AppDatabase.getAppDatabase(getApplicationContext());
                            db.movieDao().insert(m);
                            broadcast("add_movie");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });

        // instatierer request queue - kan også gøres med singleton, se:
        //https://developer.android.com/training/volley/requestqueue
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.add(request);


    }

    public void removeMovie(String title){

        Movie m = getMovie(title);
        db.movieDao().delete(m);
        broadcast("remove_movie");

    }

    public void updateMovie(Movie movie){
        db.movieDao().update(movie);
        broadcast("update_movie");
    }

    public Movie getMovie(String title){
        return db.movieDao().findByTitle(title);
    }

    public ArrayList<Movie> getAllMovies(){
        return new ArrayList<>( db.movieDao().getAll());
    }


    // NOTIFIKATIONER HVERT ANDET MINUT
    // https://www.youtube.com/watch?v=sVdpslsB9qQ
    //https://stackoverflow.com/questions/6397754/android-implementing-startforeground-for-a-service
    // Notification variable
    private static String CHANNEL_ID = "test_notification";
    private static String CHANNEL_NAME = "Movie Notifications";
    private static String CHANNEL_DESC = " testing rand notifications";
    private static Integer ID = 101;


    //public void MakeNotification()
    //public Notification MakeNotification
    public void MakeNotification(){

        run = true;
        db = AppDatabase.getAppDatabase(getApplicationContext()); //MANGLEDE...
        ArrayList<Movie> unwatched = new ArrayList<>(db.movieDao().getUnwatchedMovies(0));

        // Fetch random movie title
        int randMovie = new Random().nextInt(unwatched.size()); //HAVDE IKKE +1
        m = unwatched.get(randMovie);
        String notification = getString(R.string.Notify_head) + ": " + m.getTitle() +" ..." + getString(R.string.Notify_end);

        Notification m_notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle(getString(R.string.Notify_title))
                .setContentText(notification)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification))
                .setChannelId(CHANNEL_ID) // Kan bruge det samme CHANNEL_ID som i onCreate
                .build();

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(ID, m_notification); // er build ovenfor

        // startForeground sker i onCreate

    }

    // https://www.youtube.com/watch?v=u4828hciA-I
    private class MovieTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            while(run)
            {
                try {
                    MakeNotification();
                    Thread.sleep(1000*60*2);
                    //Thread.sleep(1000*10); // for testing
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    // https://gist.github.com/Antarix/8131277?fbclid=IwAR1iSm8V3irAxlc-rcFlUggN5gGRYQpG3Ur4v0UeeOvrnXIPv1fk2SS48iA
    public void broadcast(String bc){
        Intent i = new Intent();
        i.setAction("updated");
        // LocalBroadcastManager: no security issues, ie only within this app
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}