package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostMethodOidModel(
    val methodStep: String = ""
): Parcelable