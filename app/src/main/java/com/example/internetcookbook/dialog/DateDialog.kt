package com.example.internetcookbook.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.internetcookbook.R
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class DateDialog(var activity: FragmentActivity) : Dialog(activity), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.date_dialog)
        info { "Date Dialog has started" }
    }

}