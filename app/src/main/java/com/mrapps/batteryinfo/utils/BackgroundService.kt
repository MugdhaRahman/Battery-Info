package com.mrapps.batteryinfo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mrapps.batteryinfo.R

class BackgroundService : Service() {
    private lateinit var notificationManager: NotificationManagerCompat
    private val CHANNEL_HIGH = "channel_high"
    private val CHANNEL_LOW = "channel_low"
    private val NOTIFICATION_ID_HIGH = 1
    private val NOTIFICATION_ID_LOW = 2
    private var battery = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerBatteryReceiver()
        notificationManager = NotificationManagerCompat.from(this)
        createNotificationChannels()
    }

    private fun registerBatteryReceiver() {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryLevel = (level * 100 / scale.toFloat()).toInt()
                    battery = batteryLevel  // Update the battery level here
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val switchState = it.getBooleanExtra("SWITCH_STATE", false)
            val progress = it.getIntExtra("PROGRESS", 0)
            val type = it.getStringExtra("TYPE")

            Log.e("BackgroundService", "Switch State: $progress, $type")
            Log.e("BackgroundService", "Battery Level 2: $battery")

            if (type != null && switchState) { // Check switchState first
                when (type) {
                    "charging" -> {
                        if (battery <= progress) {
                            Log.e("BackgroundService", "Battery Level 3: $battery")
                            showNotification(
                                "Battery Full",
                                "Your battery is now fully charged.",
                                CHANNEL_HIGH,
                                NOTIFICATION_ID_HIGH
                            )
                        }
                    }

                    "low" -> {
                        if (battery <= progress) {
                            showNotification(
                                "Low Battery",
                                "Your battery is running low.",
                                CHANNEL_LOW,
                                NOTIFICATION_ID_LOW
                            )
                        }
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nameHigh = getString(R.string.channel_high_name)
            val descriptionHigh = getString(R.string.channel_high_description)
            val importanceHigh = NotificationManager.IMPORTANCE_HIGH
            val channelHigh = NotificationChannel(CHANNEL_HIGH, nameHigh, importanceHigh).apply {
                description = descriptionHigh
            }

            val nameLow = getString(R.string.channel_low_name)
            val descriptionLow = getString(R.string.channel_low_description)
            val importanceLow = NotificationManager.IMPORTANCE_LOW
            val channelLow = NotificationChannel(CHANNEL_LOW, nameLow, importanceLow).apply {
                description = descriptionLow
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelHigh)
            notificationManager.createNotificationChannel(channelLow)
        }
    }

    private fun showNotification(
        title: String,
        message: String,
        channelId: String,
        notificationId: Int
    ) {
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun unregisterBatteryReceiver() {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

            }
        }
        try {
            unregisterReceiver(batteryReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver may not be registered
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBatteryReceiver()
    }

}