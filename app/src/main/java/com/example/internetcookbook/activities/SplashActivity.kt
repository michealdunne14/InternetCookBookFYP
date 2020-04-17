package com.example.internetcookbook.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.R
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread
import java.lang.Exception

class SplashActivity : AppCompatActivity() {

    lateinit var app: MainApp
    var infoStore: InformationStore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        app = application as MainApp
        infoStore = app.informationStore as InformationStore

        doLoadData()
    }


    //  Load information before proceeding to the main application
    fun doLoadData() {
        print("Load data")
        val currentUser = infoStore!!.getCurrentUser()
        if (!currentUser.user.loggedIn) {
            startActivity(Intent(baseContext, SignInActivity::class.java))
            infoStore!!.logoutUser()
            finish()
        } else {
            doAsync {
                try {
                    infoStore!!.updateUserInfo(currentUser.user)
                } catch (e: Exception) {
                    startActivity(Intent(baseContext, SignInActivity::class.java))
                    infoStore!!.logoutUser()
                    finish()
                }
            }
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
                    startActivity(Intent(baseContext, MainView::class.java))
                    finish()
                }
            }
        }
    }
}
