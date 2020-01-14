package com.example.internetcookbook.pager

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.SampleFragmentPagerAdapter
import com.example.internetcookbook.adapter.TabsPagerAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentpresenter.FriendFragmentPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_pager.*
import kotlinx.android.synthetic.main.fragment_pager.view.*
import kotlinx.android.synthetic.main.fragment_pager.view_pager
import org.jetbrains.anko.AnkoLogger
import java.io.Serializable

class PagerFragmentView : BaseView(), AnkoLogger {

    lateinit var presenter: PagerFragmentPresenter


    interface ViewCreatedListener : Serializable {
        fun invoke()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_pager, container, false)
        presenter = initPresenter(PagerFragmentPresenter(this)) as PagerFragmentPresenter
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
            val callback = object :
                ViewCreatedListener {
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
        pagerAdapter = TabsPagerAdapter(activity!!.supportFragmentManager)
        val viewPager = view.view_pager
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 1
    }


}
