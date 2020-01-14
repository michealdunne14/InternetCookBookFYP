package com.example.internetcookbook.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentview.CameraFragmentView
import com.example.internetcookbook.fragmentview.FriendFragmentView
import com.example.internetcookbook.fragmentview.HomeFragmentView
import com.example.internetcookbook.pager.PagerFragmentView

class SampleFragmentPagerAdapter(
    private val context: Context?,
    fragmentManager: FragmentManager,
    callback: PagerFragmentView.ViewCreatedListener
) : FragmentStatePagerAdapter(fragmentManager) {

    private val fragmentList = listOf(
        CameraFragmentView(),
        HomeFragmentView.newInstance(callback),
        FriendFragmentView()
    )

    override fun getItem(position: Int): BaseView = fragmentList[position] as BaseView

    override fun getCount(): Int =
        fragmentList.size

    override fun getPageTitle(position: Int): String? =
        context?.resources?.getStringArray(R.array.fragment_titles)?.get(position)
}