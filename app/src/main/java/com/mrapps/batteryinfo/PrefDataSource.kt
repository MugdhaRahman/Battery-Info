package com.mrapps.batteryinfo

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PrefDataSource(private val sharedPreferences: SharedPreferences) {


    private val progressLiveData = MutableLiveData<Int>()
    private val switchStateLiveData = MutableLiveData<Boolean>()

    init {
        // Initialize LiveData with values from SharedPreferences
        progressLiveData.value = sharedPreferences.getInt("progress", 0)
        switchStateLiveData.value = sharedPreferences.getBoolean("switch_state", false)
    }

    fun getProgressLiveData(): LiveData<Int> {
        return progressLiveData
    }

    fun getSwitchStateLiveData(): LiveData<Boolean> {
        return switchStateLiveData
    }

    fun setProgress(progress: Int) {
        sharedPreferences.edit().putInt("progress", progress).apply()
        progressLiveData.value = progress
    }

    fun setSwitchState(isChecked: Boolean) {
        sharedPreferences.edit().putBoolean("switch_state", isChecked).apply()
        switchStateLiveData.value = isChecked
    }
}