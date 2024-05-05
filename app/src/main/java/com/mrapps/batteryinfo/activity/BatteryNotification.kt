package com.mrapps.batteryinfo.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.mrapps.batteryinfo.databinding.ActivityBatteryNotificationBinding
import com.mrapps.batteryinfo.utils.BackgroundService

class BatteryNotification : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityBatteryNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("BatteryPrefs", Context.MODE_PRIVATE)

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
            sendSwitchStateToService(isChecked, "charging")
        }

        binding.switchLowBatteryAlarm.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("lowBatteryAlarmSwitchState", isChecked).apply()
            sendSwitchStateToService(isChecked, "low")
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
        var savedChargingAlarmValue = sharedPreferences.getInt("chargingAlarmValue", 80)
        var savedChargingAlarmLowValue = sharedPreferences.getInt("chargingAlarmLowValue", 20)

        sendSeekBarProgressToService(savedChargingAlarmValue, "charging")
        sendSeekBarProgressToService(savedChargingAlarmLowValue, "low")

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
                chargingAlarmPercentTextView.text = "$progress%"
                abv.chargeLevel = progress
                abv.invalidate()
                sharedPreferences.edit().putInt("chargingAlarmValue", progress).apply()
                savedChargingAlarmValue = progress // Update saved value
                sendSeekBarProgressToService(progress, "charging")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        chargingAlarmLowSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                chargingAlarmPercentLowTextView.text = "$progress%"
                abvLow.chargeLevel = progress
                abvLow.invalidate()
                sharedPreferences.edit().putInt("chargingAlarmLowValue", progress).apply()
                savedChargingAlarmLowValue = progress // Update saved value
                sendSeekBarProgressToService(progress, "low")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    private fun sendSwitchStateToService(isChecked: Boolean, type: String) {
        val intent = Intent(this, BackgroundService::class.java)
        intent.putExtra("SWITCH_STATE", isChecked)
        intent.putExtra("TYPE", type)
        // Retrieve the correct progress values based on the switch type
        val progress = if (type == "charging") {
            sharedPreferences.getInt("chargingAlarmValue", 80)
        } else {
            sharedPreferences.getInt("chargingAlarmLowValue", 20)
        }
        intent.putExtra("PROGRESS", progress)
        startService(intent)
    }


    private fun sendSeekBarProgressToService(progress: Int, type: String) {
        val intent = Intent(this, BackgroundService::class.java)
        intent.putExtra("PROGRESS", progress)
        intent.putExtra("TYPE", type)
        startService(intent)
    }
}