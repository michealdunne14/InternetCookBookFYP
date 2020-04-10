package com.example.internetcookbook.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ImageAdapter
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.adapter.MakeAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.PostModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_post.view.*
import kotlinx.android.synthetic.main.horizontalscrollbar.view.*
import org.jetbrains.anko.AnkoLogger

var selectedDifficulty = ""

class PostFragmentView : BaseView(),AnkoLogger  {

    lateinit var presenter: PostFragmentPresenter
    lateinit var postView: View
    var postModel = PostModel()
    var personalPost = false
    var methodStepsArrayList = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        presenter = initPresenter(PostFragmentPresenter(this)) as PostFragmentPresenter
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        postView = view

        val layoutManager = LinearLayoutManager(context)
        view.mPostIngredientRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?

        view.mHomeScrollBarFirstPosition.text = getString(R.string.easy)
        view.mHomeScrollBarSecondPosition.text = getString(R.string.medium)
        view.mHomeScrollBarThirdPosition.text = getString(R.string.hard)
        view.mHomeScrollBarFirstPosition.setBackgroundColor(0)
        view.mHomeScrollBarSecondPosition.setBackgroundColor(0)
        view.mHomeScrollBarThirdPosition.setBackgroundColor(0)

        view.mHomeScrollBarFirstPosition.setOnClickListener {
            selectedDifficulty = getString(R.string.easy)
            view.mHomeScrollBarFirstPosition.setBackgroundColor(resources.getColor(R.color.colorBlue))
            view.mHomeScrollBarSecondPosition.setBackgroundColor(0)
            view.mHomeScrollBarThirdPosition.setBackgroundColor(0)
        }

        view.mHomeScrollBarSecondPosition.setOnClickListener {
            selectedDifficulty = getString(R.string.medium)
            view.mHomeScrollBarSecondPosition.setBackgroundColor(resources.getColor(R.color.colorBlue))
            view.mHomeScrollBarFirstPosition.setBackgroundColor(0)
            view.mHomeScrollBarThirdPosition.setBackgroundColor(0)
        }

        view.mHomeScrollBarThirdPosition.setOnClickListener {
            selectedDifficulty = getString(R.string.hard)
            view.mHomeScrollBarThirdPosition.setBackgroundColor(resources.getColor(R.color.colorBlue))
            view.mHomeScrollBarFirstPosition.setBackgroundColor(0)
            view.mHomeScrollBarSecondPosition.setBackgroundColor(0)
        }


        if(presenter.ingredientsAddToRecipe().isNotEmpty()){
            showBasket(presenter.ingredientsAddToRecipe())

        }else{
            view.mPostIngredientRecyclerView.visibility = View.GONE
        }

        view.mPostButton.setOnClickListener {
            postModel.title = view.mPostTitle.text.toString()
            postModel.description = view.mPostDescription.text.toString()
            postModel.difficulty = selectedDifficulty
            if(postModel.title.isNotEmpty() && postModel.description.isNotEmpty() && postModel.difficulty.isNotEmpty() && presenter.ingredientsAddToRecipe().size > 0 && methodStepsArrayList.size > 0) {
                presenter.doPostRecipe(postModel, methodStepsArrayList)
            }else{
                Snackbar.make(postView,"Fill in all fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        view.mReturnButton.setOnClickListener {
            returnToPager()
        }

        view.mIngredientsButton.setOnClickListener {
            val action = PostFragmentViewDirections.actionPostFragment2ToIngredientsFragment("")
            view.findNavController().navigate(action)
        }

//        view.mPersonalPost.setOnClickListener {
//            if (!personalPost) {
//                personalPost = true
//                view.mIngredientsButton.visibility = View.GONE
//                view.mPostIngredientRecyclerView.visibility = View.GONE
//            }else{
//                personalPost = false
//                view.mIngredientsButton.visibility = View.VISIBLE
//                view.mPostIngredientRecyclerView.visibility = View.VISIBLE
//            }
//        }

        view.mPostAddMethod.setOnClickListener {
            methodStepsArrayList.add(view.mPostMethodStep.text.toString())
            view.mPostMethodStep.setText("")
            showMethod(methodStepsArrayList)
        }



        view.mAddImage.setOnClickListener {
            presenter.doSelectImage()
        }

        return view
    }


    //  add images to the Hillfort using the adapter
    override fun addImages(listofImages: ArrayList<String>){
        val viewPager = postView.findViewById<ViewPager>(R.id.mFragmentPager)
        val adapter = ImageAdapter(postView.context, listofImages)
        viewPager.adapter = adapter
    }

    override fun showBasket(listofBasket: ArrayList<FoodMasterModel>){
        val layoutManager = LinearLayoutManager(context)
        postView.mPostIngredientRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?
        postView.mPostIngredientRecyclerView.adapter = IngredientsAdapter(
            listofBasket,
            presenter.doCurrentUser(),
            "post",
            presenter,
            activity
        )
        postView.mPostIngredientRecyclerView.adapter?.notifyDataSetChanged()
    }

    fun showMethod(listofMethods: ArrayList<String>){
        val layoutManager = LinearLayoutManager(context)
        postView.mPostMethodRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?
        postView.mPostMethodRecyclerView.adapter = MakeAdapter(listofMethods)
        postView.mPostMethodRecyclerView.adapter?.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            presenter.doActivityResult(requestCode,resultCode,data,postView.context)
        }
    }

    override fun returnToPager(){
        postView.findNavController().navigateUp()
    }


    override fun onResume() {
        super.onResume()
        addImages(presenter.listofImages())
    }
}
