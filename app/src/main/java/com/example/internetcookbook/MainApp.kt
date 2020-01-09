package com.example.internetcookbook

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    lateinit var informationStore: InformationStore

    override fun onCreate() {
        super.onCreate()
        if(isNetworkAvailable()) {
            informationStore = InformationStore(applicationContext, true)
        }else{
            informationStore = InformationStore(applicationContext, false)
        }
        info { "App has Started" }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}