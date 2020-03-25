package com.example.internetcookbook.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import kotlinx.android.synthetic.main.fragment_post.view.*
import org.jetbrains.anko.AnkoLogger

class PostFragmentView : BaseView(),AnkoLogger,AdapterView.OnItemSelectedListener  {

    lateinit var presenter: PostFragmentPresenter
    lateinit var postView: View
    var postModel = PostModel()
    var personalPost = false
    var methodStepsArrayList = ArrayList<String>()
    var selectedDifficulty = ""


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


        if(presenter.ingredientsAddToRecipe().isNotEmpty()){
            showBasket(presenter.ingredientsAddToRecipe())

        }else{
            view.mPostIngredientRecyclerView.visibility = View.GONE
        }

        view.mPostButton.setOnClickListener {
            postModel.title = view.mPostTitle.text.toString()
            postModel.description = view.mPostDescription.text.toString()
            postModel.difficulty = selectedDifficulty
            presenter.doPostRecipe(postModel,methodStepsArrayList)
        }


        ArrayAdapter.createFromResource(
            context!!,
            R.array.difficulty_level,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            view.mSelectDifficulty.adapter = adapter
        }

        view.mSelectDifficulty.onItemSelectedListener = this


        view.mReturnButton.setOnClickListener {
            returnToPager()
        }

        view.mIngredientsButton.setOnClickListener {
            val action = PostFragmentViewDirections.actionPostFragment2ToIngredientsFragment()
            view.findNavController().navigate(action)
        }

        view.mPersonalPost.setOnClickListener {
            if (!personalPost) {
                personalPost = true
                view.mIngredientsButton.visibility = View.GONE
                view.mPostIngredientRecyclerView.visibility = View.GONE
            }else{
                personalPost = false
                view.mIngredientsButton.visibility = View.VISIBLE
                view.mPostIngredientRecyclerView.visibility = View.VISIBLE
            }
        }

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
        postView.mPostIngredientRecyclerView.adapter = IngredientsAdapter(listofBasket)
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

    override fun onNothingSelected(p0: AdapterView<*>?) {
        print(p0)
    }

    override fun onItemSelected(adapterView: AdapterView<*>, p1: View?, position: Int, p3: Long) {
        val selectedClass: String = adapterView.getItemAtPosition(position).toString()
        selectedDifficulty = selectedClass
    }

}
