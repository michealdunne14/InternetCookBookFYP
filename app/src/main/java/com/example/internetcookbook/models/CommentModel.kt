package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentModel(
    var commentString: String = "",
    var userOid: String = ""
): Parcelable