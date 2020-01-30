package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataModel(
    val data: Array<String?> = arrayOfNulls<String>(5),
    val post: PostModel = PostModel()
) : Parcelable