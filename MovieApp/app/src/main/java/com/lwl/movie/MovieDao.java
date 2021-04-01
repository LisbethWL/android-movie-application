package com.lwl.movie;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface  MovieDao {

    @Query("SELECT * FROM movie")
    List<Movie> getAll();

    @Query("SELECT * FROM movie where title LIKE  :title ")
    Movie findByTitle(String title);

    @Query("SELECT * FROM movie where isWatched LIKE :status ")
    List<Movie> getUnwatchedMovies(Integer status);

    @Query("SELECT COUNT(*) from movie")
    int countUsers();

    @Insert(onConflict = OnConflictStrategy.IGNORE) // ved samme film
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

    @Update
    void update(Movie movie);
}
