package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.models.FoodModel
import kotlinx.android.synthetic.main.ingredients_list.view.*

class IngredientsAdapter(private var food: ArrayList<FoodModel>) : RecyclerView.Adapter<IngredientsAdapter.MainHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
            return MainHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.ingredients_list,
                    parent,
                    false
                )
            )
        }

        //  Item Count
        override fun getItemCount(): Int = food.size

        override fun onBindViewHolder(holder: MainHolder, position: Int) {
            val postModel = food[holder.adapterPosition]
            holder.bind(postModel)
        }

        class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            fun bind(foodModel: FoodModel) {
                itemView.mIngredientsName.text = foodModel.name
                itemView.mSelectedIngredient.setOnClickListener {
                    
                }
            }
        }


}