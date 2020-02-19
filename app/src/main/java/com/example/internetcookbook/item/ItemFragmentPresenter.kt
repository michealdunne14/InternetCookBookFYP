package com.example.internetcookbook.item

import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync

class ItemFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }


    fun doFindFollowers(): ArrayList<UserMasterModel> {
        return infoStore!!.findFollowingData()
    }

    fun doFindBasket(): ArrayList<FoodMasterModel> {
        return infoStore!!.findBasketData()
    }

    fun doFindCupboard(): ArrayList<FoodMasterModel> {
        return infoStore!!.findCupboardData()
    }

}