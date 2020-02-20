package com.example.internetcookbook.ingredients

import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger

class IngredientsFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {

    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null


    init {
        infoStore = app.informationStore as InformationStore
    }

    fun search(searchedItem: String){
        infoStore!!.findItem(searchedItem)
    }
}