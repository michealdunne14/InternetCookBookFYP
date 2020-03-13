package com.example.internetcookbook.fragmentpresenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.showImagePicker
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class CameraFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null
    val IMAGE_REQUEST = 1

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doSelectImage() {
        showImagePicker(view,IMAGE_REQUEST)
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


    fun storeImage(bitmap: Bitmap){
        return infoStore!!.storeImage(bitmap)
    }

    fun searchItems(element: String): FoodMasterModel? {
        return infoStore!!.findItem(element)
    }

    fun searchItemsInitial(storedFood: ArrayList<FoodMasterModel>) {
        return infoStore!!.searchItemsInitial(storedFood)
    }

    fun findShop(textArrayList: ArrayList<String>): FoodModel? {
        return infoStore!!.searchShop(textArrayList)
    }

    fun searchDate(element: String): String {
        return "10/11/2020"
    }

    fun doAddCupboard(validFoodItems: ArrayList<FoodMasterModel>) {
        doAsync {
            infoStore!!.cupboardAdd(validFoodItems)
            onComplete {

            }
        }
    }

    fun doGetReturnBack(): Boolean {
        return infoStore!!.findReturnBack()
    }

    fun itemsInDatabase(): ArrayList<FoodMasterModel> {
        return infoStore!!.listOfFoodArray()
    }

    fun findNewData(): FoodMasterModel {
        return infoStore!!.newFoodData()
    }

    fun doSetReturnBack(){
       infoStore!!.notReturningBack()
    }
}