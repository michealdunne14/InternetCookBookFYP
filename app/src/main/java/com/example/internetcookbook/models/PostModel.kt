package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostModel(var title: String = "",
                     var description: String = "",
                     var data: String = "",
                     var useroid: String = "") : Parcelable