package com.example.internetcookbook.fragmentpresenter

import android.content.Context
import android.content.Intent
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.showImagePicker
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync

class CameraFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null
    val IMAGE_REQUEST = 1

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doSelectImage(fragment: BaseView) {
        showImagePicker(fragment,IMAGE_REQUEST)
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent?, context: Context) {
        when(requestCode){
            IMAGE_REQUEST -> {
                if (data != null){
                    val stringData = data.data.toString()
                    view.addImageToCamera(stringData)
                }
            }
        }
    }

    fun searchShop(element: String): String? {
        return infoStore!!.findShop(element)
    }

    fun searchItems(element: String): String? {
        return infoStore!!.findItem(element)
    }

    fun doAddFoodItem(foodModel: FoodModel) {
        doAsync {
            infoStore!!.createFood(foodModel)
        }
    }
}