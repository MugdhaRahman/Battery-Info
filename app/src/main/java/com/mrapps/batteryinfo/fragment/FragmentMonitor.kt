package com.mrapps.batteryinfo.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrapps.batteryinfo.R
import com.mrapps.batteryinfo.databinding.FragmentMonitorBinding
import com.mrapps.batteryinfo.meter.Range
import com.rejowan.chart.components.XAxis
import com.rejowan.chart.data.Entry
import com.rejowan.chart.data.LineData
import com.rejowan.chart.data.LineDataSet
import com.rejowan.chart.interfaces.datasets.ILineDataSet
import java.text.DecimalFormat
import kotlin.math.abs

class FragmentMonitor : Fragment() {


    private val binding: FragmentMonitorBinding by lazy {
        FragmentMonitorBinding.inflate(layoutInflater)
    }

    private var isReceiverRegistered = false

    var capacity = 0f
    var voltage = 0f

    val handler = Handler(Looper.getMainLooper())

    val range = Range()
    val range2 = Range()

    val rangeCapacity = Range()
    val range2Capacity = Range()
    val range3Capacity = Range()

    val rangeVoltage = Range()
    val range2Voltage = Range()
    val range3Voltage = Range()

    val rangeTemperature = Range()
    val range2Temperature = Range()
    val range3Temperature = Range()
    val range4Temperature = Range()

    var temp = 0.0

    private val batteryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {

                val deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging =
                    deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING || deviceStatus == BatteryManager.BATTERY_STATUS_FULL

                if (isCharging) {
                    binding.batteryStatus.text = "Charging"
                } else {
                    binding.batteryStatus.text = "Discharging"
                }

                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = level / scale.toFloat()
                val batteryPercentage = (batteryPct * 100).toInt()

                val currentValue = getAmperage(requireActivity())?.toDouble() ?: 0.0

                val batteryManager =
                    requireActivity().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val chargeCounter =
                    batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                capacity = (chargeCounter / 1000).toFloat()


                //battery voltage
                var d = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).toDouble().also {
                    Double
                }
                if (d > 12) {
                    d /= 1000.0
                }
                val decimalFormat = DecimalFormat("#.##")
                voltage = decimalFormat.format(d).toFloat()

