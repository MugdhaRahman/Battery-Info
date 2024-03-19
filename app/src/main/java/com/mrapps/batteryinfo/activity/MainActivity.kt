package com.mrapps.batteryinfo.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.mrapps.batteryinfo.R
import com.mrapps.batteryinfo.adapter.ViewPagerAdapter
import com.mrapps.batteryinfo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewPagerAdapter: ViewPagerAdapter by lazy {
        ViewPagerAdapter(
            supportFragmentManager, lifecycle
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupBottomNav()

        binding.settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))

        }

    }

    private fun setupBottomNav() {

        binding.viewPager.adapter = viewPagerAdapter

        binding.bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.n_home -> {
                    binding.viewPager.currentItem = 0
                    binding.toolbarTitle.text = getString(R.string.app_name)
                }

                R.id.n_monitor -> {
                    binding.viewPager.currentItem = 1
                    binding.toolbarTitle.text = "Monitor"
                }
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.bottomBar.menu.findItem(R.id.n_home).isChecked = true
                        binding.toolbarTitle.text = getString(R.string.app_name)
                    }

                    1 -> {
                        binding.bottomBar.menu.findItem(R.id.n_monitor).isChecked = true
                        binding.toolbarTitle.text = "Monitor"
                    }
                }
            }
        })

    }

    data class BatteryData(val batteryLevel: Int, val time: Int)


}
