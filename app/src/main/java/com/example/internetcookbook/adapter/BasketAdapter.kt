package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.models.FoodModel
import kotlinx.android.synthetic.main.basket_list.view.*
import kotlinx.android.synthetic.main.ingredients_list.view.*

class BasketAdapter(private var basketItem: ArrayList<FoodModel>) : RecyclerView.Adapter<BasketAdapter.MainHolder>() {

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
    override fun getItemCount(): Int = basketItem.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val postModel = basketItem[holder.adapterPosition]
        holder.bind(postModel)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(foodModel: FoodModel) {
            itemView.mBasketName.text = foodModel.foodName
        }
    }
}