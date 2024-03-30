package com.mrapps.batteryinfo.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.mrapps.batteryinfo.BatteryMonitoringService
import com.mrapps.batteryinfo.databinding.ActivityBatteryNotificationBinding

class BatteryNotification : AppCompatActivity() {

    private val binding: ActivityBatteryNotificationBinding by lazy {
        ActivityBatteryNotificationBinding.inflate(layoutInflater)
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        setupSeekBar()

        setupSwitch()


    }

    private fun setupSwitch() {
        binding.switchChargingAlarm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startBatteryMonitoringService()
            } else {
                stopBatteryMonitoringService()
            }
        }

        binding.switchLowBatteryAlarm.setOnCheckedChangeListener { _, _ ->
            if (binding.switchLowBatteryAlarm.isChecked) {
                startBatteryMonitoringService()
            } else {
                stopBatteryMonitoringService()
            }
        }
    }

    private fun setupSeekBar() {
        binding.seekbarChargingAlarm.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.chargingFullAlarmPercent.text = "$progress%"
                binding.abv.chargeLevel = progress
                binding.abv.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekbarChargingAlarmLow.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.chargingFullAlarmPercentLow.text = "$progress%"
                binding.abvLow.chargeLevel = progress
                binding.abvLow.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startBatteryMonitoringService() {
        val serviceIntent = Intent(this, BatteryMonitoringService::class.java)
        startService(serviceIntent)
    }

    private fun stopBatteryMonitoringService() {
        val serviceIntent = Intent(this, BatteryMonitoringService::class.java)
        stopService(serviceIntent)
    }
}