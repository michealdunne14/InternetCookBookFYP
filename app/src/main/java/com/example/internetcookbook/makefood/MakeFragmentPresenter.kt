package com.example.internetcookbook.makefood

import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class MakeFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {

    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }


    fun findIngredients(postModel: PostModel) {
        doAsync {
        infoStore!!.findIngredients(postModel)
            onComplete {
                view.ingredientsRecyclerView(infoStore!!.getIngredients())
            }
        }
    }
    fun findUser(postModel: PostModel) {
        doAsync {
            infoStore!!.findUser(postModel)
            onComplete {
                view.makeUser(infoStore!!.getMakeName())
            }
        }
    }

    fun doCurrentUser(): UserMasterModel {
        return infoStore!!.getCurrentUser()
    }
}