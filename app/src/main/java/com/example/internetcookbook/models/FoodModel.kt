package com.example.internetcookbook.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodModel(
    @SerializedName("_id")
    var oid: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("price")
    var price: Number = 0,
    @SerializedName("shop")
    var shop: String = "",
    @SerializedName("priceReliability")
    var priceReliability: Number = 0,
    @SerializedName("shopReliability")
    var shopReliability: Number = 0,
    @SerializedName("expirationTime")
    var expirationTime: Number = 0,
    @SerializedName("expirationTimeReliability")
    var expirationTimeReliability: Number = 0,
    @SerializedName("imagePath")
    var imagePath: String = "",
    @SerializedName("imagePathReliability")
    var imagePathReliability: String = ""
): Parcelable