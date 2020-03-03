package com.example.internetcookbook.splash

import androidx.navigation.fragment.findNavController
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.network.InformationStore
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread

class SplashFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app: MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doLoadData(){
        val currentUser = infoStore!!.getCurrentUser()
        if(!currentUser.user.loggedIn){
            val action = SplashFragmentViewDirections.actionSplashFragmentToStartFragment()
            view.findNavController().navigate(action)
        }else{
            doAsync {
                infoStore!!.updateUserInfo(currentUser.user)
                onComplete {
                    doAsync {
                        infoStore!!.getFollowingData()
                    }

                    doAsync {
                        infoStore!!.getCupboardData()
                    }
                    doAsync {
                        infoStore!!.getBasketData()
                    }

                    doAsync {
                        infoStore!!.getPostData()
                        uiThread {
                            val action = SplashFragmentViewDirections.actionSplashFragmentToPagerFragment()
                            view.findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }
}