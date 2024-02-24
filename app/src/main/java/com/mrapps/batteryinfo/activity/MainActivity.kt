package com.mrapps.batteryinfo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    }


}
