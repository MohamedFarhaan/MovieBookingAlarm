package com.example.moviebookingalarm.db

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviebookingalarm.db.dao.MovieDao
import com.example.moviebookingalarm.db.entity.Movie

@Database(entities = [Movie::class], version = 1)
abstract class DatabaseInit : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseInit? = null


        fun getDatabase(context: Context): DatabaseInit {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseInit::class.java,
                    "movie_book_alarm_v2.db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}