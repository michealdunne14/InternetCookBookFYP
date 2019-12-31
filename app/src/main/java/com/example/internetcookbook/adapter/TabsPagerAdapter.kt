package com.example.internetcookbook.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.internetcookbook.fragmentview.CameraFragment
import com.example.internetcookbook.fragmentview.FriendFragmentView
import com.example.internetcookbook.fragmentview.HomeFragmentView

class TabsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return CameraFragment()
            }
            1 -> {
                return HomeFragmentView()
            }
            2 -> {
                return FriendFragmentView()
            }
            else -> return HomeFragmentView()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}