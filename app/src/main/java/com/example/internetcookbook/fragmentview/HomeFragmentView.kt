package com.example.internetcookbook.fragmentview

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_home_nav.view.*
import kotlinx.android.synthetic.main.horizontalscrollbar.view.*

class HomeFragmentView : BaseView(), PostListener, SwipeRefreshLayout.OnRefreshListener{

    lateinit var presenter: HomeFragPresenter


    lateinit var homeView: View
    private var show = false
    private var top = false
    private var difficulty = false
    private var basket = false
    var isLoading = false
    var difficultyLevel = ""


    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_home, container, false)
        homeView = view

        presenter = initPresenter(HomeFragPresenter(this)) as HomeFragPresenter
        initScrollListener()

        homeView.mListRecyclerView.adapter?.notifyItemInserted(presenter.findData().size - 1)

        // Inflate the layout for this fragment
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        swipeRefreshLayout.setOnRefreshListener(this)

        swipeRefreshLayout.setOnRefreshListener {
            presenter.doRefreshData(view)
            isLoading = false
        }


        view.mHomePost.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToPostFragment2()
            homeView.findNavController().navigate(action)
        }

        view.mHomeSettings.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToSettingsFragment()
            homeView.findNavController().navigate(action)
        }

        view.mCupboardSearch.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToIngredientsFragment("home_page")
            homeView.findNavController().navigate(action)
        }

        view.mHomeDifficultyLevel.setOnClickListener {
            if(show) {
                difficulty = false
                cancelFilter()
            }else {
                difficulty = true
                top = false
                showFilter()
            }
        }

        view.mCancelFilter.setOnClickListener {
            presenter.doRefreshData(view)
            homeView.mCancelFilter.visibility = View.INVISIBLE
        }

        view.mHomeScrollBarFirstPosition.setOnClickListener {
            if (difficulty){
                difficultyLevel = getString(R.string.easy)
                presenter.doFilterDifficulty(difficultyLevel)
            }else if(top){
                presenter.doFilterTop()
            }
            view.mCancelFilter.visibility = View.VISIBLE
        }

        view.mHomeScrollBarSecondPosition.setOnClickListener {
            if (difficulty){
                difficultyLevel = getString(R.string.medium)
                presenter.doFilterDifficulty(difficultyLevel)
            }
            view.mCancelFilter.visibility = View.VISIBLE
        }

        view.mHomeScrollBarThirdPosition.setOnClickListener {
            if (difficulty){
                difficultyLevel = getString(R.string.hard)
                presenter.doFilterDifficulty(difficultyLevel)
            }
            view.mCancelFilter.visibility == View.VISIBLE
        }

        view.mHomeTopPosts.setOnClickListener {
            if(show) {
                top = false
                cancelFilter()
            }else {
                top = true
                difficulty = false
                showFilter()
            }
        }

        presenter.doFindHomeData()

        return view
    }

    override fun showInformation(homeData: ArrayList<DataModel?>) {
        try {
            val layoutManager = LinearLayoutManager(context)
            homeView.mListRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?
            homeView.mListRecyclerView.adapter = CardAdapter(homeData, presenter)
            homeView.mListRecyclerView.adapter?.notifyDataSetChanged()
            println("Data reloaded here ...........")
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    override fun initScrollListener() {
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

    override fun commentAdded(){
        Snackbar.make(homeView,"Comment Added", Snackbar.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        if(presenter.findIngredientsSearch().size != 0) {
            showInformation(presenter.findIngredientsSearch())
        }
    }

    override fun removeLoading(findData: ArrayList<DataModel?>) {

        homeView.mListRecyclerView.adapter?.notifyItemRangeInserted(findData.lastIndex,findData.size)
        isLoading = false
    }

    private fun showFilter(){
        show = true
        if(top){
            homeView.rangeBar.visibility = View.GONE
            homeView.filterbyItem.visibility = View.GONE
            homeView.horizontalScrollBar.visibility = View.VISIBLE
            homeView.mHomeScrollBarFirstPosition.text = "Top Recipes"
            homeView.mHomeScrollBarThirdPosition.visibility = View.INVISIBLE
            homeView.mHomeScrollBarSecondPosition.visibility = View.INVISIBLE
        }else if(difficulty){
            homeView.rangeBar.visibility = View.GONE
            homeView.filterbyItem.visibility = View.GONE
            homeView.horizontalScrollBar.visibility = View.VISIBLE
            homeView.mHomeScrollBarThirdPosition.visibility = View.VISIBLE
            homeView.mHomeScrollBarSecondPosition.visibility = View.VISIBLE

            homeView.mHomeScrollBarFirstPosition.text = getString(R.string.easy)
            homeView.mHomeScrollBarSecondPosition.text = getString(R.string.medium)
            homeView.mHomeScrollBarThirdPosition.text = getString(R.string.hard)
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
