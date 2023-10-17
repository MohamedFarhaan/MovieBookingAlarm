package com.example.moviebookingalarm.service.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.moviebookingalarm.MainActivity
import com.example.moviebookingalarm.MyApplication
import com.example.moviebookingalarm.constants.INOX_CITY
import com.example.moviebookingalarm.db.DatabaseInit
import com.example.moviebookingalarm.db.dao.MovieDao
import com.example.moviebookingalarm.service.network.InoxService
import com.example.moviebookingalarm.service.network.repository.InoxApiRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var ringtone: Ringtone
    @Inject
    lateinit var inoxApiRepository: InoxApiRepository
    @Inject
    lateinit var movieDao: MovieDao

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {


        GlobalScope.launch {
            val movie = movieDao.getMovie()
            val movieName = movie?.movieName ?: "-"
            if(movieName != "-") {
                var inte = Intent(context, MainActivity::class.java)
                var pendin = PendingIntent.getActivity(
                    context,
                    0,
                    inte,
                    PendingIntent.FLAG_IMMUTABLE
                )
                val movieResponse = inoxApiRepository.getScheduledMoviesList(movie!!.showDate,
                    getKeyByValue(INOX_CITY, movie.city) ?: 0)
                if (movieResponse.isSuccessful) {
                    val movieResponseBody = movieResponse.body().toString();
                    Log.d("Movie resp body", movieResponseBody);
                    if(movieResponseBody.indexOf(movie.movieName!!.uppercase()) > 0
                        && movieResponseBody.lowercase().indexOf(movie.location.lowercase()) > 0) {
                        Log.d("Booking Started", "$movieName Last checked at ${Date().toLocaleString()}")
                        val notificationBuilder = context?.let {
                            NotificationCompat.Builder(it, "101")
                                .setContentTitle("$movieName Booking started")
                                .setContentText("$movieName Booking started at INOX\nLast checked at ${Date().toLocaleString()}")
                                .setContentIntent(pendin)
                                .setSmallIcon(android.R.drawable.star_big_on)
                        }
                        var notificationManager: NotificationManager =
                            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if((notificationBuilder ?: null) != null) notificationManager.notify(101, notificationBuilder!!.build() )
                        ringtone.play()
                    } else {
                        Log.d("Booking not Started", "$movieName Last checked at ${Date().toLocaleString()}")
                        val notificationBuilder = context?.let {
                            NotificationCompat.Builder(it, "101")
                                .setContentTitle("$movieName Booking not yet started")
                                .setContentText("$movieName Last checked at ${Date().toLocaleString()}")
                                .setContentIntent(pendin)
                                .setSmallIcon(android.R.drawable.star_big_on)
                        }
                        var notificationManager: NotificationManager =
                            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if((notificationBuilder ?: null) != null) notificationManager.notify(101, notificationBuilder!!.build() )
                    }
                } else {
                    Log.d("Server crashing", "movieName(${movie.movieName}) City(${movie.city}) Date(${movie.showDate}) Location(${movie.location})")
                    val notificationBuilder = context?.let {
                        NotificationCompat.Builder(it, "101")
                            .setContentTitle("$movieName - Server Crashing")
                            .setContentText("Booking might have started for $movieName \n Last checked at ${Date().toLocaleString()}")
                            .setContentIntent(pendin)
                            .setSmallIcon(android.R.drawable.star_big_on)
                    }
                    var notificationManager: NotificationManager =
                        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    ringtone.play()
                    if((notificationBuilder ?: null) != null) notificationManager.notify(101, notificationBuilder!!.build() )
                }
            }
        }
    }
}

fun getKeyByValue(map: Map<Int, String>, value: String): Int? {
    for ((key, mapValue) in map) {
        if (mapValue.trim().lowercase() == value) {
            return key
        }
    }
    return null // Value not found in the map
}