package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MethodModel(
    val _id: String = "",
    val methodStep: String = ""
): Parcelable