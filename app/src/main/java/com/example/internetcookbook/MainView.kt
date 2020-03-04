package com.example.internetcookbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync

class MainView : AppCompatActivity(), LifecycleOwner,AnkoLogger {

    lateinit var app: MainApp
    var infoStore: InformationStore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = application as MainApp
        infoStore = app.informationStore as InformationStore
        doAsync {
            val currentUser = infoStore!!.getCurrentUser()
            infoStore!!.updateUserInfo(currentUser.user)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

}
