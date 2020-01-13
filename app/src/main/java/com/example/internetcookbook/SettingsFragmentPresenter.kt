package com.example.internetcookbook

import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger

class SettingsFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doLogout(){
        infoStore!!.logoutUser()
    }
}