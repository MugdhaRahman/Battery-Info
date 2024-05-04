package com.mrapps.batteryinfo.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mrapps.batteryinfo.R
import com.mrapps.batteryinfo.databinding.ActivityBatteryNotificationBinding

class BatteryNotification : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityBatteryNotificationBinding

    // Notification channels
    private val CHANNEL_HIGH = "channel_high"
    private val CHANNEL_LOW = "channel_low"

    // Notification IDs
    private val NOTIFICATION_ID_HIGH = 1
    private val NOTIFICATION_ID_LOW = 2

    private lateinit var notificationManager: NotificationManagerCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager = NotificationManagerCompat.from(this)

        sharedPreferences = getSharedPreferences("BatteryPrefs", Context.MODE_PRIVATE)

        createNotificationChannels()

        binding.ivBack.setOnClickListener {
            finish()
        }

        setupSwitch()

        setupSeekBar()
    }

    private fun setupSwitch() {
        binding.switchChargingAlarm.isChecked =
            sharedPreferences.getBoolean("chargingAlarmSwitchState", false)
        binding.switchLowBatteryAlarm.isChecked =
            sharedPreferences.getBoolean("lowBatteryAlarmSwitchState", false)

        binding.switchChargingAlarm.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("chargingAlarmSwitchState", isChecked).apply()

        }

        binding.switchLowBatteryAlarm.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("lowBatteryAlarmSwitchState", isChecked).apply()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSeekBar() {
        val chargingAlarmSeekBar = binding.seekbarChargingAlarm
        val chargingAlarmPercentTextView = binding.chargingFullAlarmPercent
        val abv = binding.abv

        val chargingAlarmLowSeekBar = binding.seekbarChargingAlarmLow
        val chargingAlarmPercentLowTextView = binding.chargingFullAlarmPercentLow
        val abvLow = binding.abvLow

        // Retrieve previously saved values, defaulting to 0 if not found
        val savedChargingAlarmValue = sharedPreferences.getInt("chargingAlarmValue", 80)
        val savedChargingAlarmLowValue = sharedPreferences.getInt("chargingAlarmLowValue", 20)

        // Initialize SeekBars and TextViews with saved values
        chargingAlarmSeekBar.progress = savedChargingAlarmValue
        chargingAlarmPercentTextView.text = "$savedChargingAlarmValue%"
        abv.chargeLevel = savedChargingAlarmValue
        abv.invalidate()

        chargingAlarmLowSeekBar.progress = savedChargingAlarmLowValue
        chargingAlarmPercentLowTextView.text = "$savedChargingAlarmLowValue%"
        abvLow.chargeLevel = savedChargingAlarmLowValue
        abvLow.invalidate()

        chargingAlarmSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (binding.switchChargingAlarm.isChecked) {
                    chargingAlarmPercentTextView.text = "$progress%"
                    abv.chargeLevel = progress
                    abv.invalidate()
                    sharedPreferences.edit().putInt("chargingAlarmValue", progress).apply()
                } else {
                    Toast.makeText(
                        this@BatteryNotification,
                        "Please enable the switch to set the notification",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        chargingAlarmLowSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (binding.switchLowBatteryAlarm.isChecked) {
                    chargingAlarmPercentLowTextView.text = "$progress%"
                    abvLow.chargeLevel = progress
                    abvLow.invalidate()
                    sharedPreferences.edit().putInt("chargingAlarmLowValue", progress).apply()
                } else {
                    Toast.makeText(
                        this@BatteryNotification,
                        "Please enable the switch to set the notification",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
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


    override fun onResume() {
        super.onResume()
        registerBatteryReceiver()
    }

    private fun registerBatteryReceiver() {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryLevel = (level * 100 / scale.toFloat()).toInt()

                    // Check for battery full notification
                    if (batteryLevel >= binding.seekbarChargingAlarm.progress &&
                        binding.switchChargingAlarm.isChecked
                    ) {
                        showNotification(
                            "Battery Full",
                            "Your battery is now fully charged.",
                            CHANNEL_HIGH,
                            NOTIFICATION_ID_HIGH
                        )
                    }

                    // Check for battery low notification
                    if (batteryLevel <= binding.seekbarChargingAlarmLow.progress &&
                        binding.switchLowBatteryAlarm.isChecked
                    ) {
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
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
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