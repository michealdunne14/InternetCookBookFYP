package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageModel(
    val data: MutableList<String?> = mutableListOf(),
    val postoid: String = ""
): Parcelable