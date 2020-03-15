package com.example.internetcookbook.fragmentpresenter

import android.annotation.SuppressLint
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
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

    @SuppressLint("SimpleDateFormat")
    fun findDate(elementArrayList: ArrayList<String>): String? {
        var date: Date? = Date()
        var finalDate = String()
        for (element in elementArrayList){
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yy")
            dateFormat.isLenient = false
            try {
                date = dateFormat.parse(element)
                finalDate = dateFormat.format(date!!).toString()
                break
            } catch (ex: ParseException) {
                print("Invalid Data keep searching ...")
            }

        }
        if (date != null){
            return finalDate
        }else{
            return null
        }
    }

    fun doAddCupboard(validFoodItems: ArrayList<FoodMasterModel>) {
        doAsync {
            infoStore!!.cupboardAdd(validFoodItems)
            onComplete {
                view.resetInformation()
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