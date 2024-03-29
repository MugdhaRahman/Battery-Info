package com.mrapps.batteryinfo.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mrapps.batteryinfo.BatteryMonitoringService
import com.mrapps.batteryinfo.ViewModelNotification
import com.mrapps.batteryinfo.databinding.ActivityBatteryNotificationBinding

class BatteryNotification : AppCompatActivity() {

    private val binding: ActivityBatteryNotificationBinding by lazy {
        ActivityBatteryNotificationBinding.inflate(layoutInflater)
    }

    private val viewModel: ViewModelNotification by viewModels()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        setupSeekBar()

        setupSwitch()

        viewModel.chargingAlarmEnabled.observe(this) { enabled ->
            binding.switchChargingAlarm.isChecked = enabled
        }

        viewModel.lowBatteryAlarmEnabled.observe(this) { enabled ->
            binding.switchLowBatteryAlarm.isChecked = enabled
        }

        viewModel.chargingAlarmProgress.observe(this) { progress ->
            binding.chargingFullAlarmPercent.text = "$progress%"
            binding.abv.chargeLevel = progress
            binding.abv.invalidate()
        }

        viewModel.lowBatteryAlarmProgress.observe(this) { progress ->
            binding.chargingFullAlarmPercentLow.text = "$progress%"
            binding.abvLow.chargeLevel = progress
            binding.abvLow.invalidate()
        }
    }

    private fun setupSwitch() {
        binding.switchChargingAlarm.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChargingAlarmEnabled(isChecked)
            if (isChecked) {
                startBatteryMonitoringService()
            } else {
                stopBatteryMonitoringService()
            }
        }

        binding.switchLowBatteryAlarm.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setLowBatteryAlarmEnabled(isChecked)
        }
    }

    private fun setupSeekBar() {
        binding.seekbarChargingAlarm.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.setChargingAlarmProgress(progress)
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
                viewModel.setLowBatteryAlarmProgress(progress)
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