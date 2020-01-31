package com.example.internetcookbook.base

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.PostModel
import org.jetbrains.anko.AnkoLogger

open class BaseView: Fragment(), AnkoLogger {
    lateinit var baseFragmentPresenter: BasePresenter

    fun initPresenter(presenter: BasePresenter): BasePresenter {
        baseFragmentPresenter = presenter
        return presenter
    }

    open fun showHillforts(post: List<PostModel>) {}

    open fun showProgress(){}
    open fun hideProgress(){}
    open fun getMainPageFromLoginPage(){}
    open fun detailsIncorrect(){}
    open fun passwordIncorrect(){}
    open fun addImages(listofImages: ArrayList<String>) {}
    open fun addImageToCamera(stringData: String) {}
    open fun setProfileImage(image: Bitmap?){}
    open fun showIngredients(listofIngredients: ArrayList<FoodModel>){}
    open fun showInformation(homeData: ArrayList<DataModel>) {}
}