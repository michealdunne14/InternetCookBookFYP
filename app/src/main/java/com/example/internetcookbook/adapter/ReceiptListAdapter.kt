package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.fragmentpresenter.CameraFragmentPresenter
import com.example.internetcookbook.helper.capitalize
import com.example.internetcookbook.helper.readBit64ImageSingle
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import kotlinx.android.synthetic.main.listeachitem.view.*
import kotlinx.android.synthetic.main.listitems.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.lang.Exception

class ReceiptListAdapter(
    private var foodItems: ArrayList<FoodMasterModel>,
    private var presenter: CameraFragmentPresenter,
    private var validFoodItems: ArrayList<FoodMasterModel>,
    private var homeView: View
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
        holder.bind(food,presenter,foodItems,validFoodItems,homeView)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(
            foodModel: FoodMasterModel,
            presenter: CameraFragmentPresenter,
            foodItems: ArrayList<FoodMasterModel>,
            validFoodItems: ArrayList<FoodMasterModel>,
            homeView: View
        ){
            itemView.mFoodItemText.setText(foodModel.food.name)

            if(foodModel.food.foundItem) {
                itemView.setBackgroundColor(getColor(itemView.context,R.color.colorGreen))
                try {
                    itemView.mFoodImage.setImageBitmap(readBit64ImageSingle(foodModel.image))
                }catch (e: Exception){
                    e.printStackTrace()
                    print(foodModel)
                }
                itemView.mFoodImage.visibility == View.VISIBLE
                itemView.mItemCounter.text = foodItems[position].food.itemsCounter.toString()
            }else{
                itemView.mFoodImage.visibility == View.GONE
                itemView.setBackgroundColor(getColor(itemView.context,R.color.colorRed))
            }

            itemView.mFoodRemoveButton.setOnClickListener {
                removeItem(foodItems, homeView, position)
            }

            itemView.mFoodUpdateButton.setOnClickListener {
                val text= itemView.mFoodItemText.text.toString()
                foodItems[position].food.name = capitalize(text)
                var foodAlreadyPresent = false
                if (validFoodItems.isNotEmpty()) {
                    for (validFood in validFoodItems) {
                        if (validFood.food.name.toUpperCase() == foodItems[position].food.name.toUpperCase()) {
                            removeItem(foodItems,homeView,position)
                            itemView.mItemCounter.text = foodItems[position].food.itemsCounter.toString()
                            foodAlreadyPresent = true
                            break
                        }
                    }
                }
                if (!foodAlreadyPresent) {
                    addNewValidItem(text, presenter, foodModel, validFoodItems)
                }
            }
        }
        fun addNewValidItem(text: String, presenter: CameraFragmentPresenter, foodModel: FoodMasterModel, validFoodItems: ArrayList<FoodMasterModel>) {
            doAsync {
//                  If food item is not in valid foodItems
                val result = presenter.searchItems(text)
                onComplete {
                    if (result!!.food.name.isNotEmpty()) {
                        itemView.setBackgroundColor(getColor(itemView.context, R.color.colorGreen))
                        itemView.mFoodImage.setImageBitmap(readBit64ImageSingle(result.image))
                        result.food.foundItem = true
                        foodModel.food.foundItem = true
                        validFoodItems.add(result)
                        itemView.mFoodImage.visibility == View.VISIBLE
                    } else {
                        itemView.setBackgroundColor(getColor(itemView.context, R.color.colorRed))
                        val action = PagerFragmentViewDirections.actionPagerFragmentToFoodCreateView(text,foodModel.food.shop)
                        itemView.findNavController().navigate(action)
                    }
                }
            }
        }

        fun removeItem(
            foodItems: ArrayList<FoodMasterModel>,
            homeView: View,
            position: Int
        ) {
            foodItems.removeAt(position)
            homeView.mFoodListRecyclerView.removeViewAt(position);
            homeView.mFoodListRecyclerView.adapter?.notifyItemRemoved(position);
            homeView.mFoodListRecyclerView.adapter?.notifyItemRangeChanged(position, foodItems.size)
        }
    }
}