package com.example.internetcookbook.base

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
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
    open fun showIngredients(listofIngredients: ArrayList<FoodModel>){}
}