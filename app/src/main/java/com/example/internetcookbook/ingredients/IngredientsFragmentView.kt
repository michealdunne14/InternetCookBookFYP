package com.example.internetcookbook.ingredients

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.base.BaseView
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


        view.mIngredientsRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?


//        foodModelArrayList.add(FoodModel("Food"))
//        foodModelArrayList.add(FoodModel("Food"))

        view.mSearchIngredients.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("DefaultLocale")
            override fun onTextChanged(
                characterSearch: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                info { "Text Changed to $characterSearch"}
//                val searchedLandmarks = presenter.search(characterSearch.toString().toUpperCase())
//                view.mIngredientsRecyclerView.adapter = IngredientsAdapter(searchedLandmarks)
//                view.mIngredientsRecyclerView.adapter?.notifyDataSetChanged()
            }

        })

        return view
    }

}
