package com.example.internetcookbook.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_basket.view.*
import kotlinx.android.synthetic.main.fragment_item.*

class ItemFragmentView : BaseView() {

    lateinit var presenter: ItemFragmentPresenter
    var foodModelArrayList = ArrayList<FoodModel>()


    lateinit var itemView: View

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
        val view=  inflater.inflate(R.layout.fragment_item, container, false)
        presenter = initPresenter(ItemFragmentPresenter(this)) as ItemFragmentPresenter
        itemView = view

        val navView: BottomNavigationView = view.findViewById(R.id.itemBottomNav)
        showIngredients(foodModelArrayList)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        return view
    }

    //  Navigating to the correct selected Item
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.mNavCupboard -> {
                fragmentCart.visibility = View.VISIBLE
                fragmentBasket.visibility = View.INVISIBLE
                fragmentFollowers.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.mNavBasket -> {
                fragmentBasket.visibility = View.VISIBLE
                fragmentCart.visibility = View.INVISIBLE
                fragmentFollowers.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.mNavFollowing -> {
                fragmentBasket.visibility = View.INVISIBLE
                fragmentCart.visibility = View.INVISIBLE
                fragmentFollowers.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun showIngredients(listofIngredients: ArrayList<FoodModel>){
        val layoutManager = LinearLayoutManager(context)
        itemView.mBasketRecyclerView.layoutManager = layoutManager
        itemView.mBasketRecyclerView.adapter = IngredientsAdapter(listofIngredients)
        itemView.mBasketRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        foodModelArrayList.clear()

    }

}
