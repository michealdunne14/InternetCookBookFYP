package com.example.internetcookbook.ingredients

import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class IngredientsFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {

    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null


    init {
        infoStore = app.informationStore as InformationStore
    }

    fun searchIngredients(searchedItem: String){
        var searchedIngredients = ArrayList<FoodMasterModel>()
        doAsync {
            searchedIngredients = infoStore!!.findFoodIngredients(searchedItem)!!
            onComplete {
                view.ingredientsRecyclerView(searchedIngredients)
            }
        }
    }

    fun searchIngredientsResult(searchedItem: String){
        val searchedIngredients =  infoStore!!.searchIngredients(searchedItem)
        view.ingredientsRecyclerView(searchedIngredients)
    }

    fun defaultIngredients(){
        val defaultIngredients =  infoStore!!.defaultIngredients()
        view.ingredientsRecyclerView(defaultIngredients)
    }

    fun ingredientsAddToRecipe(): ArrayList<FoodMasterModel> {
        return infoStore!!.ingredientsAddedToRecipe()
    }

    fun doIngredientsSearch(ingredientsView: View) {
        if (infoStore!!.ingredientsAddedToRecipe().size != 0) {
            doAsync {
                infoStore!!.ingredientsSearchHomeData()
                onComplete {
                    ingredientsView.findNavController().navigateUp()
                }
            }
        } else {
            infoStore!!.getFilteredData().clear()
            ingredientsView.findNavController().navigateUp()
        }
    }

    fun doAddBasket(ingredientsView: View) {
        if (infoStore!!.ingredientsAddedToRecipe().size != 0) {
            doAsync {
                infoStore!!.basketAddManual()
                onComplete {
                    ingredientsView.findNavController().navigateUp()
                    Toast.makeText(ingredientsView.context,"Add to basket", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            infoStore!!.getFilteredData().clear()
            ingredientsView.findNavController().navigateUp()
        }
    }

    fun doAddCupboard(ingredientsView: View){
        if (infoStore!!.ingredientsAddedToRecipe().size != 0) {
            doAsync {
                infoStore!!.cupboardAddManual()
                onComplete {
                    ingredientsView.findNavController().navigateUp()
                    Toast.makeText(ingredientsView.context,"Add to Cupboard", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            infoStore!!.getFilteredData().clear()
            ingredientsView.findNavController().navigateUp()
        }
    }
}