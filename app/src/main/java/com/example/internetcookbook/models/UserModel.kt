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
    val posts: Array<PostOidModel?> = arrayOfNulls<PostOidModel>(5),
    @SerializedName("followers")
    val followers: Array<FollowersOidModel?> = arrayOfNulls<FollowersOidModel>(5),
    @SerializedName("following")
    val following: Array<FollowingOidModel?> = arrayOfNulls<FollowingOidModel>(5),
    @SerializedName("basket")
    val basket: Array<BasketOidModel?> = arrayOfNulls<BasketOidModel>(5),
    @SerializedName("cupboard")
    val cupboard: Array<CupboardOidModel?> = arrayOfNulls<CupboardOidModel>(5)
): Parcelable