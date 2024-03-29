package com.mrapps.batteryinfo

import androidx.lifecycle.LiveData
import javax.sql.CommonDataSource

class Repository(private val dataSource: PrefDataSource) {

    fun getProgressLiveData(): LiveData<Int> {
        return dataSource.getProgressLiveData()
    }

    fun getSwitchStateLiveData(): LiveData<Boolean> {
        return dataSource.getSwitchStateLiveData()
    }

    fun setProgress(progress: Int) {
        dataSource.setProgress(progress)
    }

    fun setSwitchState(isChecked: Boolean) {
        dataSource.setSwitchState(isChecked)
    }
}