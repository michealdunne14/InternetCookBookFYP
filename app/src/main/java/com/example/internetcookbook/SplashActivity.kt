package com.example.internetcookbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val background = object : Thread() {
            override fun run() {
                try {
                    sleep(500)
//                    val action = SplashFragmentDirections.actionSplashFragmentToPagerFragment()
//                    findNavController().navigate(action)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }
}
