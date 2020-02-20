package com.example.internetcookbook

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController


class SplashFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = object : Thread() {
            override fun run() {
                try {
                    sleep(500)
                    val action = SplashFragmentDirections.actionSplashFragmentToPagerFragment()
                    findNavController().navigate(action)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}
