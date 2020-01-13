package com.example.internetcookbook.fragmentview

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archaeologicalfieldwork.adapter.CardAdapter
import com.example.archaeologicalfieldwork.adapter.PostListener
import com.example.internetcookbook.pager.PagerFragmentView
import com.example.internetcookbook.R
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.filterbyItem
import kotlinx.android.synthetic.main.fragment_home.view.mHomeBasket
import kotlinx.android.synthetic.main.fragment_home.view.mHomeItem
import kotlinx.android.synthetic.main.fragment_home.view.mHomePost
import kotlinx.android.synthetic.main.fragment_home.view.mHomeSettings
import kotlinx.android.synthetic.main.fragment_home.view.mHomeTime
import kotlinx.android.synthetic.main.fragment_home.view.mHomeTopPosts
import kotlinx.android.synthetic.main.fragment_home.view.mListRecyclerView
import kotlinx.android.synthetic.main.fragment_home.view.rangeBar

class HomeFragmentView : Fragment(), PostListener {

    lateinit var homeView: View
    companion object {

        private const val CALLBACK_FUNC = "callback"

        fun newInstance(callback: PagerFragmentView.ViewCreatedListener): HomeFragmentView {
            return HomeFragmentView().apply {
                arguments = bundleOf(CALLBACK_FUNC to callback)
            }
        }
    }

    private lateinit var callback: PagerFragmentView.ViewCreatedListener
    private var show = false
    private var time = false
    private var item = false
    private var top = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializable(CALLBACK_FUNC)?.let {
            callback = it as PagerFragmentView.ViewCreatedListener
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_home, container, false)
        val postModelList = ArrayList<PostModel>()
        homeView = view

        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)

        callback.invoke()
        view.mListRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?


        view.mHomePost.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToPostFragment2()
            homeView.findNavController().navigate(action)
        }

        view.mHomeSettings.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToSettingsFragment()
            homeView.findNavController().navigate(action)
        }

        view.mHomeBasket.setOnClickListener {

        }

        view.mHomeTime.setOnClickListener {
            if(show) {
                time = false
                cancelFilter()
            }else {
                time = true
                top = false
                item = false
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
                showFilter()
            }
        }

        val user = UserModel()
//        postModelList.add(PostModel("Burger","Burger","https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg"))
        val arrayList = ArrayList<String>()
        arrayList.add("https://eatforum.org/content/uploads/2018/05/table_with_food_top_view_900x700.jpg")
        arrayList.add("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg")

        postModelList.add(PostModel("Food","food",arrayList))
        view.mListRecyclerView.adapter = CardAdapter(postModelList, this, user)
        view.mListRecyclerView.adapter?.notifyDataSetChanged()
        return view
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

}
