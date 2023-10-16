package com.example.moviebookingalarm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.example.moviebookingalarm.MainActivity
import com.example.moviebookingalarm.db.DatabaseInit
import com.example.moviebookingalarm.viewmodel.MovieViewModel
import kotlinx.coroutines.launch


class ApiService : Service() {
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "HelloService"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun getDefaultRingtone(context: Context): Uri? {
        return RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(getBaseContext(), notification);
        // Start a repeating task to log "Hello" every 10 seconds
        val interval = 60 * 1000 // 60 seconds
        val appContext: Context = getApplicationContext();
        val movieViewModel = MovieViewModel(appContext);
        val runnable = object : Runnable {
            override fun run() {
                movieViewModel.viewModelScope.launch {
//                GlobalScope.launch {
                    var movieList = InoxService.getInoxServiceInstance().getMoviesList().body().toString()
                    var movieName = DatabaseInit.getDatabase(appContext).movieDao().getMovie()?.movieName ?: "-"
                    Log.d("Movie tickets open: $movieName", (movieList.indexOf("title='${movieName.uppercase()}") >= 0).toString())
                    val intent: Intent = Intent(appContext, MainActivity::class.java);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    appContext.startActivity(intent);
                    if(movieList.indexOf("title='${movieName.uppercase()}") >= 0) {
                        val notificationManager = getSystemService(NotificationManager::class.java)
                        var pendin = PendingIntent.getActivity(
                            appContext,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        val notificationBuilder = NotificationCompat.Builder(appContext, CHANNEL_ID)
                            .setContentTitle("${movieName} Booking started")
                            .setContentText("${movieName} booking started in INOX")
                            .setContentIntent(pendin)
                            .setSmallIcon(android.R.drawable.star_big_on)
                        notificationManager.notify(1, notificationBuilder.build())
                        ringtone.play()
                    } else {
                        ringtone.stop()
                    }
                }
                handler.postDelayed(this, interval.toLong())
            }
        }

        handler.postDelayed(runnable, interval.toLong())

        return START_STICKY
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Hello Service")
            .setContentText("Running in the background")
            .setSmallIcon(androidx.core.R.drawable.notification_action_background)

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Hello Service"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        stopForeground(true)
    }

    private val handler = Handler()
}
