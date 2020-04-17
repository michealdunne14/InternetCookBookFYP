package com.example.internetcookbook.makefood

import android.graphics.Bitmap
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
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.BitmapCardAdapter
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.adapter.MakeAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.readBit64ImageArrayList
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserMasterModel
import kotlinx.android.synthetic.main.fragment_make.*
import kotlinx.android.synthetic.main.fragment_make.view.*
import kotlinx.android.synthetic.main.fragment_make.view.mReturnButton

class MakeFragmentView : BaseView() {

    lateinit var presenter: MakeFragmentPresenter

    val postModel = PostModel()
    private var show = false
    lateinit var makeView: View

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

        val postModel = MakeFragmentViewArgs.fromBundle(arguments!!).dataModel.post
        val images = MakeFragmentViewArgs.fromBundle(arguments!!).dataModel

        makeView = view

        val layoutManager = LinearLayoutManager(context)
        val makeLayoutManager = LinearLayoutManager(context)

        view.mIngredientsRecyclerView.layoutManager = layoutManager
        view.mMakeList.layoutManager = makeLayoutManager

        presenter.findIngredients(postModel)
        presenter.findUser(postModel)

        val methodArrayList = ArrayList<String>()
        for (method in postModel.method){
            methodArrayList.add(method.methodStep)
        }
        makeView.mMakeList.adapter = MakeAdapter(methodArrayList)
        makeView.mMakeList.adapter?.notifyDataSetChanged()

        makeView.mMakeName.text = postModel.title
        makeView.mMakeDescription.text = postModel.description
        makeView.mMakeTimeToCreate.text = "Time to Create is ${postModel.completionTime} minutes"
        makeView.mMakeDifficulty.text = "Level of Difficulty: ${postModel.difficulty}"

        val bitmapImages = readBit64ImageArrayList(images)
        doFindImages(bitmapImages)
        hideAndShowToolbarButtons()


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


    fun doFindImages(images: ArrayList<Bitmap>) {
        val viewPager = makeView.findViewById<ViewPager>(R.id.mMakeImage)
        val adapter = BitmapCardAdapter(makeView.context, images)
        viewPager.adapter = adapter
    }

    override fun makeUser(makeName: UserMasterModel) {
        makeView.mMakeCreatedBy.text = "Created By ${makeName.user.name}"
    }

    override fun ingredientsRecyclerView(searchedIngredients: ArrayList<FoodMasterModel>){
        makeView.mIngredientsRecyclerView.adapter = IngredientsAdapter(
            searchedIngredients,
            presenter.doCurrentUser(),
            "make",
            presenter,
            activity
        )
        makeView.mIngredientsRecyclerView.adapter?.notifyDataSetChanged()
    }


    private fun hideAndShowToolbarButtons(){
        makeView.mReturnButton.setOnClickListener {
            makeView.findNavController().navigateUp()
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
        makeView.mRemoveTimeToMake.setOnClickListener {
                if (makeView.mMakeTimeToCreate.visibility == View.GONE){
                    makeView.mMakeTimeToCreate.visibility = View.VISIBLE
                }else {
                    makeView.mMakeTimeToCreate.visibility = View.GONE
                }
            }
        makeView.mRemoveIngredients.setOnClickListener {
                if (makeView.mIngredientsRecyclerView.visibility == View.GONE){
                    makeView.mIngredientsRecyclerView.visibility = View.VISIBLE
                }else {
                    makeView.mIngredientsRecyclerView.visibility = View.GONE
                }
            }
    }

}
