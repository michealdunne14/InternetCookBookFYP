package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataModel(
    val data: MutableList<String?> = mutableListOf(),
    val post: PostModel = PostModel()
) : Parcelable