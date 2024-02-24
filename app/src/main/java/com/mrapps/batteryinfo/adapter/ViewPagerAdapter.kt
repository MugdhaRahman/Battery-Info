package com.mrapps.batteryinfo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mrapps.batteryinfo.fragment.FragmentHome
import com.mrapps.batteryinfo.fragment.FragmentMonitor

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentHome()
            1 -> FragmentMonitor()
            else -> FragmentHome()
        }
    }

}