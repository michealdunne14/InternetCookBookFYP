package com.example.internetcookbook

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.internetcookbook.fragmentview.CameraFragmentView
import com.example.internetcookbook.models.FoodModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class CustomDialog(var activity: FragmentActivity) : Dialog(activity), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)
        info { "Custom Dialog has started" }
    }

}