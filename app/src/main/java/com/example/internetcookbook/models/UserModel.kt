package com.example.internetcookbook.models

import com.google.gson.annotations.SerializedName

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
    @SerializedName("loggedIn")
    var loggedIn: Boolean = false,
    @SerializedName("posts")
    val posts: Array<PostOidModel?> = arrayOfNulls<PostOidModel>(5)
)