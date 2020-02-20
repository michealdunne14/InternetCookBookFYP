package com.example.internetcookbook.profile

import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger

class ProfileFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {

    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doGetUser(): UserMasterModel {
        return infoStore!!.getCurrentUser()
    }

    fun doGetPosts(): ArrayList<DataModel> {
        return infoStore!!.getProfileUserData()
    }
}