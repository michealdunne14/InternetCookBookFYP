package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageModel(
    val data: Array<String?> = arrayOfNulls<String>(100),
    val postoid: String = ""
): Parcelable