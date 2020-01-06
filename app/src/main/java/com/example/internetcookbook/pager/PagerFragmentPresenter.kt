package com.example.internetcookbook.pager

import androidx.navigation.fragment.findNavController
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.network.InformationStore

class PagerFragmentPresenter(view: BaseView): BasePresenter(view) {
    var app : MainApp = view.activity?.application as MainApp
    val loggedIn = false
    lateinit var infoStore: InformationStore


    init {
        if(!loggedIn){
            val action = PagerFragmentViewDirections.actionPagerFragmentToStartFragment()
            view.findNavController().navigate(action)
        }else{
            infoStore = app.informationStore as InformationStore
//            user = infoStore!!.currentUser()
        }
    }
}