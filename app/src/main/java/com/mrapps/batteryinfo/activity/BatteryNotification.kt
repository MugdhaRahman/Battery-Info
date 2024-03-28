package com.mrapps.batteryinfo.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.mrapps.batteryinfo.databinding.ActivityBatteryNotificationBinding

class BatteryNotification : AppCompatActivity() {

    private val binding: ActivityBatteryNotificationBinding by lazy {
        ActivityBatteryNotificationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        setupSeekBar()

    }

    private fun setupSeekBar() {

        binding.seekbarChargingAlarm.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.chargingFullAlarmPercent.text = "$progress%"
                progress.also { binding.abv.chargeLevel = it }
                binding.abv.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        binding.seekbarChargingAlarmLow.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.chargingFullAlarmPercentLow.text = "$progress%"
                binding.abvLow.chargeLevel = progress
                binding.abvLow.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

    }
}