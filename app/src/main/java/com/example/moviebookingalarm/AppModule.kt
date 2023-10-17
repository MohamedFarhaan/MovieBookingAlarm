package com.example.moviebookingalarm

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import com.example.moviebookingalarm.db.DatabaseInit
import com.example.moviebookingalarm.db.dao.MovieDao
import com.example.moviebookingalarm.viewmodel.MovieViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context): DatabaseInit {
        return DatabaseInit.getDatabase(context)
    }

    @Singleton
    @Provides
    fun getMovieDao(db: DatabaseInit): MovieDao {
        return db.movieDao();
    }



    @Singleton
    @Provides
    fun getRingtone(@ApplicationContext context: Context): Ringtone {
        return RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
    }

    @Singleton
    @Provides
    fun getMovieViewModel(@ApplicationContext context: Context, movieDao: MovieDao, ringtone: Ringtone): MovieViewModel {
        return MovieViewModel(context, movieDao, ringtone)
    }
}