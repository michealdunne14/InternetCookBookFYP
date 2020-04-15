package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.helper.readBit64ImageSingle
import com.example.internetcookbook.ingredients.IngredientsFragmentPresenter
import com.example.internetcookbook.models.FoodMasterModel
import kotlinx.android.synthetic.main.fragment_ingredients.view.*
import kotlinx.android.synthetic.main.ingredients_list.view.*

class IngredientsAddAdapter(
    private var food: ArrayList<FoodMasterModel>,
    private var presenter: IngredientsFragmentPresenter,
    private var ingredientsFragmentSearch: Boolean,
    private var ingredientsFragmentList: Boolean,
    private var ingredientsView: View
) : RecyclerView.Adapter<IngredientsAddAdapter.MainHolder>() {


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
        holder.bind(postModel, presenter,ingredientsFragmentSearch,ingredientsFragmentList,ingredientsView)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(
            foodModel: FoodMasterModel,
            presenter: IngredientsFragmentPresenter,
            ingredientsFragment: Boolean,
            ingredientsFragmentList: Boolean,
            ingredientsView: View
        ) {
            itemView.mIngredientsName.text = foodModel.food.name
            val bitmapImage = readBit64ImageSingle(foodModel.image)
            itemView.mFoodPicture.setImageBitmap(bitmapImage)
            if (ingredientsFragmentList){
                itemView.mSelectedIngredient.setImageResource(R.drawable.baseline_remove_black_36)
            }

            itemView.mIngredientCounter.visibility = View.INVISIBLE

            itemView.mSelectedIngredient.setOnClickListener {
                if (ingredientsFragment){
                    presenter.ingredientsAddToRecipe().add(foodModel)
                    ingredientsView.mIngredientsListRecyclerView.adapter?.notifyDataSetChanged()
                }
                if (ingredientsFragmentList){
                    presenter.ingredientsAddToRecipe().remove(foodModel)
                    ingredientsView.mIngredientsListRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }


}