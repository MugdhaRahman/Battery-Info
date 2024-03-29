package com.mrapps.batteryinfo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelNotification(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("BatteryPrefs", Context.MODE_PRIVATE)

    val chargingAlarmEnabled = MutableLiveData<Boolean>()
    val lowBatteryAlarmEnabled = MutableLiveData<Boolean>()
    val chargingAlarmProgress = MutableLiveData<Int>()
    val lowBatteryAlarmProgress = MutableLiveData<Int>()

    init {
        // Restore state from SharedPreferences
        chargingAlarmEnabled.value = sharedPreferences.getBoolean("charging_alarm", false)
        lowBatteryAlarmEnabled.value = sharedPreferences.getBoolean("low_battery_alarm", false)
        chargingAlarmProgress.value = sharedPreferences.getInt("charging_alarm_progress", 0)
        lowBatteryAlarmProgress.value = sharedPreferences.getInt("low_battery_alarm_progress", 0)
    }

    fun setChargingAlarmEnabled(enabled: Boolean) {
        chargingAlarmEnabled.value = enabled
        saveSwitchState(enabled, "charging_alarm")
    }

    fun setLowBatteryAlarmEnabled(enabled: Boolean) {
        lowBatteryAlarmEnabled.value = enabled
        saveSwitchState(enabled, "low_battery_alarm")
    }

    fun setChargingAlarmProgress(progress: Int) {
        chargingAlarmProgress.value = progress
        saveProgress(progress, "charging_alarm_progress")
    }

    fun setLowBatteryAlarmProgress(progress: Int) {
        lowBatteryAlarmProgress.value = progress
        saveProgress(progress, "low_battery_alarm_progress")
    }

    private fun saveSwitchState(state: Boolean, key: String) {
        sharedPreferences.edit().putBoolean(key, state).apply()
    }

    private fun saveProgress(progress: Int, key: String) {
        sharedPreferences.edit().putInt(key, progress).apply()
    }
}