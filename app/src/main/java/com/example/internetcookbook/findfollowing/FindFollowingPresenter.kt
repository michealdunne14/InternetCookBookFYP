package com.example.internetcookbook.findfollowing

import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class FindFollowingPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }


    fun doFindUser(userSearch: String) {
        var searchFollowing: UserMasterModel?
        doAsync {
            searchFollowing =  infoStore!!.searchForFollowing(userSearch)
            onComplete {
                
            }
        }
    }
}