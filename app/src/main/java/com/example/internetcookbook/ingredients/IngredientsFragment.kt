package com.example.internetcookbook.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.models.FoodModel
import kotlinx.android.synthetic.main.fragment_ingredients.view.*

class IngredientsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_ingredients, container, false)
        val foodModelArrayList = ArrayList<FoodModel>()

        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)


        view.mIngredientsRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?


//        foodModelArrayList.add(FoodModel("Food"))
//        foodModelArrayList.add(FoodModel("Food"))
//        view.mIngredientsRecyclerView.adapter = IngredientsAdapter(foodModelArrayList)
//        view.mIngredientsRecyclerView.adapter?.notifyDataSetChanged()

        return view
    }

}
