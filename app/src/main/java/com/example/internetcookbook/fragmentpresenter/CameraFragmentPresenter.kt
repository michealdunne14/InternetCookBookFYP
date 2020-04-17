package com.example.internetcookbook.fragmentpresenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentview.storedFood
import com.example.internetcookbook.fragmentview.validFoodItems
import com.example.internetcookbook.helper.capitalize
import com.example.internetcookbook.helper.showImagePicker
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.network.InformationStore
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.vision.text.FirebaseVisionText
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

var saveShop = ""
var saveDate = ""

class CameraFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null
    val IMAGE_REQUEST = 1
    var date = String()

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
        return infoStore!!.findItem(element,saveShop)
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
        return if (date != null){
            finalDate
        }else{
            null
        }
    }

    fun doExpirationYes(oid: String) {
        doAsync {
            infoStore!!.putExpireYes(oid)
        }
    }

    fun doExpireNo(oid: String) {
        doAsync {
            infoStore!!.putExpireNo(oid)
        }
    }

    fun doImageYes(oid: String) {
        doAsync {
            infoStore!!.putImageYes(oid)
        }
    }

    fun doImageNo(oid: String) {
        doAsync {
            infoStore!!.putImageNo(oid)
        }
    }


    fun doPriceYes(oid: String) {
        doAsync {
            infoStore!!.putPriceYes(oid)
        }
    }

    fun doPriceNo(oid: String) {
        doAsync {
            infoStore!!.putPriceNo(oid)
        }
    }

    fun doShopYes(oid: String) {
        doAsync {
            infoStore!!.putShopNo(oid)
        }
    }

    fun doShopNo(oid: String) {
        doAsync {
            infoStore!!.putShopYes(oid)
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

    fun cameraSearch(
        elementArrayList: ArrayList<String>,
        lineArrayList: ArrayList<String>,
        filteredArrayList: ArrayList<String>,
        resultText: FirebaseVisionText
    ) {
//      add text to arrays line and elements
        doAsync {
            for (block in resultText.textBlocks) {
                for (line in block.lines) {
                    lineArrayList.add(line.text.trim())
                    for (element in line.elements) {
                        elementArrayList.add(capitalize(element.text.trim()))
                    }
                }
            }
            onComplete {
                date = ""
                if (elementArrayList.size != 0) {
                    view.hideCamera()
                    val foundDate = findDate(elementArrayList)
                    if (foundDate!!.isNotEmpty()) {
                        if (saveDate.isEmpty()) {
                            date = foundDate
                            saveDate = foundDate
                        }
                        findShop(elementArrayList, filteredArrayList)
                    } else {
                        view.showDateDialog(date)
                    }
                    for (line in lineArrayList) {
                        filterLineData(line, filteredArrayList)
                    }
                }else{
                    view.noResults()
                }
            }
        }
    }

    fun findShop(
        elementArrayList: ArrayList<String>,
        filteredArrayList: ArrayList<String>
    ) {
        doAsync {
            val foundShop = findShop(elementArrayList)
            onComplete {
                var foodModel = FoodMasterModel()
                if(foundShop != null) {
                    for (foodItem in filteredArrayList) {
                        foodModel = FoodMasterModel()
                        for (item in filteredArrayList){
                            if(foodItem == item){
                                foodModel.food.itemsCounter++
                            }
                        }
                        foodModel.food.name = foodItem
                        foodModel.food.shop = foundShop.shop
                        foodModel.food.purchaseDate = date
                        if (!storedFood.contains(foodModel)) {
                            storedFood.add(foodModel)
                        }
                    }
                    saveShop = foundShop.shop
                    saveDate = date
                    findFoodItems()
                }else{
                    view.showShopDialog()
                }
            }
        }
    }


//  Filter out not wanted information on the receipt
    fun filterLineData(
        line: String,
        filteredArrayList: ArrayList<String>
    ) {
        val wordArrayList = ArrayList<String>()
        val wordList = line.split(" ")
        val removingWords = ArrayList<String>()
        wordArrayList.addAll(wordList)
//      Remove words where there is a number
        wordArrayList.forEachIndexed { index, word ->
            val chars: CharArray = word.toCharArray()
            for (c in chars) {
                if (Character.isDigit(c)) {
                    removingWords.add(word)
                    break
                }
            }
        }
        for (remove in removingWords){
            wordArrayList.remove(remove)
        }
        var foodItem = String()
//      Join line back together with words that don't have numbers
        wordArrayList.forEachIndexed { index, word ->
            val capWord = capitalize(word)
            if(index == 0){
                foodItem += capWord
            }else {
                foodItem += " $capWord"
            }
        }

        filteredArrayList.add(foodItem)
    }

    fun findFoodItems(){
//      Search for text on the receipt in the database
        doAsync {
            searchItemsInitial(storedFood)
            onComplete {
                if(itemsInDatabase().isNotEmpty()) {
                    storedFood.forEachIndexed { index, foundFood ->
                        val search: FoodMasterModel? = itemsInDatabase().find { p -> p.food.name == foundFood.food.name }
                        if (search != null) {
                            if (search.food.name.isNotEmpty()) {
                                search.food.itemsCounter = foundFood.food.itemsCounter

                                validFoodItems.add(search)

                                storedFood[index] = search
                                storedFood[index].food.foundItem = true
                            }
                        }
                    }
                }
                view.showFoodItems()
            }
        }
    }


    fun doFoodCreatePageUpdate(){
        infoStore!!.foodCreatePageUpdate()
    }

    fun itemsInDatabase(): ArrayList<FoodMasterModel> {
        return infoStore!!.listOfFoodArray()
    }
}