package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.models.FoodModel
import kotlinx.android.synthetic.main.listeachitem.view.*

class ReceiptListAdapter(
    private var foodItems: List<FoodModel>
): RecyclerView.Adapter<ReceiptListAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.listeachitem,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = foodItems.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val food = foodItems[holder.adapterPosition]
        holder.bind(food)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(foodModel: FoodModel){
            itemView.mFoodItemText.setText(foodModel.foodName)
        }
    }
}