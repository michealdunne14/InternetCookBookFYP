package com.example.internetcookbook.pager

import androidx.navigation.fragment.findNavController
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class PagerFragmentPresenter(view: BaseView): BasePresenter(view) {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore

    init {
        infoStore = app.informationStore
    }
}