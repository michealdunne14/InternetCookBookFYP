package com.example.internetcookbook.post

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.PostModel
import kotlinx.android.synthetic.main.fragment_ingredients.view.*
import kotlinx.android.synthetic.main.fragment_post.view.*
import org.jetbrains.anko.AnkoLogger

class PostFragmentView : BaseView(),AnkoLogger {

    lateinit var presenter: PostFragmentPresenter
    lateinit var postView: View
    var postModel = PostModel()
    var foodModelArrayList = ArrayList<FoodModel>()
    var personalPost = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        presenter = initPresenter(PostFragmentPresenter(this)) as PostFragmentPresenter
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        postView = view


        if(foodModelArrayList.size > 0){
            showIngredients(foodModelArrayList)
        }else{
            view.mPostFoodRecyclerView.visibility = View.GONE
        }

        view.mPostButton.setOnClickListener {
            postModel.title = view.mPostTitle.text.toString()
            postModel.description = view.mPostDescription.text.toString()
            presenter.doPostRecipe(postModel)
        }

        view.mReturnButton.setOnClickListener {
            !postView.findNavController().navigateUp()
        }

        view.mIngredientsButton.setOnClickListener {
            val action = PostFragmentViewDirections.actionPostFragment2ToIngredientsFragment()
            view.findNavController().navigate(action)
        }

        view.mPersonalPost.setOnClickListener {
            if (!personalPost) {
                personalPost = true
                view.mIngredientsButton.visibility = View.GONE
                view.mPostFoodRecyclerView.visibility = View.GONE
            }else{
                personalPost = false
                view.mIngredientsButton.visibility = View.VISIBLE
                view.mPostFoodRecyclerView.visibility = View.VISIBLE
            }

        }

        view.mAddImage.setOnClickListener {
            presenter.doSelectImage(this)
        }

        return view
    }


    //  add images to the Hillfort using the adapter
    override fun addImages(listofImages: ArrayList<String>){
        val viewPager = postView.findViewById<ViewPager>(R.id.mFragmentPager)
        val adapter = ImageAdapter(postView.context, listofImages)
        viewPager.adapter = adapter
    }

    override fun showIngredients(listofIngredients: ArrayList<FoodModel>){
        val layoutManager = LinearLayoutManager(context)
        postView.mIngredientsRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?
        postView.mIngredientsRecyclerView.adapter = IngredientsAdapter(listofIngredients)
        postView.mIngredientsRecyclerView.adapter?.notifyDataSetChanged()

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(data != null){
//            presenter.doActivityResult(requestCode,resultCode,data,postView.context)
//        }
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            try {
                val bundle = data!!.extras
                val bitmap = bundle!!.getParcelable<Bitmap>("data")
//                img_user.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}
