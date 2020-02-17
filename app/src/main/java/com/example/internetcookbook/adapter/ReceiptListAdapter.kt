package com.example.internetcookbook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.fragmentpresenter.CameraFragmentPresenter
import com.example.internetcookbook.models.FoodModel
import kotlinx.android.synthetic.main.listeachitem.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread

class ReceiptListAdapter(
    private var foodItems: List<FoodModel>,
    private var presenter: CameraFragmentPresenter
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
        holder.bind(food,presenter)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(
            foodModel: FoodModel,
            presenter: CameraFragmentPresenter
        ){
            itemView.mFoodItemText.setText(foodModel.name)
            doAsync {
                val result = presenter.searchItems(foodModel.name)
                uiThread {
                    //                    Progress Bar here
                }
                onComplete {
                    if(result != null) {
                        itemView.setBackgroundColor(getColor(itemView.context,R.color.colorGreen))
                    }else{
                        itemView.setBackgroundColor(getColor(itemView.context,R.color.colorRed))
                    }
                }
            }
            itemView.mFoodItemButton.setOnClickListener {
                val text= itemView.mFoodItemText.text.toString()
                doAsync { 
                    val result = presenter.searchItems(text)
                    uiThread {
                        //                    Progress Bar here
                    }
                    onComplete {
                        if(result != null) {
                            itemView.setBackgroundColor(getColor(itemView.context,R.color.colorGreen))
                        }else{
                            itemView.setBackgroundColor(getColor(itemView.context,R.color.colorRed))
                        }
                    }
                }
//                presenter.doAddFoodItem(foodModel)
            }
        }
    }
}