package com.example.internetcookbook.foodcreate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentpresenter.saveDate
import com.example.internetcookbook.models.FoodModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_food_create.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FoodCreateView : BaseView(){

    lateinit var presenter: FoodCreatePresenter
    var date = String()
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

//       sets the date of the calender when changed.
        foodCreateView.mFoodCreateExpirationDate.setOnDateChangeListener(CalendarView.OnDateChangeListener(){
                view, year, month, dayOfMonth ->
            date = "$dayOfMonth/$month/$year"
        })

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
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yy")
            val date1: Date = dateFormat.parse(saveDate)!!
            val dateFormat2: DateFormat = SimpleDateFormat("dd/MM/yy")
            val date2: Date = dateFormat2.parse(date)!!
            val days = daysBetween(date2,date1)
            if(foodCreateView.mFoodCreateName.text.toString().isNotEmpty() && foodCreateView.mFoodCreatePrice.text.isNotEmpty() && foodCreateView.mFoodCreateShop.text.toString().isNotEmpty()) {
                presenter.doAddFood(
                    FoodModel(
                        "",
                        foodCreateView.mFoodCreateName.text.toString(),
                        foodCreateView.mFoodCreatePrice.text.toString().toDouble(),
                        foodCreateView.mFoodCreateShop.text.toString(),
                        0,
                        0,
                        days,
                        0,
                        "",
                        0
                    )
                )
            }else{
                Snackbar.make(foodCreateView,"Fill in all fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        foodCreateView.mReturnButton.setOnClickListener {
            naviateUp()
        }

        return view
    }

    fun daysBetween(d1: Date, d2: Date): Long {
        return ((d2.time - d1.time) / (1000 * 60 * 60 * 24))
    }
    override fun detailsIncorrect(){
        Snackbar.make(foodCreateView,"Error", Snackbar.LENGTH_SHORT).show()
    }

    override fun naviateUp(){
        presenter.doFoodCreatePageUpdate()
        foodCreateView.findNavController().navigateUp()
    }

}
