package com.example.internetcookbook.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.register.RegisterFragmentPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_basket.view.*
import kotlinx.android.synthetic.main.fragment_ingredients.view.*

class BasketFragmentView : BaseView() {

    lateinit var presenter: BasketFragmentPresenter

    lateinit var basketView: View
    var foodModelArrayList = ArrayList<FoodModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))
        foodModelArrayList.add(FoodModel("hello"))

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_basket, container, false)
        presenter = initPresenter(BasketFragmentPresenter(this)) as BasketFragmentPresenter
        basketView = view
        showIngredients(foodModelArrayList)

        val navView: BottomNavigationView = view.findViewById(R.id.navigationView)


        return view
    }

    override fun showIngredients(listofIngredients: ArrayList<FoodModel>){
        val layoutManager = LinearLayoutManager(context)
        basketView.mBasketRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?
        basketView.mBasketRecyclerView.adapter = IngredientsAdapter(listofIngredients)
        basketView.mBasketRecyclerView.adapter?.notifyDataSetChanged()

    }

    override fun onDestroy() {
        super.onDestroy()
        foodModelArrayList.clear()

    }
}
