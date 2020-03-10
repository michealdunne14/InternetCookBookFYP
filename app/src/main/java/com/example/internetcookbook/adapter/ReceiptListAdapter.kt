package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.fragmentpresenter.CameraFragmentPresenter
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import kotlinx.android.synthetic.main.listeachitem.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class ReceiptListAdapter(
    private var foodItems: List<FoodModel>,
    private var presenter: CameraFragmentPresenter,
    private var validFoodItems: ArrayList<FoodMasterModel>
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
        holder.bind(food,presenter,foodItems,validFoodItems)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(
            foodModel: FoodModel,
            presenter: CameraFragmentPresenter,
            foodItems: List<FoodModel>,
            validFoodItems: ArrayList<FoodMasterModel>
        ){
            itemView.mFoodItemText.setText(foodModel.name)
            doAsync {
                val result = presenter.searchItems(foodModel.name)
                onComplete {
                    if(result!!.food.name.isNotEmpty()) {
                        itemView.setBackgroundColor(getColor(itemView.context,R.color.colorGreen))
                        validFoodItems.add(result)
                    }else{
                        itemView.setBackgroundColor(getColor(itemView.context,R.color.colorRed))
                    }
                }
            }
            itemView.mFoodItemButton.setOnClickListener {
                val text= itemView.mFoodItemText.text.toString()
                foodItems[position].name = text
                doAsync { 
                    val result = presenter.searchItems(text)
                    onComplete {
                        if(result!!.food.name.isNotEmpty()) {
                            itemView.setBackgroundColor(getColor(itemView.context,R.color.colorGreen))
                            validFoodItems.add(result)
                        }else{
                            itemView.setBackgroundColor(getColor(itemView.context,R.color.colorRed))
                            val action = PagerFragmentViewDirections.actionPagerFragmentToFoodCreateView(text)
                            itemView.findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }
}