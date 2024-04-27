package com.mrapps.batteryinfo.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.BatteryManager.BATTERY_PLUGGED_USB
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrapps.batteryinfo.R
import com.mrapps.batteryinfo.databinding.FragmentHomeBinding
import com.mrapps.batteryinfo.meter.Range
import java.lang.Double.isNaN
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class FragmentHome : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private var isReceiverRegistered = false


    val batteryUseList = mutableListOf<Int>()

    var count = 0

    private val batteryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {

                val deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging =
                    deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING || deviceStatus == BatteryManager.BATTERY_STATUS_FULL

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
                } else if (batteryPct * 100 < 30) {
                    binding.batteryPercentage.setTextColor(requireContext().getColor(R.color.warningColor))
                } else {
                    binding.batteryPercentage.setTextColor(requireContext().getColor(R.color.colorPrimary))
                }

                if (batteryPct * 100 == 100f) {
                    binding.chargingStatus.text = "Fully Charged"
                }

                if (batteryPct * 100 < 20 && !isCharging) {
                    binding.chargingStatus.text = "Low Battery"
                }

                //battery voltage
                var d = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).toDouble().also {
                    isNaN(it)
                }
                if (d > 12) {
                    d /= 1000.0
                }
                val decimalFormat = DecimalFormat("#.##")
                val voltage = decimalFormat.format(d)
                binding.voltage.text = voltage + "V"

                //battery temperature
                val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1).toDouble()
                binding.temperature.text = decimalFormat.format(temp / 10) + "°C"

                if (temp / 10 > 39) {
                    binding.temperature.setTextColor(requireContext().getColor(R.color.red))
                    binding.temperatureIcon.setColorFilter(requireContext().getColor(R.color.red))
                } else {
                    binding.temperature.setTextColor(requireContext().getColor(R.color.green))
                    binding.temperatureIcon.setColorFilter(requireContext().getColor(R.color.colorPrimary))
                }

                // total capacity
                val capacity = getAmperage(requireContext())!!.toFloat()

                val watt = capacity * voltage.toDouble() / 1000
                binding.watt.text = decimalFormat.format(watt) + "W"

                if (isCharging) {
                    binding.wattTitle.text = "Charging W"
                    binding.wattIcon.setColorFilter(requireContext().getColor(R.color.colorPrimary))
                } else {
                    binding.wattTitle.text = "Discharging W"
                    binding.wattIcon.setColorFilter(requireContext().getColor(R.color.red))
                }

                //battery health
                when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)) {
                    BatteryManager.BATTERY_HEALTH_COLD -> {
                        binding.health.text = "Cold"
                        binding.health.setTextColor(requireContext().getColor(R.color.linkColor))
                    }

                    BatteryManager.BATTERY_HEALTH_DEAD -> {
                        binding.health.text = "Almost Dead"
                        binding.health.setTextColor(requireContext().getColor(R.color.red))
                        binding.healthCard.setCardBackgroundColor(requireContext().getColor(R.color.lightRed))
                    }

                    BatteryManager.BATTERY_HEALTH_GOOD -> {
                        binding.health.text = "Good"
                        binding.health.setTextColor(requireContext().getColor(R.color.green))
                    }

                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                        binding.health.text = "Overheat"
                        binding.health.setTextColor(requireContext().getColor(R.color.red))
                        binding.healthCard.setCardBackgroundColor(requireContext().getColor(R.color.lightRed))
                    }

                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                        binding.health.text = "Over Voltage"
                        binding.health.setTextColor(requireContext().getColor(R.color.red))
                    }

                    BatteryManager.BATTERY_HEALTH_UNKNOWN -> {
                        binding.health.text = "Unknown"
                        binding.health.setTextColor(requireContext().getColor(R.color.textColor))
                    }

                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> {
                        binding.health.text = "Unspecified Failure"
                        binding.health.setTextColor(requireContext().getColor(R.color.red))
                    }
                }

                // power source
                when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
                    BatteryManager.BATTERY_PLUGGED_AC -> {
                        binding.powerSource.text = "AC"
                        binding.powerIcon.setColorFilter(requireContext().getColor(R.color.electricBlue))
                    }

                    BATTERY_PLUGGED_USB -> {
                        binding.powerSource.text = "USB"
                        binding.powerIcon.setColorFilter(requireContext().getColor(R.color.electricBlue))
                        binding.powerSource.setTextColor(requireContext().getColor(R.color.electricBlue))
                    }

                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
                        binding.powerSource.text = "Wireless"
                        binding.powerSource.setTextColor(requireContext().getColor(R.color.electricBlue))
                        binding.powerIcon.setColorFilter(requireContext().getColor(R.color.electricBlue))
                    }

                    else -> {
                        binding.powerSource.text = "Battery"
                        binding.powerIcon.setColorFilter(requireContext().getColor(R.color.colorPrimary))
                        binding.powerSource.setTextColor(requireContext().getColor(R.color.textColor))
                    }
                }

                //battery technology
                binding.technology.text = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)


                if (!isCharging) {
                    binding.llChargingTime.visibility = View.GONE
                    binding.llBackupTime.visibility = View.VISIBLE
                } else {
                    binding.llChargingTime.visibility = View.VISIBLE
                    binding.llBackupTime.visibility = View.GONE
                }

                binding.abv.attachBatteryIntent(intent)

            }
        }
    }


    val handler = Handler(Looper.getMainLooper())
    val handler2 = Handler(Looper.getMainLooper())

    val range = Range()
    val range2 = Range()

    var maxPower = 0.00
    var minPower = 0.00

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isReceiverRegistered) {
            requireActivity().registerReceiver(
                batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            isReceiverRegistered = true
        }

        setupHalfGauge()

        setupOnClicks()

        setupBatteryInfo()


    }

    private fun setupHalfGauge() {

        range.color = requireContext().getColor(R.color.red)
        range.from = -3000.00
        range.to = 0.00

        range2.color = requireContext().getColor(R.color.green)
        range2.from = 1.00
        range2.to = 3000.00

        //add color ranges to gauge
        binding.halfGauge.addRange(range)
        binding.halfGauge.addRange(range2)

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

    private fun getAmperageSamsung(context: Context): String? {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        var batteryCurrent =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).toFloat()

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


    private fun setupOnClicks() {

        binding.resetMaxOut.setOnClickListener {
            minPower = 0.0
        }

        binding.resetMaxIn.setOnClickListener {
            maxPower = 0.0
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setupBatteryInfo() {

        val totalCapacity = getBatteryCapacity(requireActivity()).toInt()

        binding.maxCapacity.text = "$totalCapacity mAh"

        //android version
        binding.version.text = Build.VERSION.RELEASE

        //android model
        binding.model.text = Build.MODEL

        //manufacturer
        binding.manufacture.text = Build.MANUFACTURER


    }

    @SuppressLint("PrivateApi")
    fun getBatteryCapacity(context: Context?): Double {
        val obj: Any? = try {
            Class.forName("com.android.internal.os.PowerProfile").getConstructor(
                *arrayOf<Class<*>>(
                    Context::class.java
                )
            ).newInstance(context)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return try {
            Class.forName("com.android.internal.os.PowerProfile").getMethod(
                "getAveragePower", *arrayOf<Class<*>>(
                    String::class.java
                )
            ).invoke(obj, *arrayOf<Any>("battery.capacity")) as Double
        } catch (e2: Exception) {
            e2.printStackTrace()
            0.0
        }
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return "${hours}hrs ${minutes}min"
    }


    override fun onResume() {
        super.onResume()
        val runnable = object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {

                val maxIn = getAmperage(requireActivity())!!.toFloat()

                if (maxIn >= range2.to) {
                    range2.to = maxIn.toDouble() + 1000
                    range.from = -range2.to
                    binding.halfGauge.maxValue = range2.to
                    binding.halfGauge.minValue = range.from
                } else {
                    range.from = -3000.00
                    range2.to = 3000.0
                    binding.halfGauge.minValue = -3000.00
                    binding.halfGauge.maxValue = 3000.00
                }

                //add color ranges to gauge
                binding.halfGauge.addRange(range)
                binding.halfGauge.addRange(range2)


                val currentValue = getAmperage(requireActivity())?.toDouble() ?: 0.0

                val currentValueSamsung = getAmperageSamsung(requireActivity())?.toDouble() ?: 0.0

                if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
                    binding.halfGauge.value = currentValueSamsung
                } else {
                    binding.halfGauge.value = currentValue
                }

                binding.halfGauge.value = currentValue


                val amp = getAmperage(requireActivity())!!.toFloat()
                val df = DecimalFormat("#.##")

                if (amp < 0) {
                    minPower = min(minPower, amp.toDouble())
                }
                if (amp > 0) {
                    maxPower = max(maxPower, amp.toDouble())
                }

                binding.maxIn.text = "Max In = " + df.format(maxPower) + " mA"
                binding.maxOut.text = "Max Out = " + df.format(minPower) + " mA"

                // calculate current capacity
                val batteryManager =
                    requireActivity().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val chargeCounter =
                    batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                val currentCapacity = chargeCounter / 1000
                binding.currentCapacity.text = "$currentCapacity mAh"

                handler.postDelayed(this, 500)
            }

        }

        handler.post(runnable)

        val runnable2 = object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                count++

                if (count % 3 == 0) {
                    count = 0
                    batteryUseList.clear()
                } else {

                    val dischargingRate = getAmperage(requireActivity())!!.toInt()
                    batteryUseList.add(dischargingRate)

                    val averageDischargingRate = batteryUseList.average()

                    // Calculate current capacity
                    val batteryManager =
                        requireActivity().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                    val chargeCounter =
                        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                    val currentCapacity = chargeCounter / 1000
                    val estimatedTimeSeconds = (currentCapacity / averageDischargingRate) * 3600
                    val positiveEstimatedTimeSeconds = abs(estimatedTimeSeconds)
                    val hours = positiveEstimatedTimeSeconds / 3600
                    val minutes = (positiveEstimatedTimeSeconds % 3600) / 60


                    val totalCapacity = getBatteryCapacity(requireActivity()).toInt()
                    val remainingCapacity = totalCapacity - currentCapacity

                    Log.e("TAG", "remainingCp: $remainingCapacity")

                    val timeInSeconds =
                        (remainingCapacity.toDouble() / averageDischargingRate) * 3600

                    Log.e("TAG", "timeInSeconds: $timeInSeconds")

                    val formattedTime = formatTime(timeInSeconds.toLong())

                    //check if usb
                    val deviceStatus = requireActivity().registerReceiver(
                        null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                    )?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)

                    if (deviceStatus == BATTERY_PLUGGED_USB) {
                        binding.estimatedTime.text = "USB Charging"
                    } else {
                        binding.estimatedTime.text = formattedTime
                    }

                    binding.remainingTime.text = "${hours.toInt()}hrs ${minutes.toInt()}min"

                }

                handler2.postDelayed(this, 3000)
            }
        }
        handler2.post(runnable2)


    }

    override fun onPause() {
        super.onPause()
        if (isReceiverRegistered) {
            requireActivity().unregisterReceiver(batteryReceiver)
            isReceiverRegistered = false
        }
        handler.removeCallbacksAndMessages(null)
        handler2.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        handler2.removeCallbacksAndMessages(null)
        if (isReceiverRegistered) {
            requireActivity().unregisterReceiver(batteryReceiver)
            isReceiverRegistered = false
        }
    }


}