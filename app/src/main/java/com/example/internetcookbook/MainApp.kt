package com.example.internetcookbook

import android.app.Application
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    lateinit var informationStore: InformationStore

    override fun onCreate() {
        super.onCreate()
        informationStore = InformationStore(applicationContext)
        info { "App has Started" }
    }
}