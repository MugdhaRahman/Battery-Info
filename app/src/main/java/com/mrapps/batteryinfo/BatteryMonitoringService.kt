package com.mrapps.batteryinfo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mrapps.batteryinfo.activity.BatteryNotification

class BatteryMonitoringService : Service() {

    private lateinit var batteryStatusReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()


        batteryStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING

                val chargingThreshold = 80
                val lowBatteryThreshold = 15

                if (!isCharging && level <= lowBatteryThreshold) {
                    showLowBatteryNotification(level)
                }

                if (isCharging && level >= chargingThreshold) {
                    showChargingNotification(level)
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryStatusReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryStatusReceiver)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showLowBatteryNotification(batteryLevel: Int) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "low_battery",
                "Low Battery",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, BatteryNotification::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(this, "low_battery")
            .setContentTitle("Low Battery Alert")
            .setContentText("Battery level is $batteryLevel%. Please charge your device.")
            .setSmallIcon(R.drawable.ic_pref_notificastion)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun showChargingNotification(batteryLevel: Int) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "charging",
                "Charging",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, BatteryNotification::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(this, "charging")
            .setContentTitle("Charging Alert")
            .setContentText("Battery level is $batteryLevel%. Battery is fully charged.")
            .setSmallIcon(R.drawable.ic_pref_notificastion)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }
}