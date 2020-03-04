package com.example.internetcookbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread

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


    fun doLoadData(){
        val currentUser = infoStore!!.getCurrentUser()
        if(!currentUser.user.loggedIn){
            startActivity(Intent(baseContext, SignInActivity::class.java))
            finish()
        }else{
            doAsync {
                infoStore!!.getPostData()
                uiThread {
                    startActivity(Intent(baseContext, MainView::class.java))
                    finish()
                }
            }

            doAsync {
                infoStore!!.getFollowingData()
            }

            doAsync {
                infoStore!!.getCupboardData()
            }
            doAsync {
                infoStore!!.getBasketData()
            }
        }
    }
}
