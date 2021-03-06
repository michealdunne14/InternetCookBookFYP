package com.example.internetcookbook.pager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.TabsPagerAdapter
import com.example.internetcookbook.base.BaseView
import kotlinx.android.synthetic.main.fragment_pager.view.*
import org.jetbrains.anko.AnkoLogger

class PagerFragmentView : BaseView(), AnkoLogger {

    lateinit var presenter: PagerFragmentPresenter
    lateinit var pagerAdapter: TabsPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_pager, container, false)
        presenter = initPresenter(PagerFragmentPresenter(this)) as PagerFragmentPresenter
        initTabLayout(view)


        view.view_pager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {}

        })
        return view
    }

    private fun initTabLayout(view: View) {
        pagerAdapter = TabsPagerAdapter(childFragmentManager)
        val viewPager = view.view_pager
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 1
    }


}
