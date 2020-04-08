package com.example.internetcookbook.login

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.activities.SignInActivity
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread

class LoginFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {

    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doSignIn(userModel: UserModel){
        var signIn: UserMasterModel?
        doAsync {
            signIn = infoStore!!.findEmail(userModel)
            uiThread {
                view.showProgress()
            }
            onComplete {
                if (signIn != null) {
                    if (signIn!!.user.email.isNotEmpty()) {
                        doAsync {
                            infoStore!!.getCupboardData()
                        }
                        doAsync {
                            infoStore!!.getFollowingData()
                        }
                        doAsync {
                            infoStore!!.getBasketData()
                        }
                        doAsync {
                            infoStore!!.getPostData()
                            onComplete {
                                view.getMainPageFromLoginPage()
                            }
                        }
                    } else {
                        view.detailsIncorrect()
                    }
                }else {
                    view.detailsIncorrect()
                }
                view.hideProgress()
            }
        }
    }
}