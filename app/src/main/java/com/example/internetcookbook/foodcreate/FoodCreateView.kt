package com.example.internetcookbook.foodcreate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_food_create.view.*

class FoodCreateView : BaseView(){

    lateinit var presenter: FoodCreatePresenter

    lateinit var foodCreateView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        presenter = initPresenter(
            FoodCreatePresenter(
                this
            )
        ) as FoodCreatePresenter
        val view=  inflater.inflate(R.layout.fragment_food_create, container, false)
        foodCreateView = view

        if (arguments != null) {
            if (arguments!!.containsKey("fooditem")){
                val foodItem = FoodCreateViewArgs.fromBundle(
                    arguments!!
                ).fooditem
                val foodShop = FoodCreateViewArgs.fromBundle(
                    arguments!!
                ).foodshop
                foodCreateView.mFoodCreateName.setText(foodItem)
                foodCreateView.mFoodCreateShop.text = foodShop
            }
        }

        if (presenter.doFindImage() != null){
            foodCreateView.mFoodCreateImage.setImageBitmap(presenter.doFindImage())
        }

        foodCreateView.mFoodCreateImage.setOnClickListener {
            val action =
                FoodCreateViewDirections.actionFoodCreateViewToCameraFragmentView(
                    "food_create"
                )
            view.findNavController().navigate(action)
        }

        foodCreateView.mFoodCreateAdd.setOnClickListener {
            presenter.doAddFood(FoodModel("",foodCreateView.mFoodCreateName.text.toString(),foodCreateView.mFoodCreatePrice.text.toString().toDouble(),foodCreateView.mFoodCreateShop.text.toString(),0,
            0,0,0,"",""))
        }

        foodCreateView.mReturnButton.setOnClickListener {
            naviateUp()
        }

        return view
    }

    override fun detailsIncorrect(){
        Snackbar.make(foodCreateView,"Error", Snackbar.LENGTH_SHORT).show()
    }

    override fun naviateUp(){
        presenter.doFoodCreatePageUpdate()
        foodCreateView.findNavController().navigateUp()
    }

}
