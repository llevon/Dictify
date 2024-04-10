package com.dictify.home

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstHomeFragment()
            1 -> SecondHomeFragment()
            else -> ThirdHomeFragment()
        }
    }

    fun startAutoRotation(viewPager: ViewPager2, interval: Long) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = viewPager.currentItem
                viewPager.setCurrentItem((currentItem + 1) % itemCount, true)
                handler.postDelayed(this, interval)
            }
        }
        handler.postDelayed(runnable, interval)
    }


    fun stopAutoRotation() {
//for stopping
        //
    }
}
