package com.mrapps.batteryinfo.utils

import com.mrapps.batteryinfo.activity.MainActivity

class BatteryUsagePredictor {

    private val historicalData = mutableListOf<MainActivity.BatteryData>()

    fun addBatteryData(batteryLevel: Int, time: Int) {
        historicalData.add(MainActivity.BatteryData(batteryLevel, time))
    }

    fun predictRemainingTime(currentBatteryLevel: Int): Int {
        // Check if we have enough historical data to make a prediction
        if (historicalData.size < 2) {
            return -1
        }

        // Extract battery level and time data from historical data
        val batteryLevels = historicalData.map { it.batteryLevel.toDouble() }
        val times = historicalData.map { it.time.toDouble() }

        // Perform linear regression to predict time based on battery level
        val n = batteryLevels.size
        val sumX = batteryLevels.sum()
        val sumY = times.sum()
        val sumXY = batteryLevels.zip(times).sumOf { it.first * it.second }
        val sumXSquare = batteryLevels.sumOf { it * it }

        val slope = (n * sumXY - sumX * sumY) / (n * sumXSquare - sumX * sumX)
        val intercept = (sumY - slope * sumX) / n

        // Use the linear regression equation to predict remaining time
        val predictedTime = slope * currentBatteryLevel + intercept
        return predictedTime.toInt()
    }
}