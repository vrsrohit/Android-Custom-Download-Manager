package com.rohit.customdownloadmanager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rohit.customdownloadmanager.R

object NotificationUtils {


    fun sendStatusNotification(message: String, context: Context, notificationId: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = Constants.notificationChannelName
            val description = Constants.notificationChannelDescription
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.notificationChannelId, name, importance)
            channel.description = description

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, Constants.notificationChannelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(Constants.notificationTitle)
            .setContentInfo(message)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
}