package com.example.internetcookbook

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.adapter.TabsPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_pager.*
import kotlinx.android.synthetic.main.fragment_pager.view.*
import kotlinx.android.synthetic.main.fragment_pager.view_pager
import java.io.Serializable

class PagerFragment : Fragment() {

    interface ViewCreatedListener : Serializable {
        fun invoke()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_pager, container, false)
        handleTransition()
        initTabLayout(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    private fun handleTransition() {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition()
    }

    private fun initAdapter() {
        fragmentManager?.let { fragmentManager ->
            val callback = object : ViewCreatedListener {
                override fun invoke() {
                    startPostponedEnterTransition()
                }
            }

            SampleFragmentPagerAdapter(context, fragmentManager, callback).also {
                view_pager.adapter = it
                view_pager.currentItem = 1
            }
        }
    }
    lateinit var pagerAdapter: TabsPagerAdapter

    private fun initTabLayout(view: View) {
        view.navigationView.selectedItemId = R.id.mNavHome
        pagerAdapter = TabsPagerAdapter(activity!!.supportFragmentManager)
        val viewPager = view.view_pager
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 1

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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

        view.navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
//
//      Navigating to the correct selected Item
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
