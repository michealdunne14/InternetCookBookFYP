package com.example.internetcookbook.makefood

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ImageAdapter
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.adapter.MakeAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.PostModel
import kotlinx.android.synthetic.main.fragment_make.*
import kotlinx.android.synthetic.main.fragment_make.view.*

class MakeFragmentView : BaseView() {

    lateinit var presenter: MakeFragmentPresenter

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
        presenter = initPresenter(MakeFragmentPresenter(this)) as MakeFragmentPresenter
        val postModel = MakeFragmentViewArgs.fromBundle(arguments!!).postModel

        val layoutManager = LinearLayoutManager(context)
        view.mMakeIngredients.layoutManager = layoutManager as RecyclerView.LayoutManager?
        val makeLayoutManager = LinearLayoutManager(context)
        view.mMakeList.layoutManager = makeLayoutManager as RecyclerView.LayoutManager?
        makeView = view
        makeView.mMakeName.text = postModel.title
        makeView.mMakeDescription.text = postModel.description
        doFindImages(postModel.images)
        hideAndShowToolbarButtons()

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


    fun hideAndShowToolbarButtons(){
        makeView.mReturnButton.setOnClickListener {
            val action = MakeFragmentViewDirections.actionMakeFragmentToPagerFragment()
            makeView.findNavController().navigate(action)
        }
        makeView.mRemoveName.setOnClickListener {
                if (makeView.mMakeName.visibility == View.GONE){
                    makeView.mMakeName.visibility = View.VISIBLE
                }else {
                    makeView.mMakeName.visibility = View.GONE
                }
            }
        makeView.mRemoveDescription.setOnClickListener {
                if (makeView.mMakeDescription.visibility == View.GONE){
                    makeView.mMakeDescription.visibility = View.VISIBLE
                }else {
                    makeView.mMakeDescription.visibility = View.GONE
                }
            }
        makeView.mRemoveCreatedBy.setOnClickListener {
                if (makeView.mMakeCreatedBy.visibility == View.GONE){
                    makeView.mMakeCreatedBy.visibility = View.VISIBLE
                }else {
                    makeView.mMakeCreatedBy.visibility = View.GONE
                }
            }
        makeView.mRemoveNumberOfPeople.setOnClickListener {
                if (makeView.mMakeAmountofPeople.visibility == View.GONE){
                    makeView.mMakeAmountofPeople.visibility = View.VISIBLE
                }else {
                    makeView.mMakeAmountofPeople.visibility = View.GONE
                }
            }
        makeView.mRemoveTimeToMake.setOnClickListener {
                if (makeView.mMakeTimeToCreate.visibility == View.GONE){
                    makeView.mMakeTimeToCreate.visibility = View.VISIBLE
                }else {
                    makeView.mMakeTimeToCreate.visibility = View.GONE
                }
            }
        makeView.mRemoveIngredients.setOnClickListener {
                if (makeView.mMakeIngredients.visibility == View.GONE){
                    makeView.mMakeIngredients.visibility = View.VISIBLE
                }else {
                    makeView.mMakeIngredients.visibility = View.GONE
                }
            }
    }

}
