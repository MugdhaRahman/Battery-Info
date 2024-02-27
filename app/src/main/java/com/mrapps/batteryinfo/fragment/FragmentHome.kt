package com.mrapps.batteryinfo.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrapps.batteryinfo.R
import com.mrapps.batteryinfo.databinding.FragmentHomeBinding
import com.mrapps.batteryinfo.meter.Range
import java.text.DecimalFormat
import kotlin.math.abs


class FragmentHome : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val batteryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {

                val deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                        deviceStatus == BatteryManager.BATTERY_STATUS_FULL

                if (isCharging) {
                    binding.chargingStatus.text = "Charging"
                    binding.chargingStatus.setTextColor(requireContext().getColor(R.color.green))
                } else {
                    binding.chargingStatus.text = "Discharging"
                    binding.chargingStatus.setTextColor(requireContext().getColor(R.color.red))
                }

                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = level / scale.toFloat()
                val batteryPercentage = (batteryPct * 100).toInt()
                binding.batteryPercentage.text = "$batteryPercentage%"

                if (isCharging || batteryPct * 100 == 100f) {
                    binding.batteryPercentage.setTextColor(requireContext().getColor(R.color.green))
                } else if (batteryPct * 100 < 10) {
                    binding.batteryPercentage.setTextColor(requireContext().getColor(R.color.red))
                } else {
                    binding.batteryPercentage.setTextColor(requireContext().getColor(R.color.colorPrimary))
                }

                if (batteryPct * 100 == 100f) {
                    binding.chargingStatus.text = "Fully Charged"
                }

                if (batteryPct * 100 < 20 && !isCharging) {
                    binding.chargingStatus.text = "Low Battery"
                }


                binding.abv.attachBatteryIntent(intent)
            }
        }
    }

    val handler = Handler(Looper.getMainLooper())

    val range = Range()
    val range2 = Range()

    var maxPower = 0.00
    var minPower = 0.00

    var capacity = 0.00
    var voltage = 0.00


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().registerReceiver(
            batteryReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        setupHalfGauge()

    }

    private fun setupHalfGauge() {

        range.color = requireContext().getColor(R.color.red)
        range.from = -4000.00
        range.to = 0.00

        range2.color = requireContext().getColor(R.color.green)
        range2.from = 1.00
        range2.to = 4000.0

        //add color ranges to gauge
        binding.halfGauge.addRange(range)
        binding.halfGauge.addRange(range2)

        binding.halfGauge.minValue = -4000.00
        binding.halfGauge.maxValue = 4000.00


        binding.halfGauge.setNeedleColor(requireContext().getColor(R.color.needleColor))
        binding.halfGauge.valueColor = requireContext().getColor(R.color.textColor)
        binding.halfGauge.minValueTextColor = requireContext().getColor(R.color.red)
        binding.halfGauge.maxValueTextColor = requireContext().getColor(R.color.green)
    }

    private fun getAmperage(context: Context): String? {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        var batteryCurrent =
            -batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).toFloat()

        return if (batteryCurrent < 0) {
            if (abs(batteryCurrent / 1000) < 1.0) {
                batteryCurrent *= 1000
            }
            val df = DecimalFormat("#.##")
            df.format((batteryCurrent / 1000).toDouble())
        } else {
            if (abs(batteryCurrent) > 100000.0) {
                batteryCurrent /= 1000
            }
            val df = DecimalFormat("#.##")
            df.format(batteryCurrent.toDouble())
        }
    }

    override fun onResume() {
        super.onResume()
        val runnable = object : Runnable {
            override fun run() {
                val currentValue = getAmperage(requireActivity())?.toDouble() ?: 0.0

                binding.halfGauge.value = currentValue

                handler.postDelayed(this, 500)
            }
        }

        handler.post(runnable)

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        requireActivity().unregisterReceiver(batteryReceiver)
    }


}