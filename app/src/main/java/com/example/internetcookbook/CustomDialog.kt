package com.example.internetcookbook

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.internetcookbook.fragmentview.CameraFragmentView
import com.example.internetcookbook.models.FoodModel
import kotlinx.android.synthetic.main.activity_custom.*
import kotlinx.android.synthetic.main.camera_show.*
import kotlinx.android.synthetic.main.camera_show.view.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class CustomDialog(var activity: FragmentActivity) : Dialog(activity), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)
        info { "Custom Dialog has started" }
    }

}