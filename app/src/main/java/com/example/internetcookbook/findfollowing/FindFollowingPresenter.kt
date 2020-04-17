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

//  Find users following
    fun doFindFollowing(userSearch: CharSequence) {
        var searchFollowing: ArrayList<UserMasterModel>
        doAsync {
            searchFollowing = infoStore!!.searchForFollowing(userSearch)!!
            onComplete {
                view.showFollowList(searchFollowing)
            }
        }
    }

//  Get the users following
    fun followingUsers(): ArrayList<UserMasterModel> {
        return infoStore!!.findFollowingData()
    }

    fun searchUsers(characterSearch: CharSequence) {
        val searched=  infoStore!!.searchFollowingSearched(characterSearch)
        view.showFollowList(searched)
    }

    fun followUser(userMasterModel: UserMasterModel) {
        doAsync {
            infoStore!!.followUser(userMasterModel)
        }
    }

    fun unfollowUser(userMasterModel: UserMasterModel){
        doAsync {
            infoStore!!.unFollowUser(userMasterModel)
        }
    }
}