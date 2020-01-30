package com.example.internetcookbook.fragmentpresenter

import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class HomeFragPresenter(view: BaseView): BasePresenter(view), AnkoLogger {

    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
        doAsync {
        infoStore!!.getPostData()
            onComplete {
                doFindHomeData()
            }
        }
    }

    fun doFindHomeData(): ArrayList<DataModel> {
        return infoStore!!.getHomeData()
    }
}