package com.example.internetcookbook.foodcreate

import android.graphics.Bitmap
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class FoodCreatePresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doFindImage(): Bitmap? {
        return infoStore!!.getImage()
    }

    fun doAddFood(foodModel: FoodModel) {
        var pass = false
        doAsync {
            pass = infoStore!!.createFood(foodModel)
            onComplete {
                if (pass){
                    infoStore!!.returningBack()
                    view.naviateUp()
                }else{
                    view.detailsIncorrect()
                }
            }
        }

    }


}