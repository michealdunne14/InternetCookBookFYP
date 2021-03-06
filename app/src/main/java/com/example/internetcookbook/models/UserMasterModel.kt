package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserMasterModel(
    val image: String = "",
    var user: UserModel = UserModel()
) : Parcelable