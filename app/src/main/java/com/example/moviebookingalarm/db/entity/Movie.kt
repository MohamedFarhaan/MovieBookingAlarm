package com.example.moviebookingalarm.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Movie")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int,

    @ColumnInfo(name = "MOVIE_NAME")
    val movieName: String?,

    @ColumnInfo(name = "LOCATION")
    val location: String = "inox"
)
