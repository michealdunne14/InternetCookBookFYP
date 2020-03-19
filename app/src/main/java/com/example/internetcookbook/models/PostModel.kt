package com.example.internetcookbook.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostModel(
    var title: String = "",
    var description: String = "",
    var _id: String = "",
    var imagePath: String = "",
    var useroid: String = "",
    var comments: MutableList<CommentModel?> = mutableListOf(),
    var method: MutableList<MethodModel> = mutableListOf(),
    var ingredients: MutableList<String> = mutableListOf(),
    var posttime: String = ""
) : Parcelable