package com.example.moviebookingalarm.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.moviebookingalarm.db.entity.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM MOVIE ORDER BY ID DESC LIMIT 1")
    suspend fun getMovie(): Movie?

    @Insert
    suspend fun insertMovie(movie: Movie)

    @Query("DELETE FROM MOVIE")
    suspend fun deleteAllMovies()
}