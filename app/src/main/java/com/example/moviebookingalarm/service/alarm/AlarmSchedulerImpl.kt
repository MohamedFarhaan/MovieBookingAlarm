package com.example.moviebookingalarm.service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.AlarmManagerCompat
import com.example.moviebookingalarm.db.entity.AlarmItem

class AlarmSchedulerImpl(
    private val context: Context
): AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context,
        "ALARM_INOX".hashCode(),
        intent,
        PendingIntent.FLAG_IMMUTABLE)
    override fun schedule(item: AlarmItem) {

        try {
            // You can schedule exact alarms
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                5*1000,
                pendingIntent
            )
        } catch (ex: SecurityException) {
            println("Security exception: ${ex.message}")
        }
    }
    override fun cancel(item: AlarmItem) {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, notification);
        ringtone.stop()
        alarmManager.cancel(
            pendingIntent
        )
    }


}