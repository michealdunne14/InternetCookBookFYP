package com.example.internetcookbook.register

import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread

class RegisterFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doRegister(userModel: UserModel): String? {
        var usercreated: String? = String()
        doAsync {
            usercreated = infoStore!!.createUser(userModel)
            onComplete {
                infoStore!!.userCreated()
            }
        }
        return usercreated
    }
}