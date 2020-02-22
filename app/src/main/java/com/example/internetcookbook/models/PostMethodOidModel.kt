package com.example.internetcookbook.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostMethodOidModel(
    @SerializedName("_id")
    val oid: String = "",
    val methodStep: String = ""
): Parcelable