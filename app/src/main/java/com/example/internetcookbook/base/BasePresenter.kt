package com.example.internetcookbook.base

import android.content.Context
import android.content.Intent
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.models.DataModel

open class BasePresenter(var view: BaseView) {
    open var app:MainApp = view.activity!!.application as MainApp

    open fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent?, context: Context){}

    open fun doRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResult: IntArray){

    }

    open fun onDestrop(){

    }

    open fun doHeartData(id: String){}
    open fun doCurrentUser(id: String){}
    open fun doSendComment(
        comment: String,
        dataModel: DataModel
    ) {}

//    open fun doCreateNote(fireStore: HillfortFireStore?, note: String, hillfort: HillFortModel){}
}