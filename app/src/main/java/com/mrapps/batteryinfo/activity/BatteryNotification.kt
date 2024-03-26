package com.mrapps.batteryinfo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrapps.batteryinfo.R
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

    }
}