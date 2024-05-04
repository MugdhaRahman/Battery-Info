package com.mrapps.batteryinfo.utils

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mrapps.batteryinfo.R
import com.mrapps.batteryinfo.activity.BatteryNotification

class BackgroundService : Service() {
    private val NOTIFICATION_ID = 1234
    private val CHANNEL_ID = "your_channel_id" // Define your channel ID here

    override fun onCreate() {
        super.onCreate()
        // Create and display a foreground notification
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, BatteryNotification::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Your App is Running")
            .setContentText("Your app is monitoring battery levels.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)

        return notificationBuilder.build()
    }
}