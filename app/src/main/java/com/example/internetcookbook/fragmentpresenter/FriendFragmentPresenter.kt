package com.example.internetcookbook.fragmentpresenter

import androidx.navigation.fragment.findNavController
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.network.InformationStore

class FriendFragmentPresenter(view: BaseView): BasePresenter(view) {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun getCurrentUser(): UserModel{
        return infoStore!!.getCurrentUser()
    }
}