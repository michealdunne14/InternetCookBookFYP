package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CupboardOidModel (
    val cupboardoid: String = ""
): Parcelable