                //battery temperature
                val temperature =
                    intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1).toDouble()
                temp =  decimalFormat.format(temperature / 10).toDouble()


            }
        }
    }


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

        setupBattery()
        setupCapacity()
        setupHalfGaugeCapacity()
        setupHalfGaugeTemp()

    }

    private fun setupHalfGauge() {

        range.color = requireContext().getColor(R.color.red)
        range.from = -1000.00
        range.to = 0.00

        range2.color = requireContext().getColor(R.color.green)
        range2.from = 1.00
        range2.to = 1000.00

        //add color ranges to gauge
        binding.halfGauge.addRange(range)
        binding.halfGauge.addRange(range2)

        binding.halfGauge.setNeedleColor(requireContext().getColor(R.color.needleColor))
        binding.halfGauge.valueColor = requireContext().getColor(R.color.textColor)
        binding.halfGauge.minValueTextColor = requireContext().getColor(R.color.red)
        binding.halfGauge.maxValueTextColor = requireContext().getColor(R.color.green)

    }

    private fun setupHalfGaugeCapacity() {

        val totalCapacity = getBatteryCapacity(requireContext())

        rangeCapacity.color = requireContext().getColor(R.color.red)
        rangeCapacity.from = 0.0
        rangeCapacity.to = totalCapacity / 4 - 1
        range2Capacity.color = requireContext().getColor(R.color.warningColor)
        range2Capacity.to = totalCapacity / 4
        range2Capacity.from = totalCapacity / 2 - 1
        range3Capacity.color = requireContext().getColor(R.color.green)
        range3Capacity.from = totalCapacity / 2
        range3Capacity.to = totalCapacity

        binding.halfGaugeCapacity.addRange(rangeCapacity)
        binding.halfGaugeCapacity.addRange(range2Capacity)
        binding.halfGaugeCapacity.addRange(range3Capacity)

        binding.halfGaugeCapacity.setNeedleColor(requireContext().getColor(R.color.needleColor))
        binding.halfGaugeCapacity.valueColor = requireContext().getColor(R.color.textColor)
        binding.halfGaugeCapacity.minValueTextColor = requireContext().getColor(R.color.red)
        binding.halfGaugeCapacity.maxValueTextColor = requireContext().getColor(R.color.green)

    }

    private fun setupHalfGaugeTemp() {

        rangeTemperature.color = requireContext().getColor(R.color.linkColor)
        rangeTemperature.from = 0.0
        rangeTemperature.to = 20.0
        range2Temperature.color = requireContext().getColor(R.color.green)
        range2Temperature.to = 20.0
        range2Temperature.from = 30.0
        range3Temperature.color = requireContext().getColor(R.color.warningColor)
        range3Temperature.from = 30.0
        range3Temperature.to = 40.0
        range4Temperature.color = requireContext().getColor(R.color.red)
        range4Temperature.from = 40.0
        range4Temperature.to = 50.0

        binding.halfGaugeTemp.addRange(rangeTemperature)
        binding.halfGaugeTemp.addRange(range2Temperature)
        binding.halfGaugeTemp.addRange(range3Temperature)
        binding.halfGaugeTemp.addRange(range4Temperature)

        binding.halfGaugeTemp.setNeedleColor(requireContext().getColor(R.color.needleColor))
        binding.halfGaugeTemp.valueColor = requireContext().getColor(R.color.textColor)
        binding.halfGaugeTemp.minValueTextColor = requireContext().getColor(R.color.linkColor)
        binding.halfGaugeTemp.maxValueTextColor = requireContext().getColor(R.color.red)


    }

    private fun setupBattery() {
        binding.batteryChart.description.isEnabled = false
        binding.batteryChart.setPinchZoom(true)
        binding.batteryChart.setDrawGridBackground(false)
        binding.batteryChart.isDragEnabled = true
        binding.batteryChart.setScaleEnabled(true)
        binding.batteryChart.setTouchEnabled(true)

        val xAxis: XAxis = binding.batteryChart.xAxis
        xAxis.isEnabled = false

        binding.batteryChart.axisLeft.isEnabled = false
        binding.batteryChart.axisRight.isEnabled = true

        val yAxis = binding.batteryChart.axisRight
        yAxis.textColor = requireActivity().getColor(R.color.textColor)
        binding.batteryChart.axisLeft.setDrawGridLines(false)
        binding.batteryChart.animateXY(1500, 1500)

        binding.batteryChart.legend.isEnabled = false

        val data = LineData()
        binding.batteryChart.data = data

    }

    private fun setupCapacity() {
        binding.capacityChart.description.isEnabled = false
        binding.capacityChart.setPinchZoom(true)
        binding.capacityChart.setDrawGridBackground(false)
        binding.capacityChart.isDragEnabled = true
        binding.capacityChart.setScaleEnabled(true)
        binding.capacityChart.setTouchEnabled(true)

        val xAxis: XAxis = binding.capacityChart.xAxis
        xAxis.isEnabled = false

        binding.capacityChart.axisLeft.isEnabled = false
        binding.capacityChart.axisRight.isEnabled = true

        val yAxis = binding.capacityChart.axisRight
        yAxis.textColor = requireActivity().getColor(R.color.textColor)
        binding.capacityChart.axisLeft.setDrawGridLines(false)
        binding.capacityChart.animateXY(1500, 1500)

        binding.capacityChart.legend.isEnabled = false

        val data = LineData()
        binding.capacityChart.data = data

    }


    private fun updateBattery() {

        val lineData = binding.batteryChart.data
        if (lineData != null) {
            var set: ILineDataSet? = lineData.getDataSetByIndex(0)


            if (set == null) {
                set = createSet()
                lineData.addDataSet(set)


            }

            lineData.addEntry(
                getAmperage(requireContext())?.let {
                    Entry(
                        set.entryCount.toFloat(),
                        it.toFloat()
                    )
                }, 0
            )

            if (set.entryCount > 25) {
                set.removeFirst()
                for (i in 0 until set.entryCount) {
                    val entry = set.getEntryForIndex(i)
                    entry.x = entry.x - 1
                }
            }


            lineData.notifyDataChanged()
            binding.batteryChart.notifyDataSetChanged()
            binding.batteryChart.invalidate()


//            if (getAmperage(requireContext())!!.contains("-")) {
//
//            } else {
//
//            }
        }


    }

    private fun updateBatteryCapacity() {

        val batteryManager =
            requireActivity().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val chargeCounter =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val currentCapacity = chargeCounter / 1000f

        val lineData = binding.capacityChart.data
        if (lineData != null) {
            var set: ILineDataSet? = lineData.getDataSetByIndex(0)

            if (set == null) {
                set = createSet()
                lineData.addDataSet(set)
            }

            lineData.addEntry(Entry(set.entryCount.toFloat(), currentCapacity), 0)

            if (set.entryCount > 25) {
                set.removeFirst()
                for (i in 0 until set.entryCount) {
                    val entry = set.getEntryForIndex(i)
                    entry.x = entry.x - 1
                }
            }


            lineData.notifyDataChanged()
            binding.capacityChart.notifyDataSetChanged()
            binding.capacityChart.invalidate()
        }
    }


    private fun createSet(): LineDataSet {
        val lineDataSet = LineDataSet(null, "")
        lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineDataSet.cubicIntensity = 0.4f
        lineDataSet.setDrawFilled(false)
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 1.8f
        lineDataSet.circleRadius = 4f
        lineDataSet.setCircleColor(requireActivity().getColor(R.color.textColor))
        lineDataSet.highLightColor = Color.rgb(244, 117, 117)
        lineDataSet.color = requireActivity().getColor(R.color.colorPrimary)
        lineDataSet.fillAlpha = 100
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawValues(false)
        return lineDataSet
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

    fun setupMonitor() {

        val maxIn = getAmperage(requireActivity())!!.toFloat()
        val totalCapacity = getBatteryCapacity(requireContext())

        val batteryManager =
            requireActivity().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val chargeCounter =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val currentCapacity = chargeCounter / 1000

        if (maxIn >= range2.to) {
            range2.to = maxIn.toDouble() + 1000
            range.from = -range2.to
            binding.halfGauge.maxValue = range2.to
            binding.halfGauge.minValue = range.from
        } else {
            range.from = -1000.00
            range2.to = 1000.0
            binding.halfGauge.minValue = -1000.00
            binding.halfGauge.maxValue = 1000.00
        }

        //add color ranges to gauge
        binding.halfGauge.addRange(range)
        binding.halfGauge.addRange(range2)

        val currentValue = getAmperage(requireActivity())?.toDouble() ?: 0.0
        binding.halfGauge.value = currentValue

        binding.halfGaugeCapacity.addRange(rangeCapacity)
        binding.halfGaugeCapacity.addRange(range2Capacity)
        binding.halfGaugeCapacity.addRange(range3Capacity)

        binding.halfGaugeCapacity.minValue = 0.0
        binding.halfGaugeCapacity.maxValue = totalCapacity

        binding.halfGaugeCapacity.value = currentCapacity.toDouble()

        // battery temperature from batteryreceiver


        binding.halfGaugeTemp.value = temp

        Log.e("TAG", "setupMonitor: $temp")

        binding.halfGaugeTemp.addRange(rangeTemperature)
        binding.halfGaugeTemp.addRange(range2Temperature)
        binding.halfGaugeTemp.addRange(range3Temperature)
        binding.halfGaugeTemp.addRange(range4Temperature)

        binding.halfGaugeTemp.minValue = 0.0
        binding.halfGaugeTemp.maxValue = 50.0


    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val runnable = object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {

                setupMonitor()

                updateBattery()

                updateBatteryCapacity()

                handler.postDelayed(this, 1000)
            }
        }

        handler.post(runnable)


    }

    override fun onPause() {
        super.onPause()
        if (isReceiverRegistered) {
            requireActivity().unregisterReceiver(batteryReceiver)
            isReceiverRegistered = false
        }
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isReceiverRegistered) {
            requireActivity().unregisterReceiver(batteryReceiver)
            isReceiverRegistered = false
        }
        handler.removeCallbacksAndMessages(null)

    }
}