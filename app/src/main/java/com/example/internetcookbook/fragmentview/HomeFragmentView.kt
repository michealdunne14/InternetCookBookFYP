package com.example.internetcookbook.fragmentview

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import kotlinx.android.synthetic.main.fragment_make.*
import kotlinx.android.synthetic.main.fragment_make.view.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializable(CALLBACK_FUNC)?.let {
            callback = it as PagerFragmentView.ViewCreatedListener
        }
        setHasOptionsMenu(true)
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
        val toolbar: Toolbar = view.findViewById(R.id.toolbarAdd)
        toolbar.title = ""
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        view.mListRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?



        val user = UserModel()
//        postModelList.add(PostModel("Burger","Burger","https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg"))
        val arrayList = ArrayList<String>()
        arrayList.add("https://eatforum.org/content/uploads/2018/05/table_with_food_top_view_900x700.jpg")
        arrayList.add("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg")

        postModelList.add(PostModel("Food","food",arrayList))
        view.mListRecyclerView.adapter = CardAdapter(postModelList, this, user)
        view.mListRecyclerView.adapter?.notifyDataSetChanged()
//        showPosts()C
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mNavSettings -> {
                val action = PagerFragmentViewDirections.actionPagerFragmentToSettingsFragment()
                homeView.findNavController().navigate(action)
            }
            R.id.mNavBasket -> {
                if(show)
                    cancelMake()
                else
                    showMake()
            }
            R.id.mNavPost -> {
                val action = PagerFragmentViewDirections.actionPagerFragmentToPostFragment2()
                homeView.findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showMake(){
        show = true

        val constraintSet = ConstraintSet()
        constraintSet.clone(homeView.context, R.layout.fragment_home_filter)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 1000

        TransitionManager.beginDelayedTransition(homeConstraint, transition)
        constraintSet.applyTo(homeConstraint) //here constraint is the name of view to which we are applying the constraintSet
    }

    private fun cancelMake(){
        show = false
        val constraintSet = ConstraintSet()
        constraintSet.clone(homeView.context, R.layout.fragment_home_filter)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 1000

        TransitionManager.beginDelayedTransition(homeConstraint, transition)
        constraintSet.applyTo(homeConstraint)  //here constraint is the name of view to which we are applying the constraintSet
    }

//    //  Show Hillforts
//    fun showPosts(/*postModelList: ArrayList<PostModel>, user: UserModel*/) {
//        val postModelList = ArrayList<PostModel>()
//        val user = UserModel()
//        postModel.name = "Test"
//        postModelList.add(postModel)
//        mListRecyclerView.adapter = CardAdapter(postModelList, this, user)
//        mListRecyclerView.adapter?.notifyDataSetChanged()
//    }

    override fun onPostClick(hillfort: PostModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
