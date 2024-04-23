package com.mrapps.batteryinfo.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.mrapps.batteryinfo.databinding.ActivityBatteryNotificationBinding

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

        setupSeekBar()
        setupSwitch()
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
                chargingAlarmPercentTextView.text = "$progress%"
                abv.chargeLevel = progress
                abv.invalidate()
                sharedPreferences.edit().putInt("chargingAlarmValue", progress).apply()
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
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    private fun setupSwitch() {
        binding.switchChargingAlarm.setOnCheckedChangeListener { _, _ ->

            if (binding.switchChargingAlarm.isChecked) {
               sendFullBatteryNotification()
            } else {
                binding.switchLowBatteryAlarm.isChecked = false
            }

        }

        binding.switchLowBatteryAlarm.setOnCheckedChangeListener { _, _ ->

            if (binding.switchLowBatteryAlarm.isChecked) {
                sendLowBatteryNotification()
            } else {
                binding.switchChargingAlarm.isChecked = false
            }

        }
    }

    private fun sendLowBatteryNotification() {


    }

    private fun sendFullBatteryNotification() {


    }
}