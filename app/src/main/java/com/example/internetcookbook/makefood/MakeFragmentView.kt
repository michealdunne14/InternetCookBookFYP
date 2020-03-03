package com.example.internetcookbook.makefood

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.BitmapCardAdapter
import com.example.internetcookbook.adapter.MakeAdapterWithModel
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.readBit64ImageArrayList
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.PostModel
import kotlinx.android.synthetic.main.fragment_make.*
import kotlinx.android.synthetic.main.fragment_make.view.mCardIngredients
import kotlinx.android.synthetic.main.fragment_make.view.mMakeButton
import kotlinx.android.synthetic.main.fragment_make.view.mMakeCreatedBy
import kotlinx.android.synthetic.main.fragment_make.view.mMakeDescription
import kotlinx.android.synthetic.main.fragment_make.view.mMakeList
import kotlinx.android.synthetic.main.fragment_make.view.mMakeName
import kotlinx.android.synthetic.main.fragment_make.view.mMakeTimeToCreate
import kotlinx.android.synthetic.main.fragment_make.view.mRemoveCreatedBy
import kotlinx.android.synthetic.main.fragment_make.view.mRemoveDescription
import kotlinx.android.synthetic.main.fragment_make.view.mRemoveIngredients
import kotlinx.android.synthetic.main.fragment_make.view.mRemoveName
import kotlinx.android.synthetic.main.fragment_make.view.mRemoveTimeToMake
import kotlinx.android.synthetic.main.fragment_make.view.mReturnButton
import kotlinx.android.synthetic.main.fragment_make.view.mSelectPeopleNumbSpinner

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
        val postModel = MakeFragmentViewArgs.fromBundle(arguments!!).dataModel.post
        val images = MakeFragmentViewArgs.fromBundle(arguments!!).dataModel

        val layoutManager = LinearLayoutManager(context)
        view.mCardIngredients.layoutManager = layoutManager
        val makeLayoutManager = LinearLayoutManager(context)
        view.mMakeList.layoutManager = makeLayoutManager
        makeView = view
        makeView.mMakeName.text = postModel.title
        makeView.mMakeDescription.text = postModel.description
        val bitmapImages = readBit64ImageArrayList(images)
        doFindImages(bitmapImages)
        hideAndShowToolbarButtons()

//        foodModelArrayList.add(FoodModel("Food"))
//        foodModelArrayList.add(FoodModel("Food"))


//        view.mCardIngredients.adapter = IngredientsAdapter(foodModelArrayList)
//        view.mCardIngredients.adapter?.notifyDataSetChanged()

        postModel.method.reverse()
        view.mMakeList.adapter = MakeAdapterWithModel(postModel.method)
        view.mMakeList.adapter?.notifyDataSetChanged()

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            R.array.number_of_people,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            view.mSelectPeopleNumbSpinner.adapter = adapter
        }


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
        makeView.mRemoveTimeToMake.setOnClickListener {
                if (makeView.mMakeTimeToCreate.visibility == View.GONE){
                    makeView.mMakeTimeToCreate.visibility = View.VISIBLE
                }else {
                    makeView.mMakeTimeToCreate.visibility = View.GONE
                }
            }
        makeView.mRemoveIngredients.setOnClickListener {
                if (makeView.mCardIngredients.visibility == View.GONE){
                    makeView.mCardIngredients.visibility = View.VISIBLE
                }else {
                    makeView.mCardIngredients.visibility = View.GONE
                }
            }
    }

}
