package com.example.internetcookbook.post

import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class PostFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doPostRecipe(postModel: PostModel) {
        doAsync {
            infoStore!!.createPost(postModel)
            onComplete {
                infoStore!!.putPostToUser(postModel)
            }
        }
    }
}