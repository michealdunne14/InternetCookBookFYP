package com.example.internetcookbook.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CupboardOidModel (
    @SerializedName("cupboardoid")
    val cupboardoid: String = "",
    @SerializedName("foodPurchasedCounter")
    var foodPurchasedCounter: Number = 0
): Parcelable