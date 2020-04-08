package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BasketOidModel(
    val basketoid: String = "",
    var counter: Number = 1
): Parcelable