package com.example.internetcookbook.makefood

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ImageAdapter
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.adapter.MakeAdapter
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.PostModel
import kotlinx.android.synthetic.main.fragment_ingredients.view.*
import kotlinx.android.synthetic.main.fragment_make.*
import kotlinx.android.synthetic.main.fragment_make.view.*

class MakeFragment : Fragment() {

    val postModel = PostModel()
    private var show = false
    lateinit var makeView: View
    val foodModelArrayList = ArrayList<FoodModel>()
    val postModelArrayList = ArrayList<PostModel>()


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

    fun doFindImages(images: ArrayList<String>) {
        val viewPager = makeView.findViewById<ViewPager>(R.id.mMakeImage)
        val adapter = ImageAdapter(makeView.context, images)
        viewPager.adapter = adapter
    }

}
