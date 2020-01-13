package com.example.internetcookbook.makefood

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ImageAdapter
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.adapter.MakeAdapter
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_ingredients.view.*
import kotlinx.android.synthetic.main.fragment_make.*
import kotlinx.android.synthetic.main.fragment_make.view.*

class MakeFragment : Fragment() {

    val postModel = PostModel()
    private var show = false
    lateinit var makeView: View
    val foodModelArrayList = ArrayList<FoodModel>()
    val postModelArrayList = ArrayList<PostModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_make, container, false)
//        val args = SecondFragmentArgs.fromBundle(arguments).users
        val postModel = MakeFragmentArgs.fromBundle(arguments!!).postModel

        val layoutManager = LinearLayoutManager(context)
        view.mMakeIngredients.layoutManager = layoutManager as RecyclerView.LayoutManager?
        val makeLayoutManager = LinearLayoutManager(context)
        view.mMakeList.layoutManager = makeLayoutManager as RecyclerView.LayoutManager?
        makeView = view
        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbarMake)
        toolbar.title = ""
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        makeView.mMakeName.text = postModel.title
        makeView.mMakeDescription.text = postModel.description
        doFindImages(postModel.images)

        foodModelArrayList.add(FoodModel("Food"))
        foodModelArrayList.add(FoodModel("Food"))
        postModelArrayList.add(PostModel("hello","sjhdfkjs",postModel.images))
        postModelArrayList.add(PostModel("hello","sjhdfkjs",postModel.images))
        postModelArrayList.add(PostModel("hello","sjhdfkjs",postModel.images))
        postModelArrayList.add(PostModel("hello","sjhdfkjs",postModel.images))
        postModelArrayList.add(PostModel("hello","sjhdfkjs",postModel.images))


        view.mMakeIngredients.adapter = IngredientsAdapter(foodModelArrayList)
        view.mMakeIngredients.adapter?.notifyDataSetChanged()
        view.mMakeList.adapter = MakeAdapter(postModelArrayList)
        view.mMakeList.adapter?.notifyDataSetChanged()


        makeView.mMakeButton.setOnClickListener {
            if(show)
                cancelMake()
            else
                showMake()
        }
        return view
    }

    private fun showMake(){
        show = true
        makeView.mMakeButton.text = "Cancel"

        val constraintSet = ConstraintSet()
        constraintSet.clone(makeView.context, R.layout.fragment_make_show)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 1000

        TransitionManager.beginDelayedTransition(constraint, transition)
        constraintSet.applyTo(constraint) //here constraint is the name of view to which we are applying the constraintSet
    }

    private fun cancelMake(){
        show = false
        makeView.mMakeButton.text = "Make"
        val constraintSet = ConstraintSet()
        constraintSet.clone(makeView.context, R.layout.fragment_make)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 1200

        TransitionManager.beginDelayedTransition(constraint, transition)
        constraintSet.applyTo(constraint)  //here constraint is the name of view to which we are applying the constraintSet
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.select_options_make, menu)
        super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mRemoveName -> {
                if (makeView.mMakeName.visibility == View.GONE){
                    makeView.mMakeName.visibility = View.VISIBLE
                }else {
                    makeView.mMakeName.visibility = View.GONE
                }
            }
            R.id.mRemoveDescription -> {
                if (makeView.mMakeDescription.visibility == View.GONE){
                    makeView.mMakeDescription.visibility = View.VISIBLE
                }else {
                    makeView.mMakeDescription.visibility = View.GONE
                }
            }
            R.id.mRemoveCreatedBy -> {
                if (makeView.mMakeCreatedBy.visibility == View.GONE){
                    makeView.mMakeCreatedBy.visibility = View.VISIBLE
                }else {
                    makeView.mMakeCreatedBy.visibility = View.GONE
                }
            }
            R.id.mRemoveNumberOfPeople -> {
                if (makeView.mMakeAmountofPeople.visibility == View.GONE){
                    makeView.mMakeAmountofPeople.visibility = View.VISIBLE
                }else {
                    makeView.mMakeAmountofPeople.visibility = View.GONE
                }
            }
            R.id.mRemoveTimeToMake -> {
                if (makeView.mMakeTimeToCreate.visibility == View.GONE){
                    makeView.mMakeTimeToCreate.visibility = View.VISIBLE
                }else {
                    makeView.mMakeTimeToCreate.visibility = View.GONE
                }
            }
            R.id.mRemoveIngredients -> {
                if (makeView.mMakeIngredients.visibility == View.GONE){
                    makeView.mMakeIngredients.visibility = View.VISIBLE
                }else {
                    makeView.mMakeIngredients.visibility = View.GONE
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun doFindImages(images: ArrayList<String>) {
        val viewPager = makeView.findViewById<ViewPager>(R.id.mMakeImage)
        val adapter = ImageAdapter(makeView.context, images)
        viewPager.adapter = adapter
    }

}
