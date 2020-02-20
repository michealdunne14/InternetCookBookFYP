package com.example.internetcookbook.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    @SerializedName("_id")
    var oid: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("password")
    var password: String = "",
    @SerializedName("username")
    var username: String = "",
    @SerializedName("signupdate")
    var signupdate: String = "",
    @SerializedName("active")
    var loggedIn: Boolean = false,
    @SerializedName("profilePicture")
    var profilePicture: String = "",
    @SerializedName("posts")
    val posts: MutableList<PostOidModel?> = mutableListOf(),
    @SerializedName("followers")
    val followers: MutableList<FollowersOidModel> = mutableListOf(),
    @SerializedName("following")
    val following: MutableList<FollowingOidModel> = mutableListOf(),
    @SerializedName("basket")
    val basket: MutableList<BasketOidModel> = mutableListOf(),
    @SerializedName("cupboard")
    val cupboard: MutableList<CupboardOidModel> = mutableListOf()
): Parcelable