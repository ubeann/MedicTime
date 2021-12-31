package com.medictime.helper

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.medictime.R
import java.time.OffsetDateTime
import android.app.NotificationManager

class NotificationMedicine : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val id = intent.getIntExtra(EXTRA_ID, 1)
        val channelId = "Channel_1"
        val channelName = "Medicine Notification"
        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(description)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(id, notification)
    }

    fun set(context: Context, id: Int, title: String, description: String, dateTime: OffsetDateTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationMedicine::class.java)

        intent.putExtra(EXTRA_TITLE, title)
        intent.putExtra(EXTRA_DESCRIPTION, description)
        intent.putExtra(EXTRA_ID, id)

        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            dateTime.toInstant().toEpochMilli(),
            pendingIntent
        )

    }

    fun delete(context: Context, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationMedicine::class.java)

        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_DESCRIPTION = "description"
        const val EXTRA_ID = "id"
    }
}