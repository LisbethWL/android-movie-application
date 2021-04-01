package com.lwl.movie;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

// tjek db her: chrome://inspect/#devices, vælg inspect - recources
// https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9?fbclid=IwAR0fCFLNtiSgKCSuY_ZqqlBYsxSvNVcvxdlL1goSwYjg1mZLxInflCUBFXM
@Database(entities = {Movie.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract MovieDao movieDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "movie-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
