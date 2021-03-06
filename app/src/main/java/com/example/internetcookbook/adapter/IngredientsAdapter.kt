package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.dialog.NumberDialog
import com.example.internetcookbook.helper.readBit64ImageSingle
import com.example.internetcookbook.item.ItemFragmentPresenter
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.UserMasterModel
import kotlinx.android.synthetic.main.ingredients_list.view.*
import kotlinx.android.synthetic.main.number_change_dialog.*

class IngredientsAdapter(
    private var food: ArrayList<FoodMasterModel>,
    private var currentUser: UserMasterModel,
    private var selectedOption: String,
    private var presenter: BasePresenter,
    private var activity: FragmentActivity?
) : RecyclerView.Adapter<IngredientsAdapter.MainHolder>() {


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
        holder.bind(postModel,currentUser,selectedOption,presenter,activity)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            foodModel: FoodMasterModel,
            currentUser: UserMasterModel,
            selectedOption: String,
            presenter: BasePresenter,
            activity: FragmentActivity?
        ) {
//              Change buttons depending on the page
            when (selectedOption) {
                "basket" -> {
                    itemView.mSelectedIngredient.setImageResource(R.drawable.baseline_remove_black_36)
                    for (basket in currentUser.user.basket) {
                        if (basket.basketoid == foodModel.food.oid) {
                            itemView.mIngredientCounter.text = basket.counter.toString()
                        }
                    }
                    itemView.mSelectedIngredient.setOnClickListener {
                        val itemPresenter = presenter as ItemFragmentPresenter
                        itemPresenter.doRemoveItemBasket(foodModel)
                    }
                    itemView.mIngredientCounter.setOnClickListener {
                        showNumberDialog(
                            activity,
                            presenter,
                            foodModel,
                            currentUser,
                            selectedOption
                        )
                    }
                }
                "cupboard" -> {
                    itemView.mSelectedIngredient.setImageResource(R.drawable.baseline_remove_black_36)
                    for (cupboard in currentUser.user.cupboard) {
                        if (cupboard.cupboardoid == foodModel.food.oid) {
                            itemView.mIngredientCounter.text =
                                cupboard.foodPurchasedCounter.toString()
                        }
                    }
                    itemView.mSelectedIngredient.setOnClickListener {
                        val itemPresenter = presenter as ItemFragmentPresenter
                        itemPresenter.doRemoveItemCupboard(foodModel)
                    }

                    itemView.mIngredientCounter.setOnClickListener {
                        showNumberDialog(
                            activity,
                            presenter,
                            foodModel,
                            currentUser,
                            selectedOption
                        )
                    }
                }
                "post" -> {
                    itemView.mSelectedIngredient.visibility = View.INVISIBLE
                    itemView.mIngredientCounter.visibility = View.INVISIBLE
                }
                "make" -> {
                    itemView.mSelectedIngredient.visibility = View.INVISIBLE
                    itemView.mIngredientCounter.visibility = View.INVISIBLE
                }
            }
            itemView.mIngredientsName.text = foodModel.food.name
            val bitmapImage = readBit64ImageSingle(foodModel.image)
            itemView.mFoodPicture.setImageBitmap(bitmapImage)
        }


        //          number dialog for changing the amount of items in the basket/cupboard
        private fun showNumberDialog(
            activity: FragmentActivity?,
            presenter: BasePresenter,
            foodModel: FoodMasterModel,
            currentUser: UserMasterModel,
            selectedOption: String
        ) {
            val numberDialog = NumberDialog(activity!!)
            numberDialog.show()
            numberDialog.setCanceledOnTouchOutside(false)
            numberDialog.mConfirm.setOnClickListener {
                if (selectedOption == "cupboard") {
                    val itemPresenter = presenter as ItemFragmentPresenter
                    for (cupboard in itemPresenter.doFindCupboard()) {
                        if (cupboard.food.oid == foodModel.food.oid) {
                            for (userCupboard in currentUser.user.cupboard) {
                                val number = numberDialog.mDialogSearch.text.toString().toInt()
                                userCupboard.foodPurchasedCounter = number
                                cupboard.food.itemsCounter = number
                                itemPresenter.doUpdateItemCounterCupboard(number, foodModel)
                                itemView.mIngredientCounter.text =
                                    userCupboard.foodPurchasedCounter.toString()
                            }
                        }
                    }
                } else {
                    val itemPresenter = presenter as ItemFragmentPresenter
                    for (basket in itemPresenter.doFindBasket()) {
                        if (basket.food.oid == foodModel.food.oid) {
                            for (userBasket in currentUser.user.basket) {
                                if (userBasket.basketoid == foodModel.food.oid) {
                                    val number = numberDialog.mDialogSearch.text.toString().toInt()
                                    userBasket.counter = number
                                    basket.food.itemsCounter = number
                                    itemPresenter.doUpdateItemCounterBasket(number, foodModel)
                                    itemView.mIngredientCounter.text = userBasket.counter.toString()
                                }
                            }
                        }
                    }
                    numberDialog.cancel()
                }
            }
        }
    }
}