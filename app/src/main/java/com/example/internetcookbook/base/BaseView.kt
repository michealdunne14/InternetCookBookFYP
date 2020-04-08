package com.example.internetcookbook.base

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.example.internetcookbook.models.*
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
    open fun showBasket(listofBasket: ArrayList<FoodMasterModel>){}
    open fun showFollowers(userMasterModel: ArrayList<UserMasterModel>){}
    open fun showCupboard(listofCupboard: ArrayList<FoodMasterModel>){}
    open fun showInformation(homeData: ArrayList<DataModel?>) {}
    open fun returnToPager(){}
    open fun removeLoading(findData: ArrayList<DataModel?>) {}
    open fun setRegisterResponse(usercreated: Boolean) {}
    open fun naviateUp(){}
    open fun resetInformation(){}
    open fun showFollowList(searchFollowing: ArrayList<UserMasterModel>) {}
    open fun ingredientsRecyclerView(searchedIngredients: ArrayList<FoodMasterModel>) {}
    open fun noDataAvilable(){}
    open fun showDateDialog(date: String) {}
    open fun showShopDialog(){}
    open fun showFoodItems(){}
    open fun commentAdded(){}
    open fun notifyDataSetChanged(){}
    open fun ingredientsRecyclerView(){}

}