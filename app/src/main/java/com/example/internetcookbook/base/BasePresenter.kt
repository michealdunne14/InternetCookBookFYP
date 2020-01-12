package com.example.internetcookbook.base

import android.content.Intent
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.models.UserModel

open class BasePresenter(var view: BaseView) {
    open var app:MainApp = view.activity!!.application as MainApp

    open fun doActivityResult(requestCode: Int,resultCode:Int,data: Intent){
    }

    open fun doRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResult: IntArray){

    }

    open fun onDestrop(){

    }

//    open fun doCreateNote(fireStore: HillfortFireStore?, note: String, hillfort: HillFortModel){}
}