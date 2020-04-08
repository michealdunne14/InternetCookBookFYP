package com.example.internetcookbook.ingredients

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.IngredientsAddAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodMasterModel
import kotlinx.android.synthetic.main.fragment_ingredients.view.*
import org.jetbrains.anko.info

class IngredientsFragmentView : BaseView() {

    lateinit var presenter: IngredientsFragmentPresenter

    lateinit var ingredientsView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = initPresenter(IngredientsFragmentPresenter(this)) as IngredientsFragmentPresenter

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_ingredients, container, false)
        ingredientsView = view
        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)
        view.mIngredientsSearchRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?

        val layoutManagerRecipe = LinearLayoutManager(context)
        view.mIngredientsListRecyclerView.layoutManager = layoutManagerRecipe as RecyclerView.LayoutManager?

        presenter.defaultIngredients()

        view.mReturnButtonIngredients.setOnClickListener {
            val ingredients = IngredientsFragmentViewArgs.fromBundle(arguments!!).ingredients
            if (ingredients == "home_page"){
                presenter.doIngredientsSearch(ingredientsView)
            }else {
                ingredientsView.findNavController().navigateUp()
            }
        }

        view.mSearchIngredients.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(
                characterSearch: CharSequence,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                info { "Text Changed to $characterSearch"}
                if (characterSearch.length == 3) {
                    presenter.searchIngredients(characterSearch.toString())
                }else if (characterSearch.length > 3){
                    presenter.searchIngredientsResult(characterSearch.toString())
                }else{
                    presenter.defaultIngredients()
                }
            }

        })

        return view
    }

    override fun ingredientsRecyclerView(searchedIngredients: ArrayList<FoodMasterModel>) {
        ingredientsView.mIngredientsSearchRecyclerView.adapter = IngredientsAddAdapter(searchedIngredients,presenter,  true,false,ingredientsView)
        ingredientsView.mIngredientsSearchRecyclerView.adapter?.notifyDataSetChanged()
        ingredientsView.mIngredientsListRecyclerView.adapter = IngredientsAddAdapter(presenter.ingredientsAddToRecipe(), presenter, false, true, ingredientsView)
        ingredientsView.mIngredientsListRecyclerView.adapter?.notifyDataSetChanged()
    }

}
