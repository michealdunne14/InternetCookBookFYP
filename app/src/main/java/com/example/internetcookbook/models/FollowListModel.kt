package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FollowListModel (
    val userArray: MutableList<UserMasterModel> = mutableListOf()
): Parcelable