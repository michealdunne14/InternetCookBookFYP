package com.example.internetcookbook.fragmentview

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.archaeologicalfieldwork.adapter.CardAdapter
import com.example.archaeologicalfieldwork.adapter.PostListener
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentpresenter.HomeFragPresenter
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.filterbyItem
import kotlinx.android.synthetic.main.fragment_home.view.mListRecyclerView
import kotlinx.android.synthetic.main.fragment_home.view.rangeBar
import kotlinx.android.synthetic.main.fragment_home_nav.view.*
import kotlinx.android.synthetic.main.horizontalscrollbar.view.*

class HomeFragmentView : BaseView(), PostListener, SwipeRefreshLayout.OnRefreshListener{

    lateinit var presenter: HomeFragPresenter


    lateinit var homeView: View
    private var show = false
    private var time = false
    private var item = false
    private var top = false
    private var difficulty = false
    private var basket = false
    var isLoading = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_home, container, false)
        homeView = view

        presenter = initPresenter(HomeFragPresenter(this)) as HomeFragPresenter
        initScrollListener()

        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)

        view.mListRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        swipeRefreshLayout.setOnRefreshListener(this)

        swipeRefreshLayout.setOnRefreshListener {
            presenter.doRefreshData(view)
        }


        view.mHomePost.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToPostFragment2()
            homeView.findNavController().navigate(action)
        }

        view.mHomeSettings.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToSettingsFragment()
            homeView.findNavController().navigate(action)
        }

        view.mBasketSearch.setOnClickListener {

        }

        view.mHomeTime.setOnClickListener {
            if(show) {
                time = false
                cancelFilter()
            }else {
                time = true
                top = false
                item = false
                difficulty = false
                showFilter()
            }
        }

        view.mHomeDifficultyLevel.setOnClickListener {
            if(show) {
                difficulty = false
                cancelFilter()
            }else {
                difficulty = true
                item = false
                top = false
                time = false
                showFilter()
            }
        }

        view.mHomeItem.setOnClickListener {
            if(show) {
                item = false
                cancelFilter()
            }else {
                item = true
                top = false
                time = false
                difficulty = false
                showFilter()
            }
        }

        view.mHomeTopPosts.setOnClickListener {
            if(show) {
                top = false
                cancelFilter()
            }else {
                top = true
                item = false
                time = false
                difficulty = false
                showFilter()
            }
        }

        presenter.doFindHomeData()

        return view
    }

    override fun showInformation(homeData: ArrayList<DataModel?>) {
        homeView.mListRecyclerView.adapter = CardAdapter(homeData, presenter)
        homeView.mListRecyclerView.adapter?.notifyDataSetChanged()
    }


    private fun initScrollListener() {
        homeView.mListRecyclerView.adapter?.notifyItemInserted(presenter.findData().size - 1)

        homeView.mListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == presenter.findData().size - 1) {
                        isLoading = true
                        loadMore()
                    }
                }
            }
        })
    }

    private fun loadMore() {
        presenter.loadMoreData()
    }

    override fun noDataAvilable(){
        Snackbar.make(homeView,"No More Data Available", Snackbar.LENGTH_LONG).show()
    }

    override fun removeLoading(findData: ArrayList<DataModel?>) {
        homeView.mListRecyclerView.adapter?.notifyItemRangeInserted(findData.lastIndex,findData.size)
        isLoading = false
    }

    private fun showFilter(){
        show = true
        if(time){
            homeView.filterbyItem.visibility = View.GONE
            homeView.horizontalScrollBar.visibility = View.GONE
            homeView.rangeBar.visibility = View.VISIBLE
        }else if(item){
            homeView.rangeBar.visibility = View.GONE
            homeView.filterbyItem.visibility = View.VISIBLE
            homeView.horizontalScrollBar.visibility = View.GONE
        }else if(top){
            homeView.rangeBar.visibility = View.GONE
            homeView.filterbyItem.visibility = View.GONE
            homeView.horizontalScrollBar.visibility = View.VISIBLE

            homeView.mHomeScrollBarFirstPosition.text = "Recipes this Week"
            homeView.mHomeScrollBarSecondPosition.text = "Recipes this Month"
            homeView.mHomeScrollBarThirdPosition.text = "Recipes this Year"
            homeView.mHomeScrollBarThirdPosition.visibility = View.VISIBLE
            homeView.mHomeScrollBarForthPosition.visibility = View.VISIBLE
            homeView.mHomeScrollBarFifthPosition.visibility = View.VISIBLE
            homeView.mHomeScrollBarSixthPosition.visibility = View.VISIBLE
        }else if(difficulty){
            homeView.rangeBar.visibility = View.GONE
            homeView.filterbyItem.visibility = View.GONE
            homeView.horizontalScrollBar.visibility = View.VISIBLE

            homeView.mHomeScrollBarFirstPosition.text = "Easy"
            homeView.mHomeScrollBarSecondPosition.text = "Medium"
            homeView.mHomeScrollBarThirdPosition.text = "Hard"
            homeView.mHomeScrollBarForthPosition.visibility = View.GONE
            homeView.mHomeScrollBarFifthPosition.visibility = View.GONE
            homeView.mHomeScrollBarSixthPosition.visibility = View.GONE
        }
            val constraintSet = ConstraintSet()
            constraintSet.clone(homeView.context, R.layout.fragment_home_filter)

            val transition = ChangeBounds()
            transition.interpolator = AnticipateOvershootInterpolator(1.0f)
            transition.duration = 500

            TransitionManager.beginDelayedTransition(homeConstraint, transition)
            constraintSet.applyTo(homeConstraint) //here constraint is the name of view to which we are applying the constraintSet
    }

    private fun cancelFilter(){
        show = false

        val constraintSet = ConstraintSet()
        constraintSet.clone(homeView.context, R.layout.fragment_home)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 500

        TransitionManager.beginDelayedTransition(homeConstraint, transition)
        constraintSet.applyTo(homeConstraint)  //here constraint is the name of view to which we are applying the constraintSet
    }


    override fun onPostClick(hillfort: PostModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRefresh() {

    }

}
