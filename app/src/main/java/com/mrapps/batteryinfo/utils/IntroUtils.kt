package com.mrapps.batteryinfo.utils

import android.content.Context
import android.content.SharedPreferences

class IntroUtils(context: Context) {

    private val prefName = "first_time"
    private val sharedPreferences: SharedPreferences? = context.getSharedPreferences(prefName, 0);
    private val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
    private val isFirstTimeLaunch = "IsFirstTimeLaunch"

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor?.putBoolean(isFirstTimeLaunch, isFirstTime)
        editor?.commit()
    }
    fun isFirstTimeLaunch(): Boolean {
        return sharedPreferences?.getBoolean(isFirstTimeLaunch, true) ?: true
    }

}