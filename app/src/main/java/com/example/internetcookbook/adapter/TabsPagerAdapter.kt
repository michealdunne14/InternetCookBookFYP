package com.example.internetcookbook.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.internetcookbook.item.ItemFragmentView
import com.example.internetcookbook.fragmentview.CameraFragmentView
import com.example.internetcookbook.fragmentview.HomeFragmentView

class TabsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return CameraFragmentView()
            }
            1 -> {
                return HomeFragmentView()
            }
            2 -> {
                return ItemFragmentView()
            }
            else -> return HomeFragmentView()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}