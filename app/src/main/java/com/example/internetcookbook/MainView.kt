package com.example.internetcookbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.adapter.TabsPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.main_layout.*

class MainView : AppCompatActivity(), LifecycleOwner {

    lateinit var pagerAdapter: TabsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView.selectedItemId = R.id.mNavHome
        pagerAdapter = TabsPagerAdapter(
            supportFragmentManager
        )
        view_pager.adapter = pagerAdapter
        view_pager.currentItem = 1

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        navigationView.selectedItemId = R.id.mNavCamera
                    }
                    1 -> {

                        navigationView.selectedItemId = R.id.mNavHome
                    }
                    2 -> {
                        navigationView.selectedItemId = R.id.mNavFavourites
                    }
                    else -> navigationView.selectedItemId = R.id.mNavHome
                }
            }
        })

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    //  Navigating to the correct selected Item
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.mNavCamera -> {
                view_pager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.mNavHome -> {
                view_pager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
            R.id.mNavFavourites -> {
                view_pager.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
